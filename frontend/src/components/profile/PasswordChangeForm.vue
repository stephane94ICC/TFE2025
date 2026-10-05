<template>
  <form class="password-form" @submit.prevent="changePassword">
    <h2 class="password-form-title">
      {{ $t("member.profile.changePasswordTitle") }}
    </h2>

    <p class="password-form-text">
      {{ $t("member.profile.changePasswordText") }}
    </p>

    <!-- Champ masqué : permet aux gestionnaires de mots de passe d'associer le bon compte. -->
    <input
        type="email"
        :value="email"
        autocomplete="username"
        readonly
        hidden
    />

    <div v-if="errorMessage" class="password-form-error">
      {{ errorMessage }}
    </div>

    <div class="password-form-row">
      <label class="password-form-label" for="currentPassword">
        {{ $t("member.profile.currentPassword") }}
      </label>
      <input
          id="currentPassword"
          v-model="form.currentPassword"
          type="password"
          autocomplete="current-password"
          class="password-form-input"
          :disabled="submitting"
      />
    </div>

    <div class="password-form-row">
      <label class="password-form-label" for="newPassword">
        {{ $t("member.profile.newPassword") }}
      </label>
      <input
          id="newPassword"
          v-model="form.newPassword"
          type="password"
          autocomplete="new-password"
          class="password-form-input"
          :disabled="submitting"
      />
    </div>

    <p class="password-form-text">
      {{ $t("member.profile.passwordRule") }}
    </p>

    <div class="password-form-row">
      <label class="password-form-label" for="confirmPassword">
        {{ $t("member.profile.confirmNewPassword") }}
      </label>
      <input
          id="confirmPassword"
          v-model="form.confirmPassword"
          type="password"
          autocomplete="new-password"
          class="password-form-input"
          :disabled="submitting"
      />
    </div>

    <div class="password-form-actions">
      <button
          type="submit"
          class="password-form-button"
          :disabled="submitting || !canSubmit"
      >
        {{ submitting ? $t("member.profile.changingPassword") : $t("member.profile.changePassword") }}
      </button>
    </div>
  </form>
</template>

<script>
import AuthService from "../../services/AuthService";
import { changeMemberPassword } from "../../services/UserService";
import { isStrongPassword } from "../../utils/passwordPolicy";

/**
 * Changement du mot de passe de l'utilisateur connecté (tous les rôles).
 * Composant autonome : il pourra être réutilisé tel quel pour une évolution
 * (changement imposé à la première connexion).
 */
export default {
  name: "PasswordChangeForm",

  props: {
    email: {
      type: String,
      default: ""
    }
  },

  data() {
    return {
      form: {
        currentPassword: "",
        newPassword: "",
        confirmPassword: ""
      },
      submitting: false,
      errorMessage: ""
    };
  },

  computed: {
    canSubmit() {
      return Boolean(this.form.currentPassword
          && this.form.newPassword
          && this.form.confirmPassword);
    }
  },

  methods: {
    async changePassword() {
      if (this.submitting || !this.canSubmit) {
        return;
      }

      this.errorMessage = "";

      const { currentPassword, newPassword, confirmPassword } = this.form;

      // Contrôles côté client : confort uniquement, le serveur revalide tout.
      if (!isStrongPassword(newPassword)) {
        this.errorMessage = this.$t("member.profile.passwordTooWeak");
        return;
      }

      if (newPassword !== confirmPassword) {
        this.errorMessage = this.$t("member.profile.passwordMismatch");
        return;
      }

      if (newPassword === currentPassword) {
        this.errorMessage = this.$t("member.profile.passwordSameAsCurrent");
        return;
      }

      try {
        this.submitting = true;

        await changeMemberPassword(currentPassword, newPassword);

        // Déconnexion : l'utilisateur se reconnecte avec son nouveau mot de passe.
        AuthService.logout();
        this.$router.push({ path: "/login", query: { reason: "password-changed" } });
      } catch (error) {
        // Ne jamais journaliser l'objet d'erreur complet : error.config.data
        // contient les mots de passe en clair.
        console.error("Changement de mot de passe refusé :", error.response?.status);

        // Après les contrôles ci-dessus, un 400 du serveur signifie
        // en pratique « mot de passe actuel incorrect ».
        this.errorMessage = error.response?.status === 400
            ? this.$t("member.profile.currentPasswordIncorrect")
            : this.$t("member.profile.changePasswordError");

        this.form.currentPassword = "";
      } finally {
        this.submitting = false;
      }
    }
  }
};
</script>

<style scoped src="./PasswordChangeForm.css"></style>