<template>
  <div class="admin-products-page">
    <section class="admin-products-header">
      <h1>{{ $t("admin.products.title") }}</h1>
      <p>
        {{ $t("admin.products.description") }}
      </p>
    </section>

    <div v-if="errorMessage" class="admin-products-error">
      {{ errorMessage }}
    </div>

    <div v-if="successMessage" class="admin-products-success">
      {{ successMessage }}
    </div>

    <p v-if="loading" class="admin-products-loading">
      {{ $t("admin.products.loading") }}
    </p>

    <section v-else class="admin-products-card">
      <div class="admin-products-card-header">
        <div>
          <h2>{{ $t("admin.products.listTitle") }}</h2>
          <span>{{ $t("admin.products.productCount", { count: products.length }) }}</span>
        </div>

        <router-link to="/admin/products/new" class="admin-add-button">
          {{ $t("admin.products.add") }}
        </router-link>
      </div>

      <div v-if="products.length" class="admin-products-table-wrapper">
        <table class="admin-products-table">
          <thead>
            <tr>
              <th>{{ $t("admin.products.id") }}</th>
              <th>{{ $t("admin.products.name") }}</th>
              <th>{{ $t("admin.products.price") }}</th>
              <th>{{ $t("admin.products.stock") }}</th>
              <th>{{ $t("admin.products.active") }}</th>
              <th>{{ $t("admin.products.actions") }}</th>
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
                  {{ product.active ? $t("admin.products.yes") : $t("admin.products.no") }}
                </span>
              </td>

              <td>
                <div class="admin-product-actions">
                  <router-link
                    :to="`/admin/products/edit/${product.id}`"
                    class="admin-action-button"
                  >
                    {{ $t("admin.products.edit") }}
                  </router-link>

                  <router-link
                    :to="`/admin/products/${product.id}/images`"
                    class="admin-action-button"
                  >
                    {{ $t("admin.products.manageImages") }}
                  </router-link>

                  <button
                    class="admin-action-button admin-action-danger"
                    :disabled="deletingId === product.id"
                    @click="deleteSelectedProduct(product)"
                  >
                    {{ deletingId === product.id ? $t("admin.products.deleting") : $t("admin.products.delete") }}
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <p v-else class="admin-products-empty">
        {{ $t("admin.products.empty") }}
      </p>
    </section>
  </div>
</template>

<script>
import { deleteProduct, getAllProducts } from "../../services/ProductService";
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
          this.errorMessage = this.$t("admin.products.loadError");
        })
        .finally(() => {
          this.loading = false;
        });
    },

    deleteSelectedProduct(product) {
      const confirmed = confirm(
        this.$t("admin.products.confirmDelete", { name: product.name })
      );

      if (!confirmed) {
        return;
      }

      this.deletingId = product.id;
      this.errorMessage = "";
      this.successMessage = "";

      deleteProduct(product.id)
        .then(() => {
          this.successMessage = this.$t("admin.products.deleteSuccess");
          this.products = this.products.filter(item => item.id !== product.id);
        })
        .catch(error => {
          console.error(error);
          this.errorMessage = this.$t("admin.products.deleteError");
        })
        .finally(() => {
          this.deletingId = null;
        });
    }
  }
};
</script>