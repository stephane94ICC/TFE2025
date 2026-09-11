<template>
  <main class="payment-result-page">
    <section class="payment-result-card">
      <h1>{{ $t("payment.success.title") }}</h1>

      <p v-if="isReservation">
        {{ $t("payment.success.reservationMessage") }}
      </p>

      <p v-else>
        {{ $t("payment.success.orderMessage") }}
      </p>

      <p v-if="sessionId" class="payment-session">
        {{ $t("payment.success.stripeSession") }} : {{ sessionId }}
      </p>

      <div class="payment-result-actions">
        <router-link
            :to="isReservation ? '/activities' : '/shop'"
            class="btn btn-primary"
        >
          {{
            isReservation
              ? $t("payment.success.backActivities")
              : $t("payment.success.backShop")
          }}
        </router-link>

        <router-link to="/" class="btn btn-secondary">
          {{ $t("payment.success.backHome") }}
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