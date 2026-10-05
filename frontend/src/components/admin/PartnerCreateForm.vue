<template>
  <form class="partner-create" novalidate @submit.prevent="submit">
    <h2 class="partner-create-title">{{ $t("admin.partners.create.title") }}</h2>
    <p class="partner-create-intro">{{ $t("admin.partners.create.intro") }}</p>

    <p v-if="errorMessage" class="partner-create-error" role="alert">{{ errorMessage }}</p>

    <fieldset class="partner-create-section">
      <legend>{{ $t("admin.partners.create.accountSection") }}</legend>

      <div class="partner-create-grid">
        <label class="partner-create-field">
          <span>{{ $t("admin.partners.create.email") }} *</span>
          <input v-model="form.email" type="email" autocomplete="off" maxlength="255" required />
        </label>

        <label class="partner-create-field">
          <span>{{ $t("admin.partners.create.password") }} *</span>
          <input v-model="form.password" type="password" autocomplete="new-password" required />
          <small>{{ $t("admin.users.passwordRule") }}</small>
        </label>

        <label class="partner-create-field">
          <span>{{ $t("admin.partners.create.firstName") }} *</span>
          <input v-model="form.firstName" type="text" maxlength="100" required />
        </label>

        <label class="partner-create-field">
          <span>{{ $t("admin.partners.create.lastName") }} *</span>
          <input v-model="form.lastName" type="text" maxlength="100" required />
        </label>
      </div>
    </fieldset>

    <fieldset class="partner-create-section">
      <legend>{{ $t("admin.partners.create.companySection") }}</legend>

      <div class="partner-create-grid">
        <label class="partner-create-field">
          <span>{{ $t("admin.partners.create.name") }} *</span>
          <input v-model="form.name" type="text" maxlength="255" required />
        </label>

        <label class="partner-create-field">
          <span>{{ $t("admin.partners.create.enterpriseNumber") }} *</span>
          <!-- Mise en forme 0123.456.749 pendant la saisie ; un « BE » collé est ignoré -->
          <input
              :value="form.enterpriseNumber"
              type="text"
              inputmode="numeric"
              maxlength="12"
              placeholder="0123.456.749"
              required
              @input="onEnterpriseNumberInput"
          />
          <small>{{ $t("admin.partners.create.enterpriseNumberHint") }}</small>
          <!-- Retour immédiat : TVA déduite si le numéro est valide, sinon la règle -->
          <small v-if="vatPreview" class="partner-create-vat">
            {{ $t("admin.partners.vatNumber") }} <strong>{{ vatPreview }}</strong>
          </small>
          <small v-else-if="enterpriseNumberComplete" class="partner-create-invalid">
            {{ $t("admin.partners.create.invalidEnterpriseNumber") }}
          </small>
        </label>

        <label class="partner-create-field">
          <span>{{ $t("admin.partners.create.phone") }} ({{ $t("admin.partners.create.optional") }})</span>
          <input v-model="form.phone" type="tel" maxlength="20" />
        </label>

        <label class="partner-create-field">
          <span>{{ $t("admin.partners.create.contactEmail") }} ({{ $t("admin.partners.create.optional") }})</span>
          <input v-model="form.contactEmail" type="email" maxlength="255" />
        </label>

        <label class="partner-create-field">
          <span>{{ $t("admin.partners.create.website") }} ({{ $t("admin.partners.create.optional") }})</span>
          <input v-model="form.website" type="url" maxlength="255" placeholder="https://" />
        </label>
      </div>
    </fieldset>

    <div class="partner-create-actions">
      <button type="submit" class="partner-create-btn partner-create-btn-primary" :disabled="submitting">
        {{ submitting ? $t("admin.partners.create.submitting") : $t("admin.partners.create.submit") }}
      </button>
      <button type="button" class="partner-create-btn partner-create-btn-secondary" :disabled="submitting" @click="$emit('cancel')">
        {{ $t("admin.partners.create.cancel") }}
      </button>
    </div>
  </form>
</template>

<script>
import { createPartner } from "../../services/AdminPartnerService";
import { isStrongPassword } from "../../utils/passwordPolicy";
import { isValidEnterpriseNumber, normalizeEnterpriseNumber } from "../../utils/enterpriseNumber";

const REQUIRED_FIELDS = ["email", "password", "firstName", "lastName", "name", "enterpriseNumber"];
const OPTIONAL_FIELDS = ["phone", "contactEmail", "website"];

function emptyForm() {
  return {
    email: "",
    password: "",
    firstName: "",
    lastName: "",
    name: "",
    enterpriseNumber: "",
    phone: "",
    contactEmail: "",
    website: ""
  };
}

/*
 * Création d'un partenaire par l'admin : compte de connexion + fiche entreprise.
 * Les contrôles ci-dessous sont un confort ; le serveur revalide tout et décide.
 * Pas de champ TVA : elle est déduite du n° d'entreprise côté serveur.
 */
export default {
  name: "PartnerCreateForm",

  emits: ["created", "cancel"],

  data() {
    return {
      form: emptyForm(),
      submitting: false,
      errorMessage: ""
    };
  },

  computed: {
    enterpriseNumberComplete() {
      return normalizeEnterpriseNumber(this.form.enterpriseNumber).length === 10;
    },

    // Aperçu seulement : le serveur déduit lui-même la TVA (BE + numéro)
    vatPreview() {
      return isValidEnterpriseNumber(this.form.enterpriseNumber)
          ? "BE" + normalizeEnterpriseNumber(this.form.enterpriseNumber)
          : "";
    }
  },

  methods: {
    // Garde les chiffres (10 max) et ajoute les points : 0123.456.749
    onEnterpriseNumberInput(event) {
      const digits = event.target.value.replace(/\D/g, "").slice(0, 10);
      let formatted = digits;

      if (digits.length > 7) {
        formatted = `${digits.slice(0, 4)}.${digits.slice(4, 7)}.${digits.slice(7)}`;
      } else if (digits.length > 4) {
        formatted = `${digits.slice(0, 4)}.${digits.slice(4)}`;
      }

      this.form.enterpriseNumber = formatted;
      event.target.value = formatted;
    },

    async submit() {
      this.errorMessage = "";

      if (REQUIRED_FIELDS.some(field => !this.form[field].trim())) {
        this.errorMessage = this.$t("admin.partners.create.required");
        return;
      }
      if (!isStrongPassword(this.form.password)) {
        this.errorMessage = this.$t("admin.partners.create.passwordTooWeak");
        return;
      }
      if (!isValidEnterpriseNumber(this.form.enterpriseNumber)) {
        this.errorMessage = this.$t("admin.partners.create.invalidEnterpriseNumber");
        return;
      }

      try {
        this.submitting = true;
        const response = await createPartner(this.buildPayload());
        this.form = emptyForm();
        this.$emit("created", response.data);
      } catch (error) {
        // Jamais l'objet d'erreur entier : sa configuration contient le mot de passe.
        console.error("Création du partenaire refusée, statut :", error.response?.status);
        this.errorMessage = this.messageFor(error);
      } finally {
        this.submitting = false;
      }
    },

    buildPayload() {
      const payload = {};

      REQUIRED_FIELDS.forEach(field => {
        payload[field] = field === "password" ? this.form.password : this.form[field].trim();
      });
      OPTIONAL_FIELDS.forEach(field => {
        const value = this.form[field].trim();
        payload[field] = value === "" ? null : value;
      });

      return payload;
    },

    // Deux 409 possibles : le code distingue le n° d'entreprise de l'e-mail.
    messageFor(error) {
      const status = error.response?.status;
      const code = error.response?.data?.code;

      if (status === 409 && code === "ENTERPRISE_NUMBER_ALREADY_USED") {
        return this.$t("admin.partners.create.enterpriseNumberTaken");
      }
      if (status === 409) {
        return this.$t("admin.partners.create.emailTaken");
      }
      if (status === 400) {
        return this.$t("admin.partners.create.invalidData");
      }
      return this.$t("admin.partners.create.error");
    }
  }
};
</script>

<style scoped src="./PartnerCreateForm.css"></style>