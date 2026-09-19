# TFE2025-2026
# Plateforme Loisirs - Bel'Loisirs

Projet de fin d’études — plateforme web de réservation et d’achat de services de loisirs.

## Description du projet

La plateforme permet de consulter et de réserver des activités de loisirs proposées par des partenaires, d’acheter des produits dans une boutique en ligne et de payer en ligne via Stripe.

L’application comprend des espaces différenciés selon les rôles :

- visiteur : consultation des activités et de la boutique, inscription, connexion ;
- membre : profil, panier, réservations, paiement, suppression du compte ;
- partenaire : gestion de ses activités, sessions, lieux et images ;
- administrateur : gestion des utilisateurs, des produits et des activités, journal d’audit.

L’architecture sépare une API REST (Spring Boot) et une application monopage (Vue.js). Le frontend compilé est servi par le backend.

## Technologies utilisées

### Backend

- Java 21
- Spring Boot 3.4.1
- Spring Security
- JWT
- Spring Data JPA
- Maven
- MySQL 8
- Flyway (43 migrations)
- Lombok
- Stripe (Checkout et webhooks)

### Frontend

- Vue.js 3
- Vue Router
- vue-i18n (français, néerlandais, anglais)
- Axios
- CSS (couleurs centralisées dans `theme.css`)

## Fonctionnalités principales

### Fonctionnalités publiques

- page d’accueil avec carrousel, catégories et suggestions ;
- recherche ;
- consultation des activités et de leurs sessions ;
- consultation de la boutique et du détail d’un produit ;
- politique de confidentialité ;
- inscription et connexion ;
- interface disponible en trois langues.

### Fonctionnalités membre

- modification du profil et de la photo ;
- panier ;
- réservation d’une session avec contrôle des places restantes ;
- paiement Stripe ;
- consultation et annulation de ses réservations ;
- suppression du compte.

### Fonctionnalités partenaire

- création et modification d’activités, soumises à validation ;
- gestion des sessions, des lieux, des adresses et des images.

### Fonctionnalités administrateur

- gestion des utilisateurs ;
- gestion des produits et de leurs images ;
- validation ou refus des activités ;
- consultation du journal d’audit avec filtres et pagination.

## Sécurité et données personnelles

- authentification par JWT ;
- mots de passe hachés avec BCrypt ;
- routes protégées selon le rôle, côté frontend et côté backend ;
- vérification à chaque requête que le compte est toujours actif ;
- accès limité à ses propres données (réponse 404 pour les ressources d’autrui) ;
- suppression du compte par désactivation et pseudonymisation des données personnelles ;
- journal d’audit des actions sensibles, purgé automatiquement après 12 mois.

## Installation

### Prérequis

- Java 21
- Node.js 20
- MySQL 8

### Variables d’environnement

- `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` : connexion à la base ;
- `APP_JWT_SECRET` : clé de signature des jetons (obligatoire) ;
- `STRIPE_SECRET_KEY`, `STRIPE_WEBHOOK_SECRET` : clés Stripe en mode test ;
- `APP_FRONTEND_URL` : URL de retour après paiement (par défaut `http://localhost:8080`).

Aucun secret n’est versionné.

### Lancement

1. Créer une base vide `loisirs` : Flyway crée le schéma et les données de démonstration au démarrage.
2. Construire le frontend :
   ```
   cd frontend
   npm install
   npm run build
   ```
3. Démarrer le backend depuis la racine du projet (sous Windows : `mvnw.cmd`) :
   ```
   ./mvnw spring-boot:run
   ```
4. Ouvrir `http://localhost:8080`.

## Tests

19 tests unitaires et d’intégration, exécutés sur une base dédiée `loisirs_test` (à créer vide) :

```
./mvnw test
```

## Améliorations prévues

- meilleur référencement (rendu côté serveur ou pré-rendu) ;
- jeton JWT dans un cookie `HttpOnly` ;
- reversement automatique aux partenaires (Stripe Connect) ;
- e-mails transactionnels ;
- conditions générales de vente ;
- mode sombre.

## Auteur

Projet réalisé dans le cadre d’un travail de fin d’études.