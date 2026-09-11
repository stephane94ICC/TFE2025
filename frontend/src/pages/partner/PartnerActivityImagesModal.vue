<template>
  <div
    v-if="visible"
    class="partner-images-modal-overlay"
    @click.self="closeModal"
  >
    <section class="partner-images-modal">
      <header class="partner-images-modal-header">
        <h3>
          {{ $t("partner.activityImages.title") }} — {{ activity ? activity.title : "" }}
        </h3>

        <button type="button" class="partner-images-modal-close" @click="closeModal">
          ×
        </button>
      </header>

      <p v-if="loading">
        {{ $t("partner.activityImages.loading") }}
      </p>

      <template v-else>
        <p v-if="errorMessage" class="form-error">
          {{ errorMessage }}
        </p>

        <p v-if="successMessage" class="form-success">
          {{ successMessage }}
        </p>

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
            {{
              uploading
                ? $t("partner.activityImages.uploading")
                : $t("partner.activityImages.addImage")
            }}
          </button>
        </div>

        <div class="images-grid">
          <div v-for="image in images" :key="image.id" class="image-card">
            <img :src="image.url" :alt="$t('partner.activityImages.imageAlt')" />

            <span v-if="isDefaultImage(image.url)">
              {{ $t("partner.activityImages.defaultImage") }}
            </span>

            <button
              v-else
              type="button"
              :disabled="deletingId === image.id"
              @click="removeImage(image)"
            >
              {{
                deletingId === image.id
                  ? $t("partner.activityImages.deleting")
                  : $t("partner.activityImages.delete")
              }}
            </button>
          </div>

          <p v-if="images.length === 0" class="partner-images-empty">
            {{ $t("partner.activityImages.empty") }}
          </p>
        </div>
      </template>
    </section>
  </div>
</template>

<script>
import {
  getPartnerActivityImages,
  uploadPartnerActivityImage,
  deletePartnerActivityImage
} from "../../services/PartnerService";

import "./PartnerActivityImagesModal.css";

export default {
  name: "PartnerActivityImagesModal",

  props: {
    visible: { type: Boolean, default: false },
    activity: { type: Object, default: null }
  },

  emits: ["close"],

  data() {
    return {
      images: [],
      selectedFile: null,
      loading: false,
      uploading: false,
      deletingId: null,
      errorMessage: "",
      successMessage: ""
    };
  },

  watch: {
    visible(isVisible) {
      if (isVisible && this.activity) {
        this.loadImages();
      }
    }
  },

  methods: {
    loadImages() {
      this.loading = true;
      this.errorMessage = "";
      this.successMessage = "";

      getPartnerActivityImages(this.activity.id)
        .then(response => {
          this.images = response.data;
        })
        .catch(error => {
          console.error(error);
          this.errorMessage = this.$t("partner.activityImages.loadError");
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

      uploadPartnerActivityImage(this.activity.id, this.selectedFile)
        .then(() => {
          this.successMessage = this.$t("partner.activityImages.addSuccess");
          this.selectedFile = null;
          this.loadImages();
        })
        .catch(error => {
          console.error(error);
          this.errorMessage = error.response?.data?.error
            || this.$t("partner.activityImages.addError");
        })
        .finally(() => {
          this.uploading = false;
        });
    },

    removeImage(image) {
      this.deletingId = image.id;
      this.errorMessage = "";
      this.successMessage = "";

      deletePartnerActivityImage(this.activity.id, image.id)
        .then(() => {
          this.images = this.images.filter(item => item.id !== image.id);
          this.successMessage = this.$t("partner.activityImages.deleteSuccess");
        })
        .catch(error => {
          console.error(error);
          this.errorMessage = error.response?.data?.error
            || this.$t("partner.activityImages.deleteError");
        })
        .finally(() => {
          this.deletingId = null;
        });
    },

    isDefaultImage(url) {
      return url.includes("default-activity.png");
    },

    closeModal() {
      this.$emit("close");
      this.images = [];
      this.selectedFile = null;
      this.errorMessage = "";
      this.successMessage = "";
    }
  }
};
</script>