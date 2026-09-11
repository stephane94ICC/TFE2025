<template>
  <div class="shop-page">
    <div class="page-header">
      <div>
        <h1>{{ $t("public.shop.title") }}</h1>
        <p>{{ $t("public.shop.subtitle") }}</p>
      </div>
    </div>

    <div v-if="errorMessage" class="alert alert-error">
      {{ errorMessage }}
    </div>

    <p v-if="loading">
      {{ $t("public.shop.loading") }}
    </p>

    <div v-else-if="products.length" class="product-grid">
      <div v-for="product in products" :key="product.id" class="product-card">
        <div class="product-image">
          <img
              v-if="getProductImage(product)"
              :src="getProductImage(product)"
              :alt="product.name"
          >
          <span v-else>{{ product.name.charAt(0) }}</span>
        </div>

        <div class="product-content">
          <h2>{{ product.name }}</h2>

          <p class="description">
            {{ product.description }}
          </p>

          <div class="product-info">
            <span>{{ product.price }} €</span>
            <span>
              {{ $t("public.shop.stock") }} : {{ product.stockQuantity }}
            </span>
          </div>

          <router-link :to="`/products/${product.id}`" class="btn btn-primary">
            {{ $t("public.shop.details") }}
          </router-link>
        </div>
      </div>
    </div>

    <p v-else>
      {{ $t("public.shop.empty") }}
    </p>
  </div>
</template>

<script>
import { getProducts } from '../../services/ProductService';

export default {
  name: 'ShopPage',

  data() {
    return {
      products: [],
      loading: false,
      errorMessage: ''
    };
  },

  mounted() {
    this.loadProducts();
  },

  methods: {
    loadProducts() {
      this.loading = true;
      this.errorMessage = '';

      getProducts()
          .then(res => {
            this.products = res.data;
          })
          .catch(err => {
            console.error(err);
            this.errorMessage = this.$t("public.shop.loadError");
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
    }
  }
};
</script>

<style scoped src="./ShopPage.css"></style>