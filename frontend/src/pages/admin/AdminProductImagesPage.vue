<template>
  <div class="admin-product-images-page">
    <h1>{{ $t("admin.productImages.title") }}</h1>

    <p v-if="loading">{{ $t("admin.productImages.loading") }}</p>

    <template v-else>
      <p v-if="product">
        {{ $t("admin.productImages.productLabel") }} <strong>{{ product.name }}</strong>
      </p>

      <div v-if="errorMessage" class="form-error">
        {{ errorMessage }}
      </div>

      <div v-if="successMessage" class="form-success">
        {{ successMessage }}
      </div>

      <div class="upload-zone">
        <input
            type="file"
            accept=".jpg,.jpeg,.png,.webp"
            @change="selectFile"
        />

        <button
            type="button"
            :disabled="!selectedFile || uploading"
            @click="uploadImage"
        >
          {{ uploading ? $t("admin.productImages.uploading") : $t("admin.productImages.addImage") }}
        </button>
      </div>

      <div class="images-grid">
        <div v-for="image in images" :key="image.id" class="image-card">
          <img :src="image.url" :alt="$t('admin.productImages.imageAlt')" />

          <span v-if="isDefaultImage(image.url)">
            {{ $t("admin.productImages.defaultImage") }}
          </span>

          <button
              v-else
              type="button"
              :disabled="deletingId === image.id"
              @click="removeImage(image)"
          >
            {{ deletingId === image.id ? $t("admin.productImages.deleting") : $t("admin.productImages.delete") }}
          </button>
        </div>
      </div>

      <router-link to="/admin/products">
        {{ $t("admin.productImages.backToList") }}
      </router-link>
    </template>
  </div>
</template>

<script>
import {
  deleteProductImage,
  getAdminProductById,
  getProductImages,
  uploadProductImage
} from "../../services/ProductService";

export default {
  name: "AdminProductImagesPage",

  data() {
    return {
      product: null,
      images: [],
      selectedFile: null,
      loading: false,
      uploading: false,
      deletingId: null,
      errorMessage: "",
      successMessage: ""
    };
  },

  computed: {
    productId() {
      return this.$route.params.id;
    }
  },

  mounted() {
    this.loadData();
  },

  methods: {
    loadData() {
      this.loading = true;

      Promise.all([
        getAdminProductById(this.productId),
        getProductImages(this.productId)
      ])
          .then(([productResponse, imagesResponse]) => {
            this.product = productResponse.data;
            this.images = imagesResponse.data;
          })
          .catch(error => {
            console.error(error);
            this.errorMessage = this.$t("admin.productImages.loadError");
          })
          .finally(() => {
            this.loading = false;
          });
    },

    selectFile(event) {
      this.selectedFile = event.target.files[0] || null;
    },

    uploadImage() {
      if (!this.selectedFile) {
        return;
      }

      this.uploading = true;
      this.errorMessage = "";
      this.successMessage = "";

      uploadProductImage(this.productId, this.selectedFile)
          .then(() => {
            this.successMessage = this.$t("admin.productImages.addSuccess");
            this.selectedFile = null;
            this.loadData();
          })
          .catch(error => {
            console.error(error);
            this.errorMessage = error.response?.data?.error
                || this.$t("admin.productImages.addError");
          })
          .finally(() => {
            this.uploading = false;
          });
    },

    removeImage(image) {
      this.deletingId = image.id;
      this.errorMessage = "";
      this.successMessage = "";

      deleteProductImage(this.productId, image.id)
          .then(() => {
            this.images = this.images.filter(item => item.id !== image.id);
            this.successMessage = this.$t("admin.productImages.deleteSuccess");
          })
          .catch(error => {
            console.error(error);
            this.errorMessage = error.response?.data?.error
                || this.$t("admin.productImages.deleteError");
          })
          .finally(() => {
            this.deletingId = null;
          });
    },

    isDefaultImage(url) {
      return url.includes("default-product.png");
    }
  }
};
</script>

<style scoped src="./AdminProductImagesPage.css"></style>