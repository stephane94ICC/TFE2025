<template>
  <div class="partner-logo-section">
    <img :src="logoUrl" alt="Logo de l’entreprise" class="partner-logo" />

    <div class="partner-logo-actions">
      <input
        ref="fileInput"
        type="file"
        accept=".jpg,.jpeg,.png,.webp"
        @change="selectLogo"
      />

      <button
        type="button"
        :disabled="!selectedLogo || uploading"
        @click="submitLogo"
      >
        {{ uploading ? "Envoi..." : "Modifier le logo" }}
      </button>
    </div>
  </div>
</template>

<script>
import "./PartnerLogoForm.css";
export default {
  name: "PartnerLogoForm",

  props: {
    logoUrl: {
      type: String,
      required: true
    },

    uploading: {
      type: Boolean,
      default: false
    }
  },

  emits: ["upload"],

  data() {
    return {
      selectedLogo: null
    };
  },

  methods: {
    selectLogo(event) {
      this.selectedLogo = event.target.files[0] || null;
    },

    submitLogo() {
      if (!this.selectedLogo) return;

      this.$emit("upload", this.selectedLogo);
      this.selectedLogo = null;
      this.$refs.fileInput.value = "";
    }
  }
};
</script>
