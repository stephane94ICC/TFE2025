<template>
  <div v-if="visible" class="partner-rejection-overlay" @click.self="closeModal">
    <section class="partner-rejection-modal">
      <h3>Raison du refus</h3>

      <p class="partner-rejection-text">
        {{ reviewComment || "Aucun commentaire n’a été indiqué par l’administrateur." }}
      </p>

      <button type="button" class="partner-rejection-close" @click="closeModal">
        Fermer
      </button>
    </section>
  </div>
</template>

<script>
export default {
  name: "PartnerActivityRejectionModal",

  props: {
    visible: { type: Boolean, default: false },
    reviewComment: { type: String, default: "" }
  },

  emits: ["close"],

  methods: {
    closeModal() {
      this.$emit("close");
    }
  }
};
</script>

<style scoped src="./PartnerActivityRejectionModal.css"></style>