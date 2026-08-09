<template>
  <div
    v-if="visible"
    class="partner-images-modal-overlay"
    @click.self="closeModal"
  >
    <section class="partner-images-modal">
      <header class="partner-images-modal-header">
        <h3>Images — {{ activity ? activity.title : "" }}</h3>

        <button type="button" class="partner-images-modal-close" @click="closeModal">
          ×
        </button>
      </header>

      <p v-if="loading">Chargement...</p>

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
            {{ uploading ? "Envoi..." : "Ajouter une image" }}
          </button>
        </div>

        <div class="images-grid">
          <div v-for="image in images" :key="image.id" class="image-card">
            <img :src="image.url" alt="Image de l’activité" />

            <span v-if="isDefaultImage(image.url)">
              Image par défaut
            </span>

            <button
              v-else
              type="button"
              :disabled="deletingId === image.id"
              @click="removeImage(image)"
            >
              {{ deletingId === image.id ? "Suppression..." : "Supprimer" }}
            </button>
          </div>

          <p v-if="images.length === 0" class="partner-images-empty">
            Aucune image pour cette activité.
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
          this.errorMessage = "Impossible de charger les images.";
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
          this.successMessage = "Image ajoutée avec succès.";
          this.selectedFile = null;
          this.loadImages();
        })
        .catch(error => {
          console.error(error);
          this.errorMessage = error.response?.data?.error
            || "Impossible d’ajouter l’image.";
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
          this.successMessage = "Image supprimée avec succès.";
        })
        .catch(error => {
          console.error(error);
          this.errorMessage = error.response?.data?.error
            || "Impossible de supprimer l’image.";
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