<template>
  <div class="profile-page">
    <section class="profile-card">
      <div class="profile-header">
        <h1>Mon profil</h1>
        <p>Voici les informations liées à votre compte utilisateur.</p>
      </div>

      <div v-if="user" class="profile-content">
        <div class="profile-image-section">
          <img
              :src="profileImageUrl"
              alt="Photo de profil"
              class="profile-image"
          />

          <div class="profile-image-actions">
            <input
                ref="fileInput"
                type="file"
                accept=".jpg,.jpeg,.png,.webp"
                @change="selectFile"
            />

            <button
                type="button"
                class="profile-image-button"
                :disabled="!selectedFile || uploading"
                @click="uploadImage"
            >
              {{ uploading ? "Envoi..." : "Modifier ma photo" }}
            </button>
          </div>
        </div>

        <div v-if="successMessage" class="profile-success">
          {{ successMessage }}
        </div>

        <div v-if="errorMessage" class="profile-error">
          {{ errorMessage }}
        </div>

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
import AuthService from "../../services/AuthService";
import { uploadProfileImage } from "../../services/UserService";
import "./ProfilePage.css";

export default {
  name: "ProfilePage",

  data() {
    return {
      user: null,
      selectedFile: null,
      uploading: false,
      successMessage: "",
      errorMessage: ""
    };
  },

  computed: {
    profileImageUrl() {
      return this.user?.profileImageUrl
          || "/uploads/members/default-profile.png";
    }
  },

  mounted() {
    this.user = AuthService.getConnectedUser();
  },

  methods: {
    selectFile(event) {
      this.selectedFile = event.target.files[0] || null;
    },

    async uploadImage() {
      if (!this.selectedFile) {
        return;
      }

      try {
        this.uploading = true;
        this.successMessage = "";
        this.errorMessage = "";

        const response = await uploadProfileImage(this.selectedFile);

        this.user.profileImageUrl = response.data.profileImageUrl;
        AuthService.saveConnectedUser(this.user);

        this.selectedFile = null;
        this.$refs.fileInput.value = "";
        this.successMessage = "Photo de profil modifiée avec succès.";
      } catch (error) {
        console.error(error);
        this.errorMessage = error.response?.data?.error
            || "Impossible de modifier la photo de profil.";
      } finally {
        this.uploading = false;
      }
    },

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