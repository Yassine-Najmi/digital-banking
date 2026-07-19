# Guide produits et politiques - E-Bank

Document interne fictif destiné au conseiller virtuel. Banque digitale E-Bank, siège Casablanca.

## Présentation

E-Bank propose des services bancaires en ligne pour les particuliers et les professionnels.
Les clients authentifiés peuvent consulter leurs comptes, effectuer des opérations et
obtenir des informations sur les produits via l'application web.

## Types de comptes

### Compte courant (Current Account)

- Destiné aux opérations quotidiennes (paiements, virements, retraits).
- Solde disponible immédiatement après crédit.
- Découvert autorisé jusqu'à **5 000 MAD** pour les clients particuliers éligibles,
  sous réserve d'une analyse du risque crédit.
- Agios de découvert : **12 % l'an**, calculés au prorata des jours d'utilisation.
- Carte bancaire Visa Classic incluse (cotisation annuelle 150 MAD).

### Compte épargne (Saving Account)

- Destiné à la constitution d'une épargne de précaution.
- Taux d'intérêt créditeur : **2,5 % l'an**, capitalisé trimestriellement.
- Plafond de dépôt : **500 000 MAD** par compte épargne.
- Pas de découvert : le solde ne peut pas être négatif.
- Retraits libres sans pénalité, sous réserve d'un solde minimum de **500 MAD**.

## Frais bancaires

| Opération | Tarif |
|-----------|-------|
| Ouverture de compte | Gratuite |
| Tenue de compte courant | 25 MAD / mois |
| Tenue de compte épargne | Gratuite |
| Virement interne E-Bank | Gratuit |
| Virement vers une autre banque (Maroc) | 10 MAD |
| Virement international SWIFT | 75 MAD + frais correspondant |
| Opposition sur carte | 50 MAD |
| Relevé papier mensuel | 15 MAD (gratuit en PDF dans l'app) |

Les frais sont prélevés automatiquement le dernier jour ouvré du mois.

## Découvert et incidents

1. Le découvert n'est disponible que sur compte courant, après validation du dossier.
2. Au-delà du plafond autorisé, les opérations de débit sont refusées.
3. En cas de solde débiteur non autorisé, des frais d'incident de **80 MAD** par écriture
   peuvent s'appliquer, dans la limite de 3 incidents par mois.
4. Le client doit régulariser sa situation sous 15 jours, faute de quoi le compte
   peut être suspendu.

## Procédures courantes

### Ouverture de compte

1. Création du profil client (nom, email) par un administrateur ou en agence.
2. Ouverture d'un compte courant et/ou épargne.
3. Remise des identifiants de connexion à l'application web.
4. Activation sous 24 heures ouvrées.

### Débit, crédit et virement

- **Crédit** : dépôt d'espèces ou réception d'un virement ; augmente le solde.
- **Débit** : paiement ou retrait ; diminue le solde (contrôle du solde et du découvert).
- **Virement** : débit du compte source puis crédit du compte destination, atomique.
- Seuls les utilisateurs avec le rôle **ADMIN** peuvent saisir ces opérations dans l'application.

### Réclamations

Les réclamations s'adressent à `reclamations@ebank.example` ou via le chatbot pour
une orientation. Délai de réponse indicatif : 5 jours ouvrés.

## Horaires et contacts

- Service client : du lundi au vendredi, 9h-18h (heure de Casablanca).
- Urgences cartes : 24h/24 via l'application (menu Sécurité).
- Site : application web E-Bank (frontend Angular) et API sur le port 8085 en local.

## Hors périmètre

Le conseiller virtuel répond uniquement aux questions liées aux produits, frais,
comptes et procédures E-Bank décrits dans ce document. Les sujets sans rapport
(météo, sport, politique, conseils médicaux ou juridiques généraux) doivent être refusés.
