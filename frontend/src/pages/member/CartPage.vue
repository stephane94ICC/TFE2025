<template>
  <div class="cart-page">
    <h1>Mon panier</h1>

    <div v-if="errorMessage" class="cart-alert cart-alert-error">
      {{ errorMessage }}
    </div>

    <div v-if="cart.length === 0" class="empty-cart">
      Votre panier est vide.
    </div>

    <div v-else class="cart-content">
      <div
        v-for="item in cart"
        :key="item.id"
        class="cart-item"
      >
        <div class="cart-item-image">
          <img
            v-if="item.imageUrl"
            :src="item.imageUrl"
            :alt="item.name"
          >
          <span v-else>{{ item.name.charAt(0) }}</span>
        </div>

        <div class="cart-item-info">
          <h2>{{ item.name }}</h2>
          <p>{{ item.description }}</p>
          <p class="price">{{ item.price }} €</p>

          <label>
            Quantité :
            <input
              type="number"
              min="1"
              :value="item.quantity"
              @change="updateQuantity(item.id, $event)"
            >
          </label>

          <p class="subtotal">
            Sous-total : {{ (item.price * item.quantity).toFixed(2) }} €
          </p>

          <button class="btn btn-danger" @click="removeItem(item.id)">
            Supprimer
          </button>
        </div>
      </div>

      <div class="cart-summary">
        <h2>Total : {{ total.toFixed(2) }} €</h2>

        <button class="btn btn-secondary" @click="clearCart">
          Vider le panier
        </button>

        <button
          class="btn btn-primary"
          :disabled="paymentLoading"
          @click="payWithStripe"
        >
          {{ paymentLoading ? "Redirection vers Stripe..." : "Payer avec Stripe" }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import CartService from "../../services/CartService";
import PaymentService from "../../services/PaymentService";

export default {
  name: "CartPage",

  data() {
    return {
      cart: [],
      paymentLoading: false,
      errorMessage: ""
    };
  },

  computed: {
    total() {
      return this.cart.reduce((sum, item) => {
        return sum + item.price * item.quantity;
      }, 0);
    }
  },

  mounted() {
    this.cart = CartService.getCart();
  },

  methods: {
    updateQuantity(productId, event) {
      const quantity = Number(event.target.value);
      CartService.updateQuantity(productId, quantity);
      this.cart = CartService.getCart();
    },

    removeItem(productId) {
      CartService.removeFromCart(productId);
      this.cart = CartService.getCart();
    },

    clearCart() {
      CartService.clearCart();
      this.cart = [];
    },

    payWithStripe() {
      this.errorMessage = "";

      if (this.cart.length === 0) {
        this.errorMessage = "Votre panier est vide.";
        return;
      }

      this.paymentLoading = true;

      PaymentService.createCheckoutSession(this.cart)
        .then(response => {
          const checkoutUrl = response.data.checkoutUrl;

          if (!checkoutUrl) {
            throw new Error("URL Stripe absente.");
          }

          window.location.href = checkoutUrl;
        })
        .catch(error => {
          console.error(error);
          this.errorMessage = "Impossible de démarrer le paiement Stripe.";
        })
        .finally(() => {
          this.paymentLoading = false;
        });
    }
  }
};
</script>

<style scoped src="./CartPage.css"></style>
