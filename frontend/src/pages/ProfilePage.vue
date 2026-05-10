<template>
  <div class="profile-page">
    <section class="profile-card">
      <div class="profile-header">
        <h1>Mon profil</h1>
        <p>Voici les informations liées à votre compte utilisateur.</p>
      </div>

      <div v-if="user" class="profile-content">
        <div class="profile-row">
          <span class="profile-label">Prénom</span>
          <span class="profile-value">{{ user.firstName }}</span>
        </div>

        <div class="profile-row">
          <span class="profile-label">Nom</span>
          <span class="profile-value">{{ user.lastName }}</span>
        </div>

        <div class="profile-row">
          <span class="profile-label">Email</span>
          <span class="profile-value">{{ user.email }}</span>
        </div>

        <div class="profile-row">
          <span class="profile-label">Rôle(s)</span>
          <div class="profile-roles">
            <span
                v-for="role in user.roles"
                :key="role"
                class="profile-role"
            >
              {{ formatRole(role) }}
            </span>
          </div>
        </div>
      </div>

      <div v-else class="profile-empty">
        <p>Aucun utilisateur connecté.</p>
      </div>
    </section>
  </div>
</template>

<script>
import AuthService from "../services/AuthService";
import "./ProfilePage.css";

export default {
  name: "ProfilePage",

  data() {
    return {
      user: null
    };
  },

  mounted() {
    this.user = AuthService.getConnectedUser();
  },

  methods: {
    formatRole(role) {
      if (role === "ADMIN") {
        return "Administrateur";
      }

      if (role === "PARTNER") {
        return "Partenaire";
      }

      if (role === "MEMBER") {
        return "Membre";
      }

      return role;
    }
  }
};
</script>