<template>
  <main class="payment-result-page">
    <section class="payment-result-card">
      <h1>Paiement réussi</h1>

      <p v-if="isReservation">
        Le paiement test Stripe a été accepté. Votre réservation a été
        enregistrée dans la plateforme.
      </p>

      <p v-else>
        Le paiement test Stripe a été accepté. Votre commande a été créée dans la
        plateforme.
      </p>

      <p v-if="sessionId" class="payment-session">
        Session Stripe : {{ sessionId }}
      </p>

      <div class="payment-result-actions">
        <router-link
            :to="isReservation ? '/activities' : '/shop'"
            class="btn btn-primary"
        >
          {{ isReservation ? "Retour aux activités" : "Retour à la boutique" }}
        </router-link>

        <router-link to="/" class="btn btn-secondary">
          Retour à l’accueil
        </router-link>
      </div>
    </section>
  </main>
</template>

<script>
import CartService from "../../services/CartService";

export default {
  name: "PaymentSuccessPage",

  computed: {
    sessionId() {
      return this.$route.query.session_id || "";
    },

    isReservation() {
      return this.$route.query.type === "reservation";
    }
  },

  mounted() {
    if (!this.isReservation) {
      CartService.clearCart();
    }
  }
};
</script>

<style scoped src="./PaymentResultPage.css"></style>