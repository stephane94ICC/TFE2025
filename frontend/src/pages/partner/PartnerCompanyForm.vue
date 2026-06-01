<template>
  <section class="partner-company">
    <header class="partner-company-header">
      <div>
        <h2>Informations générales</h2>
        <p>Coordonnées professionnelles et informations officielles.</p>
      </div>

      <button type="button" @click="openModal">
        Modifier
      </button>
    </header>

    <div class="partner-company-grid">
      <div>
        <strong>Nom</strong>
        <p>{{ partner.name }}</p>
      </div>

      <div>
        <strong>Téléphone</strong>
        <p>{{ partner.phone || "Non renseigné" }}</p>
      </div>

      <div>
        <strong>Email professionnel</strong>
        <p>{{ partner.email || "Non renseigné" }}</p>
      </div>

      <div>
        <strong>Site web</strong>
        <p>{{ partner.website || "Non renseigné" }}</p>
      </div>

      <div>
        <strong>Numéro d’entreprise</strong>
        <p>{{ partner.enterpriseNumber }}</p>
      </div>

      <div>
        <strong>Numéro de TVA</strong>
        <p>{{ partner.vatNumber }}</p>
      </div>
    </div>

    <div v-if="partner.description" class="partner-company-description">
      <strong>Description</strong>
      <p>{{ partner.description }}</p>
    </div>

    <div v-if="showModal" class="partner-modal-overlay" @click.self="closeModal">
      <section class="partner-modal">
        <header class="partner-modal-header">
          <h3>Modifier les informations</h3>

          <button type="button" class="partner-modal-close" @click="closeModal">
            ×
          </button>
        </header>

        <form class="partner-form" @submit.prevent="submitForm">
          <label>
            Nom de l’entreprise
            <input v-model="form.name" type="text" required />
          </label>

          <label>
            Description
            <textarea v-model="form.description" rows="4"></textarea>
          </label>

          <label>
            Téléphone
            <input v-model="form.phone" type="text" />
          </label>

          <label>
            Email professionnel
            <input v-model="form.email" type="email" />
          </label>

          <label>
            Site web
            <input v-model="form.website" type="text" />
          </label>

          <p class="partner-readonly-help">
            Le numéro d’entreprise et le numéro de TVA ne peuvent pas être modifiés ici.
          </p>

          <div class="partner-form-actions">
            <button type="submit" :disabled="saving">
              {{ saving ? "Enregistrement..." : "Enregistrer" }}
            </button>

            <button type="button" @click="closeModal">
              Annuler
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