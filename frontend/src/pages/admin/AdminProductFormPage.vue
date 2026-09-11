<template>
  <div class="admin-product-form-page">
    <section class="admin-product-form-header">
      <h1>{{ isEditMode ? $t("admin.productForm.editTitle") : $t("admin.productForm.addTitle") }}</h1>
      <p>
        {{
          isEditMode
              ? $t("admin.productForm.editDescription")
              : $t("admin.productForm.addDescription")
        }}
      </p>
    </section>

    <p v-if="loading" class="form-loading">
      {{ $t("admin.productForm.loading") }}
    </p>

    <section v-else class="admin-product-form-card">
      <form @submit.prevent="submitForm">
        <div class="form-group">
          <label for="name">{{ $t("admin.productForm.name") }}</label>
          <input
              id="name"
              v-model="product.name"
              type="text"
              :placeholder="$t('admin.productForm.namePlaceholder')"
              required
          />
        </div>

        <div class="form-group">
          <label for="description">{{ $t("admin.productForm.description") }}</label>
          <textarea
              id="description"
              v-model="product.description"
              rows="4"
              :placeholder="$t('admin.productForm.descriptionPlaceholder')"
          ></textarea>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="price">{{ $t("admin.productForm.price") }}</label>
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
            <label for="stockQuantity">{{ $t("admin.productForm.stock") }}</label>
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
          <label for="active">{{ $t("admin.productForm.activeLabel") }}</label>
        </div>

        <div v-if="errorMessage" class="form-error">
          {{ errorMessage }}
        </div>

        <div class="form-actions">
          <router-link to="/admin/products" class="form-secondary-button">
            {{ $t("admin.productForm.cancel") }}
          </router-link>

          <button type="submit" class="form-primary-button" :disabled="saving">
            {{
              saving
                  ? $t("admin.productForm.saving")
                  : isEditMode
                      ? $t("admin.productForm.editButton")
                      : $t("admin.productForm.addButton")
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
            this.errorMessage = this.$t("admin.productForm.loadError");
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
                ? this.$t("admin.productForm.editError")
                : this.$t("admin.productForm.addError");
          })
          .finally(() => {
            this.saving = false;
          });
    }
  }
};
</script>