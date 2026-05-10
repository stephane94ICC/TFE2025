<template>
  <div class="profile-page">
    <div class="profile-card">
      <h1>Mon profil</h1>

      <div v-if="user" class="profile-info">
        <p>
          <strong>Prénom :</strong>
          {{ user.firstName }}
        </p>

        <p>
          <strong>Nom :</strong>
          {{ user.lastName }}
        </p>

        <p>
          <strong>Email :</strong>
          {{ user.email }}
        </p>

        <p>
          <strong>Rôles :</strong>
          {{ formattedRoles }}
        </p>
      </div>

      <p v-else class="error-message">
        Aucun utilisateur connecté.
      </p>
    </div>
  </div>
</template>

<script>
import "./ProfilePage.css";
import AuthService from "../services/AuthService";

export default {
  name: "ProfilePage",

  data() {
    return {
      user: null
    };
  },

  computed: {
    formattedRoles() {
      if (!this.user || !this.user.roles || this.user.roles.length === 0) {
        return "Aucun rôle";
      }

      return this.user.roles.join(", ");
    }
  },

  mounted() {
    this.user = AuthService.getConnectedUser();
  }
};
</script>