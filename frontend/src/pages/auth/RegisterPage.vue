<template>
  <div class="register-page">
    <div class="register-card">
      <h1>Inscription</h1>

      <p class="subtitle">
        Créez votre compte pour accéder à la plateforme.
      </p>

      <form @submit.prevent="handleRegister">
        <div class="form-group">
          <label for="firstName">Prénom</label>
          <input
              id="firstName"
              v-model="form.firstName"
              type="text"
              placeholder="Entrez votre prénom"
              required
          />
        </div>

        <div class="form-group">
          <label for="lastName">Nom</label>
          <input
              id="lastName"
              v-model="form.lastName"
              type="text"
              placeholder="Entrez votre nom"
              required
          />
        </div>

        <div class="form-group">
          <label for="email">Adresse e-mail</label>
          <input
              id="email"
              v-model="form.email"
              type="email"
              placeholder="exemple@email.com"
              required
          />
        </div>

        <div class="form-group">
          <label for="password">Mot de passe</label>
          <input
              id="password"
              v-model="form.password"
              type="password"
              placeholder="Entrez un mot de passe"
              required
          />
        </div>

        <div class="checkbox-group">
          <input
              id="consentRgpd"
              v-model="form.consentRgpd"
              type="checkbox"
              required
          />

          <label for="consentRgpd">
            J’accepte le traitement de mes données personnelles.
          </label>
        </div>

        <button type="submit" class="register-button">
          S'inscrire
        </button>
      </form>

      <p v-if="errorMessage" class="error-message">
        {{ errorMessage }}
      </p>

      <p v-if="successMessage" class="success-message">
        {{ successMessage }}
      </p>

      <p class="login-link">
        Vous avez déjà un compte ?
        <RouterLink to="/login">Se connecter</RouterLink>
      </p>
    </div>
  </div>
</template>

<script>
import "./RegisterPage.css";
import AuthService from "../../services/AuthService";

export default {
  name: "RegisterPage",

  data() {
    return {
      form: {
        firstName: "",
        lastName: "",
        email: "",
        password: "",
        consentRgpd: false
      },
      errorMessage: "",
      successMessage: ""
    };
  },

  methods: {
    handleRegister() {
      this.errorMessage = "";
      this.successMessage = "";

      if (!this.form.consentRgpd) {
        this.errorMessage = "Vous devez accepter le traitement de vos données personnelles.";
        return;
      }

      AuthService.register(this.form)
          .then(response => {
            this.successMessage = response.message;

            this.form = {
              firstName: "",
              lastName: "",
              email: "",
              password: "",
              consentRgpd: false
            };
          })
          .catch(error => {
            console.error(error);
            this.errorMessage = "Impossible de créer le compte.";
          });
    }
  }
};
</script>