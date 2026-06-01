<template>
  <div v-if="visible" class="partner-modal-overlay" @click.self="cancelEdit">
    <section class="partner-modal">
      <header class="partner-modal-header">
        <h3>{{ isEditing ? "Modifier l’adresse" : "Ajouter une adresse" }}</h3>

        <button type="button" class="partner-modal-close" @click="cancelEdit">
          ×
        </button>
      </header>

      <form class="partner-address-form" @submit.prevent="submitForm">
        <label>
          Type d’adresse
          <select v-model="form.addressType" required>
            <option value="LEGAL">Adresse légale</option>
            <option value="CONTACT">Adresse de contact</option>
          </select>
        </label>

        <label>
          Rue
          <input v-model="form.street" type="text" required />
        </label>

        <label>
          Numéro
          <input v-model="form.houseNumber" type="text" required />
        </label>

        <label>
          Boîte
          <input v-model="form.box" type="text" />
        </label>

        <label>
          Code postal
          <input v-model="form.postalCode" type="text" required />
        </label>

        <label>
          Ville
          <input v-model="form.city" type="text" required />
        </label>

        <label>
          Pays
          <input v-model="form.country" type="text" required />
        </label>

        <div class="partner-address-form-actions">
          <button type="submit" :disabled="saving">
            {{ saving ? "Enregistrement..." : "Enregistrer" }}
          </button>

          <button type="button" @click="cancelEdit">
            Annuler
          </button>
        </div>
      </form>
    </section>
  </div>
</template>

<script>
import "./PartnerAddressForm.css";

function createEmptyForm() {
  return {
    street: "",
    houseNumber: "",
    box: "",
    city: "",
    postalCode: "",
    country: "Belgique",
    addressType: "CONTACT"
  };
}

export default {
  name: "PartnerAddressForm",

  props: {
    visible: {
      type: Boolean,
      default: false
    },

    addressToEdit: {
      type: Object,
      default: null
    },

    saving: {
      type: Boolean,
      default: false
    }
  },

  emits: ["save", "cancel"],

  data() {
    return {
      form: createEmptyForm()
    };
  },

  computed: {
    isEditing() {
      return this.addressToEdit !== null;
    }
  },

  watch: {
    addressToEdit: {
      immediate: true,
      deep: true,
      handler(address) {
        this.form = address
          ? {
              street: address.street,
              houseNumber: address.houseNumber,
              box: address.box || "",
              city: address.city,
              postalCode: address.postalCode,
              country: address.country,
              addressType: address.addressType
            }
          : createEmptyForm();
      }
    },

    visible(isVisible) {
      if (isVisible && !this.addressToEdit) {
        this.form = createEmptyForm();
      }
    }
  },

  methods: {
    submitForm() {
      this.$emit("save", { ...this.form });
    },

    cancelEdit() {
      this.form = createEmptyForm();
      this.$emit("cancel");
    }
  }
};
</script>