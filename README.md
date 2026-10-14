# TFE2025-2026
# Plateforme Loisirs - Bel'Loisirs

Projet de fin d’études — plateforme web de réservation et d’achat de services de loisirs.

## Description du projet

La plateforme permet de consulter et de réserver des activités de loisirs proposées par des partenaires, d’acheter des produits dans une boutique en ligne et de payer en ligne via Stripe. Le paiement d’une réservation est versé directement au partenaire, la plateforme retenant sa commission.

L’application comprend des espaces différenciés selon les rôles :

- visiteur : consultation des activités, des vitrines des partenaires et de la boutique, inscription, connexion ;
- membre : profil, panier, réservations, paiement, suppression du compte ;
- partenaire : gestion de ses activités, créneaux, lieux, images, compte de paiement et ventes ;
- administrateur : gestion des utilisateurs, des partenaires, des produits et des activités, journal d’audit.

L’architecture sépare une API REST (Spring Boot) et une application monopage (Vue.js). Le frontend compilé est servi par le backend, sur la même adresse.

## Technologies utilisées

### Backend

- Java 21
- Spring Boot 3.4.1
- Spring Security (cookie de session HttpOnly, protection CSRF)
- JWT (jjwt)
- Spring Data JPA
- Maven
- MySQL (développé et testé avec la version 9.1)
- Flyway (59 migrations)
- Lombok
- Stripe : Checkout, webhooks et Connect
- OpenPDF (factures, justificatifs et notes de crédit)

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
- consultation des activités, de leurs créneaux et de l’adresse de chaque lieu ;
- vitrine publique de chaque partenaire (présentation, activités, lieux, coordonnées) ;
- consultation de la boutique et du détail d’un produit ;
- conditions générales et politique de confidentialité ;
- inscription et connexion ;
- interface disponible en trois langues.

### Fonctionnalités membre

- modification du profil, de la photo et du mot de passe ;
- panier ;
- réservation d’un créneau avec contrôle des places restantes ;
- paiement Stripe ;
- consultation et annulation de ses réservations, avec remboursement ;
- suppression du compte.

### Fonctionnalités partenaire

- création et modification d’activités, soumises à validation ;
- gestion des créneaux, des lieux, des adresses et des images ;
- compte de paiement Stripe Connect ;
- consultation des ventes et téléchargement des documents de vente en PDF.

### Fonctionnalités administrateur

- gestion des utilisateurs et des partenaires (taux de commission) ;
- gestion des produits et de leurs images ;
- validation ou refus des activités ;
- consultation du journal d’audit avec filtres et pagination.

## Sécurité et données personnelles

- authentification par JWT, placé dans un cookie `HttpOnly` (`__Host-BL_AUTH` en HTTPS) : le jeton n’est jamais accessible au JavaScript ;
- protection CSRF par double soumission (cookie `XSRF-TOKEN` et en-tête `X-XSRF-TOKEN`) ;
- déconnexion valable sur tous les appareils (version de session) ; jetons révoqués après un changement de mot de passe ;
- rôles relus en base à chaque requête : un rôle retiré prend effet immédiatement ;
- réponses 401 (non connecté ou session expirée) et 403 (droits insuffisants) distinctes ; avertissement 10 minutes avant la fin de la session ;
- mots de passe hachés avec BCrypt ; limitation des tentatives de connexion par compte et par adresse IP ;
- accès limité à ses propres données (réponse 404 pour les ressources d’autrui) ;
- webhook Stripe authentifié par sa signature ; aucune donnée de carte bancaire ne transite par le serveur ;
- suppression du compte par désactivation et pseudonymisation des données personnelles ;
- journal d’audit des actions sensibles, purgé automatiquement après 12 mois ;
- deux cookies seulement, strictement nécessaires : pas de bandeau de consentement.

## Installation

### Prérequis

- Java 21
- Node.js 20
- MySQL (9.1 en développement)
- Stripe CLI, pour recevoir les webhooks en local

### Variables d’environnement

- `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` : connexion à la base ;
- `APP_JWT_SECRET` : clé de signature des jetons (obligatoire) ;
- `APP_JWT_EXPIRATION_MINUTES` : durée de la session (par défaut 120) ;
- `APP_AUTH_COOKIE_SECURE` : à mettre à `false` en local (`http://localhost`), à laisser par défaut (`true`) en HTTPS ;
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
4. Dans un autre terminal, transmettre les webhooks Stripe à l’application, puis reporter le secret `whsec_…` affiché dans `STRIPE_WEBHOOK_SECRET` :
   ```
   stripe listen --forward-to localhost:8080/api/stripe/webhook
   ```
   Sans cette commande, les paiements restent « en attente ».
5. Ouvrir `http://localhost:8080`.

## Tests

159 tests unitaires et d’intégration, rangés par thème (`business`, `security`, `rgpd`, `common`), exécutés sur une base dédiée `loisirs_test` (à créer vide). Aucun appel n’est fait à Stripe pendant les tests.

```
./mvnw test
```

## Améliorations prévues

- verrou sur le créneau lors d’une réservation (deux clients sur la dernière place) ;
- fin de partenariat demandée par le partenaire et validée par l’administrateur ;
- anonymisation des données de facturation après 10 ans ;
- double authentification des administrateurs ;
- en-tête `Content-Security-Policy` ;
- meilleur référencement (rendu côté serveur ou pré-rendu) ;
- e-mails transactionnels ;
- mode sombre.

## Auteur

Projet réalisé dans le cadre d’un travail de fin d’études.