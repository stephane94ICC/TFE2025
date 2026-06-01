<template>
  <div v-if="visible" class="admin-review-overlay" @click.self="closeModal">
    <section class="admin-review-modal">
      <h3>Refuser l’activité</h3>

      <p>
        Indiquez la raison du refus. Le partenaire pourra consulter ce commentaire.
      </p>

      <textarea
        v-model="reviewComment"
        rows="5"
        placeholder="Raison du refus..."
      ></textarea>

      <p v-if="errorMessage" class="admin-review-error">
        {{ errorMessage }}
      </p>

      <div class="admin-review-actions">
        <button type="button" class="btn-reject" @click="confirmRejection">
          Confirmer le refus
        </button>

        <button type="button" class="btn-cancel" @click="closeModal">
          Annuler
        </button>
      </div>
    </section>
  </div>
</template>

<script>
export default {
  name: "AdminActivityReviewModal",

  props: {
    visible: { type: Boolean, default: false }
  },

  emits: ["confirm", "close"],

  data() {
    return {
      reviewComment: "",
      errorMessage: ""
    };
  },

  methods: {
    confirmRejection() {
      if (!this.reviewComment.trim()) {
        this.errorMessage = "La raison du refus est obligatoire.";
        return;
      }

      this.$emit("confirm", this.reviewComment.trim());
      this.resetForm();
    },

    closeModal() {
      this.resetForm();
      this.$emit("close");
    },

    resetForm() {
      this.reviewComment = "";
      this.errorMessage = "";
    }
  }
};
</script>

<style scoped src="./AdminActivityReviewModal.css"></style>