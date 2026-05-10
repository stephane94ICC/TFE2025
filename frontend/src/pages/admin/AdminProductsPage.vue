<template>
  <div class="admin-products-page">
    <section class="admin-products-header">
      <h1>Gestion des produits</h1>
      <p>
        Cette page permet à l’administrateur de consulter les produits de la boutique.
      </p>
    </section>

    <div v-if="errorMessage" class="admin-products-error">
      {{ errorMessage }}
    </div>

    <div v-if="successMessage" class="admin-products-success">
      {{ successMessage }}
    </div>

    <p v-if="loading" class="admin-products-loading">
      Chargement des produits...
    </p>

    <section v-else class="admin-products-card">
      <div class="admin-products-card-header">
        <div>
          <h2>Liste des produits</h2>
          <span>{{ products.length }} produit(s)</span>
        </div>

        <router-link to="/admin/products/new" class="admin-add-button">
          Ajouter un produit
        </router-link>
      </div>

      <div v-if="products.length" class="admin-products-table-wrapper">
        <table class="admin-products-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Nom</th>
              <th>Prix</th>
              <th>Stock</th>
              <th>Actif</th>
              <th>Actions</th>
            </tr>
          </thead>

          <tbody>
            <tr v-for="product in products" :key="product.id">
              <td>{{ product.id }}</td>
              <td>{{ product.name }}</td>
              <td>{{ product.price }} €</td>
              <td>{{ product.stockQuantity }}</td>
              <td>
                <span
                  class="status-badge"
                  :class="product.active ? 'status-active' : 'status-inactive'"
                >
                  {{ product.active ? "Oui" : "Non" }}
                </span>
              </td>
              <td>
                <div class="admin-product-actions">
                  <router-link
                    :to="`/admin/products/edit/${product.id}`"
                    class="admin-action-button"
                  >
                    Modifier
                  </router-link>

                  <button
                    class="admin-action-button admin-action-danger"
                    @click="deleteSelectedProduct(product)"
                    :disabled="deletingId === product.id"
                  >
                    {{ deletingId === product.id ? "Suppression..." : "Supprimer" }}
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <p v-else class="admin-products-empty">
        Aucun produit trouvé.
      </p>
    </section>
  </div>
</template>

<script>
import { getAllProducts, deleteProduct } from "../../services/ProductService";
import "./AdminProductsPage.css";

export default {
  name: "AdminProductsPage",

  data() {
    return {
      products: [],
      loading: false,
      deletingId: null,
      errorMessage: "",
      successMessage: ""
    };
  },

  mounted() {
    this.loadProducts();
  },

  methods: {
    loadProducts() {
      this.loading = true;
      this.errorMessage = "";
      this.successMessage = "";

      getAllProducts()
        .then(response => {
          this.products = response.data;
        })
        .catch(error => {
          console.error(error);
          this.errorMessage = "Impossible de charger les produits.";
        })
        .finally(() => {
          this.loading = false;
        });
    },

    deleteSelectedProduct(product) {
      const confirmed = confirm(
        `Voulez-vous vraiment supprimer le produit "${product.name}" ?`
      );

      if (!confirmed) {
        return;
      }

      this.deletingId = product.id;
      this.errorMessage = "";
      this.successMessage = "";

      deleteProduct(product.id)
        .then(() => {
          this.successMessage = "Produit supprimé avec succès.";
          this.products = this.products.filter(item => item.id !== product.id);
        })
        .catch(error => {
          console.error(error);
          this.errorMessage = "Impossible de supprimer le produit.";
        })
        .finally(() => {
          this.deletingId = null;
        });
    }
  }
};
</script>