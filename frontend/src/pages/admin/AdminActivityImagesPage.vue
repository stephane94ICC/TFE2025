<template>
  <div class="admin-activity-images-page">
    <h1>{{ $t("admin.activityImages.title") }}</h1>

    <p v-if="loading">{{ $t("admin.activityImages.loading") }}</p>

    <template v-else>
      <p v-if="activity">
        {{ $t("admin.activityImages.activityLabel") }}
        <strong>{{ activity.title }}</strong>
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
          {{ uploading ? $t("admin.activityImages.uploading") : $t("admin.activityImages.addImage") }}
        </button>
      </div>

      <div class="images-grid">
        <div v-for="image in images" :key="image.id" class="image-card">
          <img :src="image.url" :alt="$t('admin.activityImages.imageAlt')" />

          <span v-if="isDefaultImage(image.url)">
            {{ $t("admin.activityImages.defaultImage") }}
          </span>

          <button
              v-else
              type="button"
              :disabled="deletingId === image.id"
              @click="removeImage(image)"
          >
            {{ deletingId === image.id ? $t("admin.activityImages.deleting") : $t("admin.activityImages.delete") }}
          </button>
        </div>
      </div>

      <router-link to="/admin/activities">
        {{ $t("admin.activityImages.backToList") }}
      </router-link>
    </template>
  </div>
</template>

<script>
import {
  deleteActivityImage,
  getAdminActivityById,
  getActivityImages,
  uploadActivityImage
} from "../../services/ActivityService";

export default {
  name: "AdminActivityImagesPage",

  data() {
    return {
      activity: null,
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
    activityId() {
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
        getAdminActivityById(this.activityId),
        getActivityImages(this.activityId)
      ])
          .then(([activityResponse, imagesResponse]) => {
            this.activity = activityResponse.data;
            this.images = imagesResponse.data;
          })
          .catch(error => {
            console.error(error);
            this.errorMessage = this.$t("admin.activityImages.loadError");
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

      uploadActivityImage(this.activityId, this.selectedFile)
          .then(() => {
            this.successMessage = this.$t("admin.activityImages.addSuccess");
            this.selectedFile = null;
            this.loadData();
          })
          .catch(error => {
            console.error(error);
            this.errorMessage = error.response?.data?.error
                || this.$t("admin.activityImages.addError");
          })
          .finally(() => {
            this.uploading = false;
          });
    },

    removeImage(image) {
      this.deletingId = image.id;
      this.errorMessage = "";
      this.successMessage = "";

      deleteActivityImage(this.activityId, image.id)
          .then(() => {
            this.images = this.images.filter(item => item.id !== image.id);
            this.successMessage = this.$t("admin.activityImages.deleteSuccess");
          })
          .catch(error => {
            console.error(error);
            this.errorMessage = error.response?.data?.error
                || this.$t("admin.activityImages.deleteError");
          })
          .finally(() => {
            this.deletingId = null;
          });
    },

    isDefaultImage(url) {
      return url.includes("default-activity.png");
    }
  }
};
</script>

<style scoped src="./AdminActivityImagesPage.css"></style>