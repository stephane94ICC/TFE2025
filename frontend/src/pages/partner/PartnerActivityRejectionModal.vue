<template>
  <div v-if="visible" class="partner-rejection-overlay" @click.self="closeModal">
    <section class="partner-rejection-modal">
      <h3>{{ $t("partner.activityRejection.title") }}</h3>

      <p class="partner-rejection-text">
        {{ reviewComment || $t("partner.activityRejection.noComment") }}
      </p>

      <button type="button" class="partner-rejection-close" @click="closeModal">
        {{ $t("partner.activityRejection.close") }}
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