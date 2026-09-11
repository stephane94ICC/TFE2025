<template>
  <div
    v-if="visible"
    class="partner-activity-modal-overlay"
    @click.self="closeForm"
  >
    <section class="partner-activity-modal">
      <header class="partner-activity-modal-header">
        <h3>
          {{
            isEditMode
              ? $t("partner.activityForm.editTitle")
              : $t("partner.activityForm.addTitle")
          }}
        </h3>

        <button type="button" class="partner-activity-modal-close" @click="closeForm">
          ×
        </button>
      </header>

      <p class="partner-activity-form-note">
        {{ $t("partner.activityForm.note") }}
      </p>

      <p v-if="errorMessage" class="partner-activity-form-error">
        {{ errorMessage }}
      </p>

      <form @submit.prevent="saveActivity">
        <label>
          {{ $t("partner.activityForm.title") }}
          <input v-model="form.title" type="text" required />
        </label>

        <label>
          {{ $t("partner.activityForm.description") }}
          <textarea v-model="form.description" rows="5"></textarea>
        </label>

        <label>
          {{ $t("partner.activityForm.price") }}
          <input v-model.number="form.price" type="number" min="0" step="0.01" required />
        </label>

        <label>
          {{ $t("partner.activityForm.duration") }}
          <input v-model.number="form.durationMinutes" type="number" min="1" required />
        </label>

        <label>
          {{ $t("partner.activityForm.minimumAge") }}
          <input v-model.number="form.minimumAge" type="number" min="0" required />
        </label>

        <label>
          {{ $t("partner.activityForm.equipmentInformation") }}
          <textarea v-model="form.equipmentInformation" rows="3"></textarea>
        </label>

        <div class="partner-activity-form-actions">
          <button type="submit" :disabled="saving">
            {{
              saving
                ? $t("partner.activityForm.saving")
                : $t("partner.activityForm.save")
            }}
          </button>

          <button type="button" class="partner-activity-form-cancel" @click="closeForm">
            {{ $t("partner.activityForm.cancel") }}
          </button>
        </div>
      </form>
    </section>
  </div>
</template>

<script>
import {
  addPartnerActivity,
  updatePartnerActivity
} from "../../services/PartnerService";

import "./PartnerActivityFormPage.css";

function createEmptyForm() {
  return {
    title: "",
    description: "",
    price: null,
    durationMinutes: null,
    minimumAge: 0,
    equipmentInformation: ""
  };
}

export default {
  name: "PartnerActivityFormPage",

  props: {
    visible: { type: Boolean, default: false },
    activityToEdit: { type: Object, default: null }
  },

  emits: ["saved", "close"],

  data() {
    return {
      form: createEmptyForm(),
      saving: false,
      errorMessage: ""
    };
  },

  computed: {
    isEditMode() {
      return this.activityToEdit !== null;
    }
  },

  watch: {
    activityToEdit: {
      immediate: true,
      deep: true,
      handler(activity) {
        this.form = activity
          ? {
              title: activity.title || "",
              description: activity.description || "",
              price: activity.price ?? null,
              durationMinutes: activity.durationMinutes ?? null,
              minimumAge: activity.minimumAge ?? 0,
              equipmentInformation: activity.equipmentInformation || ""
            }
          : createEmptyForm();
      }
    },

    visible(isVisible) {
      if (isVisible && !this.activityToEdit) {
        this.form = createEmptyForm();
      }
    }
  },

  methods: {
    async saveActivity() {
      try {
        this.saving = true;
        this.errorMessage = "";

        if (this.isEditMode) {
          await updatePartnerActivity(this.activityToEdit.id, this.form);
        } else {
          await addPartnerActivity(this.form);
        }

        this.$emit("saved");
      } catch (error) {
        console.error(error);
        this.errorMessage = this.$t("partner.activityForm.saveError");
      } finally {
        this.saving = false;
      }
    },

    closeForm() {
      this.errorMessage = "";
      this.form = createEmptyForm();
      this.$emit("close");
    }
  }
};
</script>