<template>
  <div class="product-detail-page">
    <router-link to="/shop" class="back-link">
      ← Retour à la boutique
    </router-link>

    <div v-if="errorMessage" class="alert alert-error">
      {{ errorMessage }}
    </div>

    <p v-if="loading">Chargement du produit...</p>

    <div v-else-if="product" class="detail-card">
      <div class="detail-image">
        <img
            v-if="product.imageUrl"
            :src="product.imageUrl"
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
            <strong>Prix</strong>
            <span>{{ product.price }} €</span>
          </div>

          <div class="info-box">
            <strong>Stock</strong>
            <span>{{ product.stockQuantity }} disponible(s)</span>
          </div>

          <div class="info-box">
            <strong>Statut</strong>
            <span>{{ product.active ? 'Disponible' : 'Indisponible' }}</span>
          </div>
        </div>

        <button
            class="btn btn-primary"
            :disabled="!product.active || product.stockQuantity <= 0"
            @click="addProductToCart"
        >
          Ajouter au panier
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { getProductById } from '../services/ProductService';
import CartService from '../services/CartService';

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
            this.errorMessage = "Impossible de charger le détail du produit.";
          })
          .finally(() => {
            this.loading = false;
          });
    },

    addProductToCart() {
      CartService.addToCart(this.product);
      alert("Produit ajouté au panier !");
    }
  }
};
</script>

<style scoped src="./ProductDetailPage.css"></style>