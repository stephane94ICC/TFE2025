<template>
  <main class="payment-result-page">
    <section class="payment-result-card">
      <h1>{{ $t("payment.cancel.title") }}</h1>

      <p v-if="isReservation">
        {{ $t("payment.cancel.reservationMessage") }}
      </p>

      <p v-else>
        {{ $t("payment.cancel.orderMessage") }}
      </p>

      <p v-if="cancelMessage" class="payment-result-info">
        {{ cancelMessage }}
      </p>

      <div class="payment-result-actions">
        <router-link
            :to="isReservation ? '/activities' : '/cart'"
            class="btn btn-primary"
        >
          {{
            isReservation
              ? $t("payment.cancel.backActivities")
              : $t("payment.cancel.backCart")
          }}
        </router-link>

        <router-link to="/" class="btn btn-secondary">
          {{ $t("payment.cancel.backHome") }}
        </router-link>
      </div>
    </section>
  </main>
</template>

<script>
import PaymentService from "../../services/PaymentService";
import ReservationService from "../../services/ReservationService";

export default {
  name: "PaymentCancelPage",

  data() {
    return {
      cancelMessage: ""
    };
  },

  computed: {
    sessionId() {
      return this.$route.query.session_id || "";
    },

    isReservation() {
      return this.$route.query.type === "reservation";
    }
  },

  async mounted() {
    if (!this.sessionId) {
      return;
    }

    if (this.isReservation) {
      try {
        await ReservationService.cancelCheckoutSession(this.sessionId);
        this.cancelMessage = this.$t(
          "payment.cancel.reservationCancelSuccess"
        );
      } catch (error) {
        console.error("Impossible d'annuler la réservation Stripe :", error);
        this.cancelMessage = this.$t(
          "payment.cancel.reservationCancelError"
        );
      }

      return;
    }

    try {
      await PaymentService.cancelCheckoutSession(this.sessionId);
      this.cancelMessage = this.$t(
        "payment.cancel.orderCancelSuccess"
      );
    } catch (error) {
      console.error("Impossible d'annuler la commande Stripe :", error);
      this.cancelMessage = this.$t(
        "payment.cancel.orderCancelError"
      );
    }
  }
};
</script>

<style scoped src="./PaymentResultPage.css"></style>