<template>
  <div class="profile-page">
    <section class="profile-card">
      <div class="profile-header">
        <h1>Mon profil</h1>
        <p>Consultez et modifiez les informations liées à votre compte.</p>
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
          <label class="profile-label" for="firstName">Prénom</label>
          <input
              id="firstName"
              v-model="form.firstName"
              type="text"
              maxlength="100"
              class="profile-input"
              :disabled="saving"
          />
        </div>

        <div class="profile-row">
          <label class="profile-label" for="lastName">Nom</label>
          <input
              id="lastName"
              v-model="form.lastName"
              type="text"
              maxlength="100"
              class="profile-input"
              :disabled="saving"
          />
        </div>

        <div class="profile-row">
          <label class="profile-label" for="phone">Téléphone</label>
          <input
              id="phone"
              v-model="form.phone"
              type="tel"
              maxlength="20"
              placeholder="Optionnel"
              class="profile-input"
              :disabled="saving"
          />
        </div>

        <div class="profile-row">
          <span class="profile-label">Email</span>
          <span class="profile-value">{{ user.email }}</span>
        </div>

        <p class="profile-hint">
          L'adresse e-mail identifie votre compte et ne peut pas être modifiée.
        </p>

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

        <div class="profile-actions">
          <button
              type="button"
              class="profile-save-button"
              :disabled="saving || !hasChanges"
              @click="saveProfile"
          >
            {{ saving ? "Enregistrement..." : "Enregistrer" }}
          </button>

          <button
              type="button"
              class="profile-reset-button"
              :disabled="saving || !hasChanges"
              @click="resetForm"
          >
            Annuler
          </button>
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
import {
  uploadProfileImage,
  getMemberProfile,
  updateMemberProfile
} from "../../services/UserService";
import "./ProfilePage.css";

export default {
  name: "ProfilePage",

  data() {
    return {
      user: null,
      form: {
        firstName: "",
        lastName: "",
        phone: ""
      },
      selectedFile: null,
      uploading: false,
      saving: false,
      successMessage: "",
      errorMessage: ""
    };
  },

  computed: {
    profileImageUrl() {
      return this.user?.profileImageUrl
          || "/uploads/members/default-profile.png";
    },

    hasChanges() {
      if (!this.user) {
        return false;
      }

      return this.form.firstName !== (this.user.firstName || "")
          || this.form.lastName !== (this.user.lastName || "")
          || this.form.phone !== (this.user.phone || "");
    }
  },

  async mounted() {
    this.user = AuthService.getConnectedUser();

    if (!this.user) {
      return;
    }

    await this.loadProfile();
  },

  methods: {
    async loadProfile() {
      try {
        const response = await getMemberProfile();

        this.applyProfile(response.data);
      } catch (error) {
        console.error(error);
        this.errorMessage = error.response?.data?.error
            || "Impossible de charger votre profil.";
      }
    },

    applyProfile(profile) {
      this.user = { ...this.user, ...profile };
      AuthService.saveConnectedUser(this.user);
      this.resetForm();
    },

    resetForm() {
      this.form.firstName = this.user.firstName || "";
      this.form.lastName = this.user.lastName || "";
      this.form.phone = this.user.phone || "";
    },

    async saveProfile() {
      try {
        this.saving = true;
        this.successMessage = "";
        this.errorMessage = "";

        const response = await updateMemberProfile({
          firstName: this.form.firstName.trim(),
          lastName: this.form.lastName.trim(),
          phone: this.form.phone.trim()
        });

        this.applyProfile(response.data);
        this.successMessage = "Profil modifié avec succès.";
      } catch (error) {
        console.error(error);
        this.errorMessage = error.response?.data?.error
            || "Impossible de modifier votre profil.";
      } finally {
        this.saving = false;
      }
    },

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