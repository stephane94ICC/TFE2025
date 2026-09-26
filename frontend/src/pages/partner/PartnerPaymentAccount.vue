<template>
  <div class="partner-payment">
    <div class="partner-payment-header">
      <h2>{{ $t("partner.payment.title") }}</h2>
      <span
        v-if="status"
        :class="['partner-payment-status', statusClass]"
      >
        {{ $t(`partner.payment.statuses.${status}`) }}
      </span>
    </div>

    <p v-if="loading">{{ $t("partner.payment.loading") }}</p>

    <template v-else>
      <p v-if="infoMessage" class="partner-payment-info">{{ infoMessage }}</p>
      <p v-if="errorMessage" class="partner-payment-error">{{ errorMessage }}</p>

      <p v-if="status">{{ $t(`partner.payment.descriptions.${status}`) }}</p>

      <!-- Le formulaire est hébergé par Stripe : identité et IBAN ne transitent jamais par Bel'Loisirs -->
      <button
        v-if="status === 'INCOMPLETE' || status === 'PENDING_VERIFICATION'"
        type="button"
        class="partner-payment-button"
        :disabled="redirecting"
        @click="startOnboarding"
      >
        {{
          redirecting
            ? $t("partner.payment.redirecting")
            : status === "INCOMPLETE"
              ? $t("partner.payment.start")
              : $t("partner.payment.update")
        }}
      </button>
    </template>
  </div>
</template>

<script>
import {
  createPartnerOnboardingLink,
  getPartnerPaymentAccount
} from "../../services/PartnerService";

export default {
  name: "PartnerPaymentAccount",

  data() {
    return {
      status: null,
      loading: true,
      redirecting: false,
      infoMessage: "",
      errorMessage: ""
    };
  },

  async mounted() {
    const stripeReturn = this.$route.query.stripe;

    // Paramètre retiré de l'adresse : un rechargement ne rejoue pas le retour
    if (stripeReturn) {
      this.$router.replace({ query: {} });
    }

    // Lien expiré ou déjà utilisé : Stripe renvoie ici, on en demande un nouveau
    if (stripeReturn === "refresh") {
      await this.startOnboarding();
      return;
    }

    await this.loadStatus();

    if (stripeReturn === "return") {
      this.infoMessage = this.$t("partner.payment.returned");
    }
  },

  computed: {
    statusClass() {
      if (this.status === "ACTIVE") return "partner-payment-status-active";
      if (this.status === "NOT_CREATED") return "partner-payment-status-none";
      return "partner-payment-status-pending";
    }
  },

  methods: {
    async loadStatus() {
      try {
        this.loading = true;
        this.errorMessage = "";
        const response = await getPartnerPaymentAccount();
        this.status = response.data.status;
      } catch (error) {
        console.error(error);
        this.errorMessage = this.$t("partner.payment.loadError");
      } finally {
        this.loading = false;
      }
    },

    async startOnboarding() {
      try {
        this.redirecting = true;
        this.errorMessage = "";
        const response = await createPartnerOnboardingLink();
        window.location.assign(response.data.onboardingUrl);
      } catch (error) {
        console.error(error);
        this.redirecting = false;
        this.errorMessage = this.$t("partner.payment.redirectError");
        await this.loadStatus();
      }
    }
  }
};
</script>

<style scoped src="./PartnerPaymentAccount.css"></style>