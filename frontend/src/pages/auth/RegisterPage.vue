<template>
  <div class="register-page">
    <div class="register-card">
      <h1>{{ $t("auth.register.title") }}</h1>

      <p class="subtitle">
        {{ $t("auth.register.subtitle") }}
      </p>

      <form @submit.prevent="handleRegister">
        <div class="form-group">
          <label for="firstName">{{ $t("auth.register.firstName") }}</label>
          <input
              id="firstName"
              v-model="form.firstName"
              type="text"
              :placeholder="$t('auth.register.firstNamePlaceholder')"
              required
          />
        </div>

        <div class="form-group">
          <label for="lastName">{{ $t("auth.register.lastName") }}</label>
          <input
              id="lastName"
              v-model="form.lastName"
              type="text"
              :placeholder="$t('auth.register.lastNamePlaceholder')"
              required
          />
        </div>

        <div class="form-group">
          <label for="email">{{ $t("auth.register.email") }}</label>
          <input
              id="email"
              v-model="form.email"
              type="email"
              :placeholder="$t('auth.register.emailPlaceholder')"
              required
          />
        </div>

        <div class="form-group">
          <label for="password">{{ $t("auth.register.password") }}</label>
          <input
              id="password"
              v-model="form.password"
              type="password"
              :placeholder="$t('auth.register.passwordPlaceholder')"
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
            {{ $t("auth.register.consentPrefix") }}
            <router-link to="/politique-confidentialite" target="_blank" rel="noopener">
              {{ $t("auth.register.consentLink") }}
            </router-link>.
          </label>
        </div>

        <button type="submit" class="register-button">
          {{ $t("auth.register.submit") }}
        </button>
      </form>

      <p v-if="errorMessage" class="error-message">
        {{ errorMessage }}
      </p>

      <p v-if="successMessage" class="success-message">
        {{ successMessage }}
      </p>

      <p class="login-link">
        {{ $t("auth.register.alreadyAccount") }}
        <RouterLink to="/login">{{ $t("auth.register.login") }}</RouterLink>
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
        this.errorMessage = this.$t("auth.register.consentRequired");
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
            this.errorMessage =
                error.status === 409 ? this.$t("auth.register.emailAlreadyUsed")
              : error.status === 400 ? this.$t("auth.register.invalidData")
              : this.$t("auth.register.error");          });
    }
  }
};
</script>