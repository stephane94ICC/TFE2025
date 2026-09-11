<template>
  <div class="product-detail-page">
    <router-link to="/shop" class="back-link">
      ← {{ $t("public.productDetail.back") }}
    </router-link>

    <div v-if="errorMessage" class="alert alert-error">
      {{ errorMessage }}
    </div>

    <p v-if="loading">
      {{ $t("public.productDetail.loading") }}
    </p>

    <div v-else-if="product" class="detail-card">
      <div class="detail-image">
        <img
            v-if="getProductImage(product)"
            :src="getProductImage(product)"
            :alt="product.name"
        >
        <span v-else>{{ product.name.charAt(0) }}</span>
      </div>

      <div class="detail-content">
        <h1>{{ product.name }}</h1>

        <p class="description">
          {{ product.description }}
        </p>

        <div class="info-grid">
          <div class="info-box">
            <strong>{{ $t("public.productDetail.price") }}</strong>
            <span>{{ product.price }} €</span>
          </div>

          <div class="info-box">
            <strong>{{ $t("public.productDetail.stock") }}</strong>
            <span>
              {{ product.stockQuantity }}
              {{ $t("public.productDetail.available") }}
            </span>
          </div>

          <div class="info-box">
            <strong>{{ $t("public.productDetail.status") }}</strong>
            <span>
              {{
                product.active
                  ? $t("public.productDetail.availableStatus")
                  : $t("public.productDetail.unavailableStatus")
              }}
            </span>
          </div>
        </div>

        <button
            class="btn btn-primary"
            :disabled="!product.active || product.stockQuantity <= 0"
            @click="addProductToCart"
        >
          {{ $t("public.productDetail.addToCart") }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { getProductById } from '../../services/ProductService';
import CartService from '../../services/CartService';
import AuthService from '../../services/AuthService';

export default {
  name: 'ProductDetailPage',

  data() {
    return {
      product: null,
      loading: false,
      errorMessage: ''
    };
  },

  mounted() {
    this.loadProduct();
  },

  methods: {
    loadProduct() {
      const id = this.$route.params.id;

      this.loading = true;
      this.errorMessage = '';

      getProductById(id)
          .then(res => {
            this.product = res.data;
          })
          .catch(err => {
            console.error(err);
            this.errorMessage = this.$t("public.productDetail.loadError");
          })
          .finally(() => {
            this.loading = false;
          });
    },

    getProductImage(product) {
      if (product.imageUrls && product.imageUrls.length > 0) {
        return product.imageUrls[0];
      }

      return product.imageUrl || null;
    },

    addProductToCart() {
      if (!AuthService.isLoggedIn()) {
        this.$router.push("/login");
        return;
      }

      CartService.addToCart(this.product);
      alert(this.$t("public.productDetail.addedToCart"));
    }
  }
};
</script>

<style scoped src="./ProductDetailPage.css"></style>