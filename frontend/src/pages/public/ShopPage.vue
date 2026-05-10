<template>
  <div class="shop-page">
    <div class="page-header">
      <div>
        <h1>Boutique</h1>
        <p>Découvrez les produits disponibles pour vos activités de loisirs.</p>
      </div>
    </div>

    <div v-if="errorMessage" class="alert alert-error">
      {{ errorMessage }}
    </div>

    <p v-if="loading">Chargement des produits...</p>

    <div v-else-if="products.length" class="product-grid">
      <div v-for="product in products" :key="product.id" class="product-card">
        <div class="product-image">
          <img
              v-if="product.imageUrl"
              :src="product.imageUrl"
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
            <span>Stock : {{ product.stockQuantity }}</span>
          </div>

          <router-link :to="`/products/${product.id}`" class="btn btn-primary">
            Voir le détail
          </router-link>
        </div>
      </div>
    </div>

    <p v-else>Aucun produit disponible.</p>
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
            this.errorMessage = "Impossible de charger les produits.";
          })
          .finally(() => {
            this.loading = false;
          });
    }
  }
};
</script>

<style scoped src="./ShopPage.css"></style>