<template>
  <div class="login-page">
    <div class="login-card">
      <h1>Connexion</h1>

      <p class="subtitle">
        Connectez-vous à votre compte.
      </p>

      <form @submit.prevent="handleLogin">
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
              placeholder="Entrez votre mot de passe"
              required
          />
        </div>

        <button type="submit" class="login-button">
          Se connecter
        </button>
      </form>

      <p v-if="errorMessage" class="error-message">
        {{ errorMessage }}
      </p>

      <p v-if="successMessage" class="success-message">
        {{ successMessage }}
      </p>

      <p class="register-link">
        Vous n’avez pas encore de compte ?
        <RouterLink to="/register">S'inscrire</RouterLink>
      </p>
    </div>
  </div>
</template>

<script>
import "./LoginPage.css";
import AuthService from "../services/AuthService";

export default {
  name: "LoginPage",

  data() {
    return {
      form: {
        email: "",
        password: ""
      },
      errorMessage: "",
      successMessage: ""
    };
  },

  methods: {
    handleLogin() {
      this.errorMessage = "";
      this.successMessage = "";

      AuthService.login(this.form)
          .then(response => {
            this.successMessage = response.message;
            localStorage.setItem("connectedUser", JSON.stringify(response));
          })
          .catch(error => {
            console.error(error);
            this.errorMessage = "Adresse e-mail ou mot de passe incorrect.";
          });
    }
  }
};
</script>