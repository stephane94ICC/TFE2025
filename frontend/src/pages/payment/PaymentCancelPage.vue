<template>
  <main class="payment-result-page">
    <section class="payment-result-card">
      <h1>Paiement annulé</h1>

      <p v-if="isReservation">
        Le paiement Stripe a été annulé. Votre réservation n’a pas été confirmée
        et les places ont été libérées.
      </p>

      <p v-else>
        Le paiement Stripe a été annulé. Votre panier est conservé pour permettre
        un nouvel essai.
      </p>

      <p v-if="cancelMessage" class="payment-result-info">
        {{ cancelMessage }}
      </p>

      <div class="payment-result-actions">
        <router-link
            :to="isReservation ? '/activities' : '/cart'"
            class="btn btn-primary"
        >
          {{ isReservation ? "Retour aux activités" : "Retour au panier" }}
        </router-link>

        <router-link to="/" class="btn btn-secondary">
          Retour à l’accueil
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
        this.cancelMessage = "La réservation liée à ce paiement a été annulée.";
      } catch (error) {
        console.error("Impossible d'annuler la réservation Stripe :", error);
        this.cancelMessage =
            "Le paiement est annulé, mais la réservation n'a pas pu être mise à jour automatiquement.";
      }

      return;
    }

    try {
      await PaymentService.cancelCheckoutSession(this.sessionId);
      this.cancelMessage = "La commande liée à ce paiement a été annulée.";
    } catch (error) {
      console.error("Impossible d'annuler la commande Stripe :", error);
      this.cancelMessage =
          "Le paiement est annulé, mais la commande n'a pas pu être mise à jour automatiquement.";
    }
  }
};
</script>

<style scoped src="./PaymentResultPage.css"></style>