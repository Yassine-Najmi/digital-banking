# Compte rendu - Digital Banking

Yassine Najmi

## Architecture

Le projet est un monorepo organisé en deux parties :

- `backend/` : API Spring Boot 3 (Java 17, Spring Data JPA, MySQL). La documentation OpenAPI est exposée via springdoc.
- `frontend/` : application Angular qui consomme l'API.

La sécurité repose sur JWT (à ajouter dans les prochaines étapes). Un chatbot RAG est prévu pour interroger les données bancaires en langage naturel.

Au démarrage local, MySQL 8 tourne dans Docker (`docker compose up -d`, port 3307, base `ebank-db`). Le backend écoute sur le port 8085.
