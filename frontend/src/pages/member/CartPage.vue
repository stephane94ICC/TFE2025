<template>
  <div class="cart-page">
    <header class="cart-header">
      <h1>{{ $t("member.cart.title") }}</h1>
      <p v-if="cart.length > 0">{{ $t("member.cart.subtitle") }}</p>
    </header>

    <p v-if="errorMessage" class="cart-alert" role="alert">
      {{ errorMessage }}
    </p>

    <div v-if="cart.length === 0" class="cart-empty">
      <p>{{ $t("member.cart.empty") }}</p>

      <router-link to="/shop" class="cart-button cart-button--primary">
        {{ $t("member.cart.continueShopping") }}
      </router-link>
    </div>

    <div v-else class="cart-layout">
      <ul class="cart-items">
        <li v-for="item in cart" :key="item.id" class="cart-item">
          <div class="cart-item__image">
            <img v-if="item.imageUrl" :src="item.imageUrl" alt="">
            <span v-else aria-hidden="true">{{ item.name.charAt(0) }}</span>
          </div>

          <div class="cart-item__info">
            <h2 class="cart-item__name">{{ item.name }}</h2>

            <p class="cart-item__unit-price">
              {{ $t("member.cart.unitPrice", { price: $price(item.price) }) }}
            </p>

            <div class="cart-item__controls">
              <div class="cart-quantity" role="group" :aria-label="$t('member.cart.quantity')">
                <button
                  type="button"
                  class="cart-quantity__button"
                  :disabled="item.quantity <= 1"
                  :aria-label="$t('member.cart.decrease', { name: item.name })"
                  @click="changeQuantity(item, -1)"
                >
                  −
                </button>

                <span class="cart-quantity__value">{{ item.quantity }}</span>

                <button
                  type="button"
                  class="cart-quantity__button"
                  :disabled="item.quantity >= maxQuantity"
                  :aria-label="$t('member.cart.increase', { name: item.name })"
                  @click="changeQuantity(item, 1)"
                >
                  +
                </button>
              </div>

              <button type="button" class="cart-item__remove" @click="removeItem(item.id)">
                {{ $t("member.cart.delete") }}
              </button>
            </div>
          </div>

          <p class="cart-item__total">{{ $price(item.price * item.quantity) }}</p>
        </li>
      </ul>

      <aside class="cart-summary">
        <h2>{{ $t("member.cart.summaryTitle") }}</h2>

        <dl class="cart-summary__lines">
          <div class="cart-summary__line">
            <dt>{{ $t("member.cart.items") }}</dt>
            <dd>{{ itemCount }}</dd>
          </div>

          <div class="cart-summary__line cart-summary__line--total">
            <dt>{{ $t("member.cart.total") }}</dt>
            <dd>{{ $price(total) }}</dd>
          </div>
        </dl>

        <button
          type="button"
          class="cart-button cart-button--primary cart-button--block"
          :disabled="paymentLoading"
          @click="payWithStripe"
        >
          {{ paymentLoading ? $t("member.cart.stripeRedirect") : $t("member.cart.payWithStripe") }}
        </button>

        <button type="button" class="cart-summary__clear" @click="clearCart">
          {{ $t("member.cart.clear") }}
        </button>

        <!-- Information avant l'achat : lien vers les CGV, ouvert dans un nouvel onglet -->
        <p class="cart-summary__terms">
          {{ $t("public.terms.paymentNotice") }}
          <router-link to="/conditions-generales" target="_blank" rel="noopener">
            {{ $t("public.terms.termsLink") }}</router-link>.
        </p>
      </aside>
    </div>
  </div>
</template>

<script>
import CartService from "../../services/CartService";
import PaymentService from "../../services/PaymentService";

// Le stock réel est contrôlé par le serveur au paiement ; ici, seulement une borne d'interface.
const MAX_QUANTITY = 99;

export default {
  name: "CartPage",

  data() {
    return {
      cart: [],
      maxQuantity: MAX_QUANTITY,
      paymentLoading: false,
      errorMessage: ""
    };
  },

  computed: {
    total() {
      return this.cart.reduce((sum, item) => sum + item.price * item.quantity, 0);
    },

    itemCount() {
      return this.cart.reduce((sum, item) => sum + item.quantity, 0);
    }
  },

  mounted() {
    this.cart = CartService.getCart();
  },

  methods: {
    changeQuantity(item, delta) {
      const quantity = item.quantity + delta;

      if (quantity < 1 || quantity > MAX_QUANTITY) {
        return;
      }

      CartService.updateQuantity(item.id, quantity);
      this.cart = CartService.getCart();
    },

    removeItem(productId) {
      CartService.removeFromCart(productId);
      this.cart = CartService.getCart();
    },

    clearCart() {
      if (!confirm(this.$t("member.cart.confirmClear"))) {
        return;
      }

      CartService.clearCart();
      this.cart = [];
    },

    async payWithStripe() {
      this.errorMessage = "";

      if (this.cart.length === 0) {
        this.errorMessage = this.$t("member.cart.empty");
        return;
      }

      this.paymentLoading = true;

      try {
        const response = await PaymentService.createCheckoutSession(this.cart);
        const checkoutUrl = response.data.checkoutUrl;

        if (!checkoutUrl) {
          throw new Error("URL Stripe absente.");
        }

        // Le bouton reste désactivé pendant la redirection vers Stripe.
        window.location.href = checkoutUrl;
      } catch (error) {
        console.error(error);
        this.errorMessage = this.$t("member.cart.paymentError");
        this.paymentLoading = false;
      }
    }
  }
};
</script>

<style scoped src="./CartPage.css"></style>
