<template>
  <div v-if="visible" class="admin-review-overlay" @click.self="closeModal">
    <section class="admin-review-modal">
      <h3>{{ $t("admin.activityReview.title") }}</h3>

      <p>
        {{ $t("admin.activityReview.instruction") }}
      </p>

      <textarea
        v-model="reviewComment"
        rows="5"
        :placeholder="$t('admin.activityReview.placeholder')"
      ></textarea>

      <p v-if="errorMessage" class="admin-review-error">
        {{ errorMessage }}
      </p>

      <div class="admin-review-actions">
        <button type="button" class="btn-reject" @click="confirmRejection">
          {{ $t("admin.activityReview.confirm") }}
        </button>

        <button type="button" class="btn-cancel" @click="closeModal">
          {{ $t("admin.activityReview.cancel") }}
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
        this.errorMessage = this.$t("admin.activityReview.requiredError");
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