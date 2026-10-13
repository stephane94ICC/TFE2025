<template>
  <div v-if="visible" class="session-notice" role="status">
    <p class="session-notice__text">
      {{ $t("auth.session.expiryWarning", { minutes: minutesLeft, time: expiresAtLabel }) }}
    </p>

    <button type="button" class="session-notice__button" @click="dismiss">
      {{ $t("auth.session.dismiss") }}
    </button>
  </div>
</template>

<script>
import AuthService from "../services/AuthService";
import i18n from "../i18n";
import "./SessionExpiryNotice.css";

const WARNING_BEFORE_MS = 10 * 60 * 1000;
const CHECK_INTERVAL_MS = 15 * 1000;

export default {
  name: "SessionExpiryNotice",

  data() {
    return {
      expiresAt: null,
      minutesLeft: null,
      dismissedExpiresAt: null,
      timerId: null
    };
  },

  computed: {
    visible() {
      return this.minutesLeft !== null && this.dismissedExpiresAt !== this.expiresAt?.getTime();
    },

    // « 22 h 39 » en français, « 22:39 » en néerlandais et en anglais.
    expiresAtLabel() {
      if (!this.expiresAt) {
        return "";
      }

      const hours = String(this.expiresAt.getHours()).padStart(2, "0");
      const minutes = String(this.expiresAt.getMinutes()).padStart(2, "0");

      return i18n.global.locale.value === "fr" ? `${hours}\u00A0h\u00A0${minutes}` : `${hours}:${minutes}`;
    }
  },

  mounted() {
    this.check();
    this.timerId = window.setInterval(this.check, CHECK_INTERVAL_MS);
    // Onglet revenu au premier plan : ses minuteries étaient ralenties, on vérifie tout de suite.
    document.addEventListener("visibilitychange", this.check);
  },

  beforeUnmount() {
    window.clearInterval(this.timerId);
    document.removeEventListener("visibilitychange", this.check);
  },

  methods: {
    check() {
      this.expiresAt = AuthService.getSessionExpiresAt();

      if (!this.expiresAt) {
        this.minutesLeft = null;
        return;
      }

      const remaining = this.expiresAt.getTime() - Date.now();

      if (remaining <= 0) {
        this.expire();
        return;
      }

      this.minutesLeft = remaining <= WARNING_BEFORE_MS ? Math.ceil(remaining / 60000) : null;
    },

    // Session terminée pendant l'utilisation : l'interface est vidée et l'utilisateur prévenu.
    expire() {
      this.minutesLeft = null;
      AuthService.clearConnectedUser();
      this.$router.push({ path: "/login", query: { reason: "session-expired" } });
    },

    dismiss() {
      this.dismissedExpiresAt = this.expiresAt?.getTime() ?? null;
    }
  }
};
</script>
