<template>
  <div class="login-page">
    <div class="login-card">
      <h1>{{ $t("auth.login.title") }}</h1>

      <p class="subtitle">
        {{ $t("auth.login.subtitle") }}
      </p>

      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label for="email">{{ $t("auth.login.email") }}</label>
          <input
              id="email"
              v-model="form.email"
              type="email"
              :placeholder="$t('auth.login.emailPlaceholder')"
              required
          />
        </div>

        <div class="form-group">
          <label for="password">{{ $t("auth.login.password") }}</label>
          <input
              id="password"
              v-model="form.password"
              type="password"
              :placeholder="$t('auth.login.passwordPlaceholder')"
              required
          />
        </div>

        <button type="submit" class="login-button">
          {{ $t("auth.login.submit") }}
        </button>
      </form>

      <p v-if="errorMessage" class="error-message">
        {{ errorMessage }}
      </p>

      <p v-if="successMessage" class="success-message">
        {{ successMessage }}
      </p>

      <p class="register-link">
        {{ $t("auth.login.noAccount") }}
        <RouterLink to="/register">{{ $t("auth.login.register") }}</RouterLink>
      </p>
    </div>
  </div>
</template>

<script>
import "./LoginPage.css";
import AuthService from "../../services/AuthService";

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
          AuthService.saveConnectedUser(response);
          this.successMessage = response.message;
          this.$router.push("/");
        })
        .catch(error => {
          console.error(error);
          this.errorMessage = error.message || this.$t("auth.login.error");
        });
    }
  }
};
</script>