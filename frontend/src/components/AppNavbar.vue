<template>
  <nav class="navbar">
    <div class="navbar-brand">
      <router-link to="/" class="brand-link">
        Plateforme Loisirs
      </router-link>
    </div>

    <div class="navbar-links">
      <router-link to="/" class="nav-link">Accueil</router-link>
      <router-link to="/users" class="nav-link">Utilisateurs</router-link>
      <router-link to="/activities" class="nav-link">Activités</router-link>
      <router-link to="/shop" class="nav-link">Boutique</router-link>
      <router-link to="/cart" class="nav-link">Panier</router-link>
      <router-link to="/login" class="nav-link login-link">Connexion</router-link>
      <router-link to="/register" class="nav-link login-link">Inscription</router-link>
    </div>
  </nav>
</template>

<script>
export default {
  name: 'AppNavbar'
};
</script>

<style scoped src="./AppNavbar.css"></style>