# Compte rendu - Digital Banking

Yassine Najmi

## Architecture

Le projet est un monorepo organisé en deux parties :

- `backend/` : API Spring Boot 3 (Java 17, Spring Data JPA, MySQL). La documentation OpenAPI est exposée via springdoc.
- `frontend/` : application Angular qui consomme l'API.

La sécurité repose sur JWT (à ajouter dans les prochaines étapes). Un chatbot RAG est prévu pour interroger les données bancaires en langage naturel.

Au démarrage local, MySQL 8 tourne dans Docker (`docker compose up -d`, port 3307, base `ebank-db`). Le backend écoute sur le port 8085.

## Couche DAO

### Modèle

Le domaine bancaire se compose de :

- `Customer` : client, lié à plusieurs comptes (`OneToMany`)
- `BankAccount` : compte abstrait (solde, date, statut, client, opérations)
- `CurrentAccount` / `SavingAccount` : spécialisations (découvert / taux d'intérêt)
- `AccountOperation` : opération de débit ou crédit sur un compte

Relations principales : Customer 1..* BankAccount, BankAccount 1..* AccountOperation. Les enums `AccountStatus` et `OperationType` sont dans le package `enums`.

[À remplacer : capture du diagramme de classes]

### Stratégie d'héritage SINGLE_TABLE

JPA propose trois stratégies pour l'héritage :

- **SINGLE_TABLE** : une seule table pour toute la hiérarchie, avec une colonne discriminante
- **JOINED** : une table par classe, jointures pour lire une sous-classe
- **TABLE_PER_CLASS** : une table concrète par sous-classe (pas de table pour l'abstraite)

Ici on utilise `SINGLE_TABLE` avec `@DiscriminatorColumn(name = "TYPE", length = 4)` et les valeurs `CA` / `SA` :

```java
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE", length = 4)
public abstract class BankAccount { ... }

@Entity
@DiscriminatorValue("CA")
public class CurrentAccount extends BankAccount {
    private double overDraft;
}

@Entity
@DiscriminatorValue("SA")
public class SavingAccount extends BankAccount {
    private double interestRate;
}
```

SINGLE_TABLE est simple et performant en lecture (pas de jointure). Les colonnes spécifiques (`over_draft`, `interest_rate`) sont nullable selon le type. JOINED serait plus normalisé mais plus coûteux ; TABLE_PER_CLASS duplique les colonnes communes.

### Repositories

```java
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByNameContains(String keyword);
}

public interface AccountOperationRepository extends JpaRepository<AccountOperation, Long> {
    List<AccountOperation> findByBankAccountId(String accountId);
    Page<AccountOperation> findByBankAccountId(String accountId, Pageable pageable);
}
```

### Test DAO

Un `CommandLineRunner` crée 3 clients, pour chacun un compte courant et un compte épargne (id UUID), puis 10 opérations aléatoires par compte.

[À remplacer : capture de la table `bank_account` avec la colonne `TYPE` (CA / SA) dans MySQL]

## Couche service et DTOs

### Pourquoi des DTOs

Les contrôleurs REST ne doivent pas exposer directement les entités JPA : relations lazy, structure interne, couplage fort avec la base. Les DTOs (`CustomerDTO`, `CurrentBankAccountDTO`, `SavingBankAccountDTO`, `AccountOperationDTO`, `AccountHistoryDTO`) portent uniquement les données utiles à l'API. Un mapper (`BankAccountMapperImpl`) convertit entité ↔ DTO avec `BeanUtils.copyProperties`.

```java
public CustomerDTO fromCustomer(Customer customer) {
    CustomerDTO customerDTO = new CustomerDTO();
    BeanUtils.copyProperties(customer, customerDTO);
    return customerDTO;
}
```

`BankAccountDTO` regroupe les champs communs ; `CurrentBankAccountDTO` et `SavingBankAccountDTO` l'étendent.

### Service transactionnel

`BankAccountServiceImpl` est annoté `@Service` et `@Transactional`. Toute la logique métier (clients, comptes, débit/crédit/virement, historique paginé) passe par ce service. Les erreurs métier sont des exceptions dédiées (`CustomerNotFoundException`, `BankAccountNotFoundException`, `BalanceNotSufficientException`).

```java
public void debit(String accountId, double amount, String description)
        throws BankAccountNotFoundException, BalanceNotSufficientException {
    BankAccount bankAccount = bankAccountRepository.findById(accountId)
            .orElseThrow(() -> new BankAccountNotFoundException("BankAccount not found"));
    if (bankAccount.getBalance() < amount) {
        throw new BalanceNotSufficientException("Balance not sufficient");
    }
    // enregistre l'opération et met à jour le solde
}
```

Le `CommandLineRunner` utilise désormais uniquement la couche service (plus d'accès direct aux repositories).

## Web services RESTful

Les contrôleurs REST exposent la couche service en JSON. CORS est ouvert (`@CrossOrigin("*")`) pour préparer le frontend Angular. La doc interactive est disponible via springdoc : http://localhost:8085/swagger-ui.html

### Endpoints clients

| Méthode | URL | Description |
|---------|-----|-------------|
| GET | `/customers` | Liste des clients |
| GET | `/customers/search?keyword=` | Recherche par nom |
| GET | `/customers/{id}` | Détail d'un client |
| POST | `/customers` | Création |
| PUT | `/customers/{id}` | Mise à jour |
| DELETE | `/customers/{id}` | Suppression |

### Endpoints comptes

| Méthode | URL | Description |
|---------|-----|-------------|
| GET | `/accounts` | Liste des comptes |
| GET | `/accounts/{id}` | Détail d'un compte |
| GET | `/accounts/{id}/operations` | Historique complet |
| GET | `/accounts/{id}/pageOperations?page=&size=` | Historique paginé |
| POST | `/accounts/debit` | Débit (`DebitDTO`) |
| POST | `/accounts/credit` | Crédit (`CreditDTO`) |
| POST | `/accounts/transfer` | Virement (`TransferRequestDTO`) |

Exemple de débit :

```java
@PostMapping("/accounts/debit")
public DebitDTO debit(@RequestBody DebitDTO debitDTO)
        throws BankAccountNotFoundException, BalanceNotSufficientException {
    bankAccountService.debit(debitDTO.getAccountId(), debitDTO.getAmount(), debitDTO.getDescription());
    return debitDTO;
}
```

### Gestion des exceptions

Un `@RestControllerAdvice` traduit les exceptions métier en codes HTTP : 404 pour client/compte introuvable, 400 pour solde insuffisant.

```java
@ExceptionHandler(BalanceNotSufficientException.class)
public ResponseEntity<Map<String, Object>> handleBalanceNotSufficient(BalanceNotSufficientException ex) {
    return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
}
```

[À remplacer : capture Swagger UI sur /swagger-ui.html]
