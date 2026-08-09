<template>
  <main class="payment-result-page">
    <section class="payment-result-card">
      <h1>Paiement annulé</h1>

      <p>
        Le paiement Stripe a été annulé. Votre panier est conservé pour permettre un nouvel essai.
      </p>

      <p v-if="cancelMessage" class="payment-result-info">
        {{ cancelMessage }}
      </p>

      <div class="payment-result-actions">
        <router-link to="/cart" class="btn btn-primary">
          Retour au panier
        </router-link>

        <router-link to="/shop" class="btn btn-secondary">
          Retour à la boutique
        </router-link>
      </div>
    </section>
  </main>
</template>

<script>
import PaymentService from "../../services/PaymentService";

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
    }
  },

  async mounted() {
    if (!this.sessionId) {
      return;
    }

    try {
      await PaymentService.cancelCheckoutSession(this.sessionId);
      this.cancelMessage = "La commande liée à ce paiement a été annulée.";
    } catch (error) {
      console.error("Impossible d'annuler la commande Stripe :", error);
      this.cancelMessage = "Le paiement est annulé, mais la commande n'a pas pu être mise à jour automatiquement.";
    }
  }
};
</script>

<style scoped src="./PaymentResultPage.css"></style>