<template>
  <div v-if="visible" class="partner-modal-overlay" @click.self="cancelEdit">
    <section class="partner-modal">
      <header class="partner-modal-header">
        <h3>{{ isEditing ? "Modifier le lieu" : "Ajouter un lieu" }}</h3>

        <button type="button" class="partner-modal-close" @click="cancelEdit">
          ×
        </button>
      </header>

      <form class="partner-location-form" @submit.prevent="submitForm">
        <label>
          Nom du lieu
          <input v-model="form.name" type="text" required />
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

        <p class="partner-location-form-hint">
          Coordonnées GPS (facultatif) — dans Google Maps, clic droit sur le
          lieu : les coordonnées se copient automatiquement.
        </p>

        <label>
          Latitude
          <input v-model="form.latitude" type="text" />
        </label>

        <label>
          Longitude
          <input v-model="form.longitude" type="text" />
        </label>

        <label>
          Informations d’accès
          <textarea v-model="form.accessInformation" rows="3"></textarea>
        </label>

        <div class="partner-location-form-actions">
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
import "./PartnerLocationForm.css";

function createEmptyForm() {
  return {
    name: "",
    street: "",
    houseNumber: "",
    box: "",
    city: "",
    postalCode: "",
    country: "Belgique",
    latitude: "",
    longitude: "",
    accessInformation: ""
  };
}

export default {
  name: "PartnerLocationForm",

  props: {
    visible: {
      type: Boolean,
      default: false
    },

    locationToEdit: {
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
      return this.locationToEdit !== null;
    }
  },

  watch: {
    locationToEdit: {
      immediate: true,
      deep: true,
      handler(location) {
        this.form = location
          ? {
              name: location.name,
              street: location.street,
              houseNumber: location.houseNumber,
              box: location.box || "",
              city: location.city,
              postalCode: location.postalCode,
              country: location.country,
              latitude: location.latitude ?? "",
              longitude: location.longitude ?? "",
              accessInformation: location.accessInformation || ""
            }
          : createEmptyForm();
      }
    },

    visible(isVisible) {
      if (isVisible && !this.locationToEdit) {
        this.form = createEmptyForm();
      }
    }
  },

  methods: {
    submitForm() {
      const payload = { ...this.form };

      payload.latitude = this.toNumberOrNull(payload.latitude);
      payload.longitude = this.toNumberOrNull(payload.longitude);

      this.$emit("save", payload);
    },

    toNumberOrNull(value) {
      if (value === null || value === undefined || String(value).trim() === "") {
        return null;
      }

      const parsed = Number(String(value).replace(",", "."));

      return Number.isNaN(parsed) ? null : parsed;
    },

    cancelEdit() {
      this.form = createEmptyForm();
      this.$emit("cancel");
    }
  }
};
</script>