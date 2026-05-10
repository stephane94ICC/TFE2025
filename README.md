# TFE2025-2026
# Plateforme Loisirs

Projet de fin d’études — plateforme web de réservation et d’achat de services de loisirs.

## Description du projet

Ce projet consiste à développer une plateforme web permettant aux utilisateurs de consulter des activités de loisirs, d’acheter des produits liés à ces activités et de gérer un panier.

L’application comprend également des espaces différenciés selon les rôles :

- visiteur : consultation des activités et de la boutique ;
- membre : accès au profil et au panier ;
- administrateur : gestion des produits et des utilisateurs ;
- partenaire : espace dédié prévu pour la gestion future des activités.

Le projet utilise une architecture séparant le backend, le frontend et la base de données.

## Technologies utilisées

### Backend

- Java 21
- Spring Boot 3.4.1
- Spring Security
- JWT
- Maven
- MySQL
- Flyway
- Lombok

### Frontend

- Vue.js
- Vue Router
- Axios
- CSS

## Fonctionnalités principales

### Fonctionnalités publiques

- affichage de la page d’accueil ;
- consultation des activités ;
- consultation de la boutique ;
- consultation du détail d’un produit ;
- inscription ;
- connexion.

### Fonctionnalités membre

- accès au profil ;
- accès au panier ;
- gestion locale du panier.

### Fonctionnalités administrateur

- accès à un tableau de bord administrateur ;
- consultation des utilisateurs ;
- consultation des produits ;
- ajout d’un produit ;
- modification d’un produit ;
- suppression d’un produit ;
- séparation entre les routes publiques et les routes d’administration.

### Fonctionnalités partenaire

- espace partenaire préparé ;
- futures fonctionnalités prévues pour la gestion des activités.

## Sécurité

L’authentification repose sur un système de connexion avec JWT.

Les mots de passe sont chiffrés avec BCrypt avant d’être enregistrés en base de données.

Les routes d’administration sont protégées et réservées aux utilisateurs ayant le rôle `ADMIN`.



## État actuel du projet
### Fonctionnalités déjà mises en place :
- inscription ; 
- connexion ; 
- hashage des mots de passe avec BCrypt ; 
- génération et utilisation d’un token JWT ; 
- gestion des rôles ; 
- protection des routes frontend avec Vue Router ; 
- protection des routes backend avec Spring Security ; 
- page profil ; 
- page panier ; 
- tableau de bord administrateur ; 
- gestion complète des produits côté administrateur ; 
- séparation des endpoints publics et administrateur. 
### Améliorations prévues
### Fonctionnalités prévues pour la suite :
- upload d’image pour les produits ; 
- amélioration esthétique des pages ; 
- gestion des activités côté administrateur ; 
- développement de l’espace partenaire ; 
- validation complète du panier et création de commande ; 
- système d’avis ou de commentaires ; 
- amélioration de la gestion du profil utilisateur. 

## Auteur
### Projet réalisé dans le cadre d’un travail de fin d’études.
