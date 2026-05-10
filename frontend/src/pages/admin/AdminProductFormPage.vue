<template>
  <div class="admin-product-form-page">
    <section class="admin-product-form-header">
      <h1>{{ isEditMode ? "Modifier un produit" : "Ajouter un produit" }}</h1>
      <p>
        {{
          isEditMode
              ? "Ce formulaire permet à l’administrateur de modifier un produit existant."
              : "Ce formulaire permet à l’administrateur d’ajouter un nouveau produit dans la boutique."
        }}
      </p>
    </section>

    <p v-if="loading" class="form-loading">
      Chargement du produit...
    </p>

    <section v-else class="admin-product-form-card">
      <form @submit.prevent="submitForm">
        <div class="form-group">
          <label for="name">Nom du produit</label>
          <input
              id="name"
              v-model="product.name"
              type="text"
              placeholder="Exemple : Tapis de yoga"
              required
          />
        </div>

        <div class="form-group">
          <label for="description">Description</label>
          <textarea
              id="description"
              v-model="product.description"
              rows="4"
              placeholder="Description du produit"
          ></textarea>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="price">Prix</label>
            <input
                id="price"
                v-model.number="product.price"
                type="number"
                min="0"
                step="0.01"
                required
            />
          </div>

          <div class="form-group">
            <label for="stockQuantity">Stock</label>
            <input
                id="stockQuantity"
                v-model.number="product.stockQuantity"
                type="number"
                min="0"
                required
            />
          </div>
        </div>

        <div class="form-check">
          <input
              id="active"
              v-model="product.active"
              type="checkbox"
          />
          <label for="active">Produit actif dans la boutique</label>
        </div>

        <div v-if="errorMessage" class="form-error">
          {{ errorMessage }}
        </div>

        <div class="form-actions">
          <router-link to="/admin/products" class="form-secondary-button">
            Annuler
          </router-link>

          <button type="submit" class="form-primary-button" :disabled="saving">
            {{
              saving
                  ? "Enregistrement..."
                  : isEditMode
                      ? "Modifier le produit"
                      : "Ajouter le produit"
            }}
          </button>
        </div>
      </form>
    </section>
  </div>
</template>

<script>
import {
  createProduct,
  getAdminProductById,
  updateProduct
} from "../../services/ProductService";
import "./AdminProductFormPage.css";

export default {
  name: "AdminProductFormPage",

  data() {
    return {
      product: {
        name: "",
        description: "",
        price: 0,
        stockQuantity: 0,
        active: true
      },
      loading: false,
      saving: false,
      errorMessage: ""
    };
  },

  computed: {
    isEditMode() {
      return Boolean(this.$route.params.id);
    },

    productId() {
      return this.$route.params.id;
    }
  },

  mounted() {
    if (this.isEditMode) {
      this.loadProduct();
    }
  },

  methods: {
    loadProduct() {
      this.loading = true;
      this.errorMessage = "";

      getAdminProductById(this.productId)
          .then(response => {
            this.product = response.data;
          })
          .catch(error => {
            console.error(error);
            this.errorMessage = "Impossible de charger le produit.";
          })
          .finally(() => {
            this.loading = false;
          });
    },

    submitForm() {
      this.saving = true;
      this.errorMessage = "";

      const request = this.isEditMode
          ? updateProduct(this.productId, this.product)
          : createProduct(this.product);

      request
          .then(() => {
            this.$router.push("/admin/products");
          })
          .catch(error => {
            console.error(error);
            this.errorMessage = this.isEditMode
                ? "Impossible de modifier le produit."
                : "Impossible d’ajouter le produit.";
          })
          .finally(() => {
            this.saving = false;
          });
    }
  }
};
</script>