<template>
  <nav class="navbar">
    <div class="navbar-brand">
      <router-link to="/" class="brand-link">
        Plateforme Loisirs
      </router-link>
    </div>

    <div class="navbar-links">
      <router-link to="/" class="nav-link">Accueil</router-link>
      <router-link to="/activities" class="nav-link">Activités</router-link>
      <router-link to="/shop" class="nav-link">Boutique</router-link>

      <router-link v-if="isAdmin" to="/admin" class="nav-link">
        Admin
      </router-link>

      <router-link v-if="isAdmin" to="/users" class="nav-link">
        Utilisateurs
      </router-link>
      <router-link v-if="isLoggedIn" to="/profile" class="nav-link">
        Profile
      </router-link>
      <router-link v-if="isLoggedIn" to="/cart" class="nav-link">
        Panier
      </router-link>

      <router-link v-if="isPartner" to="/partner" class="nav-link">
        Espace partenaire
      </router-link>

      <router-link v-if="!isLoggedIn" to="/login" class="nav-link login-link">
        Connexion
      </router-link>

      <router-link v-if="!isLoggedIn" to="/register" class="nav-link register-link">
        Inscription
      </router-link>

      <button v-if="isLoggedIn" @click="logout" class="logout-button">
        Déconnexion
      </button>
    </div>
  </nav>
</template>

<script>
import AuthService from "../services/AuthService";

export default {
  name: "AppNavbar",

  data() {
    return {
      isLoggedIn: false,
      isAdmin: false,
      isPartner: false,
      isMember: false
    };
  },

  mounted() {
    this.refreshAuthState();
  },

  watch: {
    $route() {
      this.refreshAuthState();
    }
  },

  methods: {
    refreshAuthState() {
      this.isLoggedIn = AuthService.isLoggedIn();
      this.isAdmin = AuthService.hasRole("ADMIN");
      this.isPartner = AuthService.hasRole("PARTNER");
      this.isMember = AuthService.hasRole("MEMBER");
    },

    logout() {
      AuthService.logout();
      this.refreshAuthState();
      this.$router.push("/login");
    }
  }
};
</script>

<style scoped src="./AppNavbar.css"></style>