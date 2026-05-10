<template>
  <div class="admin-page">
    <section class="admin-header">
      <h1>Espace administrateur</h1>
      <p>
        Bienvenue dans le tableau de bord administrateur. Cette page permet
        d’accéder aux principales zones de gestion de la plateforme.
      </p>
    </section>

    <section class="admin-dashboard">
      <router-link to="/users" class="admin-block admin-block-active">
        <h2>Utilisateurs</h2>
        <p>
          Consulter et gérer les comptes utilisateurs inscrits sur la plateforme.
        </p>
        <span>Accéder</span>
      </router-link>

      <div class="admin-block admin-block-disabled">
        <h2>Activités</h2>
        <p>
          Gérer les activités proposées par les partenaires.
        </p>
        <span>Bientôt disponible</span>
      </div>

      <router-link to="/admin/products" class="admin-block admin-block-active">
        <h2>Produits</h2>
        <p>
          Gérer les produits disponibles dans la boutique.
        </p>
        <span>Accéder</span>
      </router-link>

      <div class="admin-block admin-block-disabled">
        <h2>Partenaires</h2>
        <p>
          Consulter et gérer les partenaires de la plateforme.
        </p>
        <span>Bientôt disponible</span>
      </div>
    </section>
  </div>
</template>

<script>
import "./AdminPage.css";

export default {
  name: "AdminPage"
};
</script>