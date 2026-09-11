<template>
  <section class="partner-company">
    <header class="partner-company-header">
      <div>
        <h2>{{ $t("partner.companyForm.title") }}</h2>
        <p>{{ $t("partner.companyForm.subtitle") }}</p>
      </div>

      <button type="button" @click="openModal">
        {{ $t("partner.companyForm.edit") }}
      </button>
    </header>

    <div class="partner-company-grid">
      <div>
        <strong>{{ $t("partner.companyForm.name") }}</strong>
        <p>{{ partner.name }}</p>
      </div>

      <div>
        <strong>{{ $t("partner.companyForm.phone") }}</strong>
        <p>{{ partner.phone || $t("partner.companyForm.notProvided") }}</p>
      </div>

      <div>
        <strong>{{ $t("partner.companyForm.email") }}</strong>
        <p>{{ partner.email || $t("partner.companyForm.notProvided") }}</p>
      </div>

      <div>
        <strong>{{ $t("partner.companyForm.website") }}</strong>
        <p>{{ partner.website || $t("partner.companyForm.notProvided") }}</p>
      </div>

      <div>
        <strong>{{ $t("partner.companyForm.enterpriseNumber") }}</strong>
        <p>{{ partner.enterpriseNumber }}</p>
      </div>

      <div>
        <strong>{{ $t("partner.companyForm.vatNumber") }}</strong>
        <p>{{ partner.vatNumber }}</p>
      </div>
    </div>

    <div v-if="partner.description" class="partner-company-description">
      <strong>{{ $t("partner.companyForm.description") }}</strong>
      <p>{{ partner.description }}</p>
    </div>

    <div v-if="showModal" class="partner-modal-overlay" @click.self="closeModal">
      <section class="partner-modal">
        <header class="partner-modal-header">
          <h3>{{ $t("partner.companyForm.editTitle") }}</h3>

          <button type="button" class="partner-modal-close" @click="closeModal">
            ×
          </button>
        </header>

        <form class="partner-form" @submit.prevent="submitForm">
          <label>
            {{ $t("partner.companyForm.companyName") }}
            <input v-model="form.name" type="text" required />
          </label>

          <label>
            {{ $t("partner.companyForm.description") }}
            <textarea v-model="form.description" rows="4"></textarea>
          </label>

          <label>
            {{ $t("partner.companyForm.phone") }}
            <input v-model="form.phone" type="text" />
          </label>

          <label>
            {{ $t("partner.companyForm.email") }}
            <input v-model="form.email" type="email" />
          </label>

          <label>
            {{ $t("partner.companyForm.website") }}
            <input v-model="form.website" type="text" />
          </label>

          <p class="partner-readonly-help">
            {{ $t("partner.companyForm.readonlyHelp") }}
          </p>

          <div class="partner-form-actions">
            <button type="submit" :disabled="saving">
              {{
                saving
                  ? $t("partner.companyForm.saving")
                  : $t("partner.companyForm.save")
              }}
            </button>

            <button type="button" @click="closeModal">
              {{ $t("partner.companyForm.cancel") }}
            </button>
          </div>
        </form>
      </section>
    </div>
  </section>
</template>

<script>
import "./PartnerCompanyForm.css";

function createEditableForm(partner) {
  return {
    name: partner?.name || "",
    description: partner?.description || "",
    phone: partner?.phone || "",
    email: partner?.email || "",
    website: partner?.website || ""
  };
}

export default {
  name: "PartnerCompanyForm",

  props: {
    partner: {
      type: Object,
      required: true
    },

    saving: {
      type: Boolean,
      default: false
    }
  },

  emits: ["save"],

  data() {
    return {
      showModal: false,
      form: createEditableForm(this.partner)
    };
  },

  watch: {
    partner: {
      deep: true,
      handler(newPartner) {
        this.form = createEditableForm(newPartner);
        this.showModal = false;
      }
    }
  },

  methods: {
    openModal() {
      this.form = createEditableForm(this.partner);
      this.showModal = true;
    },

    closeModal() {
      this.showModal = false;
    },

    submitForm() {
      this.$emit("save", { ...this.form });
    }
  }
};
</script>