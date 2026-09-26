<template>
  <div class="admin-partners-page">
    <header class="admin-partners-header">
      <h1>{{ $t("admin.partners.title") }}</h1>
      <p>{{ $t("admin.partners.subtitle") }}</p>
    </header>

    <p v-if="errorMessage" class="admin-partners-alert admin-partners-alert-error">
      {{ errorMessage }}
    </p>
    <p v-if="successMessage" class="admin-partners-alert admin-partners-alert-success">
      {{ successMessage }}
    </p>

    <p v-if="loading">{{ $t("admin.partners.loading") }}</p>

    <p v-else-if="partners.length === 0">{{ $t("admin.partners.empty") }}</p>

    <template v-else>
      <p class="admin-partners-hint">{{ $t("admin.partners.rateHint") }}</p>

      <!-- Un tableau large défile dans son propre conteneur, jamais la page entière -->
      <div class="admin-partners-table-wrapper">
        <table class="admin-partners-table">
          <thead>
            <tr>
              <th>{{ $t("admin.partners.name") }}</th>
              <th>{{ $t("admin.partners.vatNumber") }}</th>
              <th>{{ $t("admin.partners.commission") }}</th>
              <th>{{ $t("admin.partners.paymentStatus") }}</th>
              <th>{{ $t("admin.partners.actions") }}</th>
            </tr>
          </thead>

          <tbody>
            <tr v-for="partner in partners" :key="partner.id">
              <td>
                <strong>{{ partner.name }}</strong>
                <span v-if="partner.email" class="admin-partners-email">{{ partner.email }}</span>
              </td>

              <td>{{ partner.vatNumber }}</td>

              <td>
                <!--
                  Premier niveau de défense : le navigateur bloque un taux
                  hors fourchette. Le serveur (DTO) et la base (CHECK) suivent.
                -->
                <form class="admin-partners-rate-form" @submit.prevent="saveRate(partner)">
                  <input
                    v-model.number="rates[partner.id]"
                    type="number"
                    min="1"
                    max="8"
                    step="0.01"
                    required
                    :aria-label="$t('admin.partners.commission')"
                  />
                  <button
                    type="submit"
                    class="admin-partners-btn admin-partners-btn-secondary"
                    :disabled="savingId === partner.id"
                  >
                    {{ $t("admin.partners.saveRate") }}
                  </button>
                </form>
              </td>

              <td>
                <span :class="['admin-partners-status', statusClass(partner.paymentStatus)]">
                  {{ statusLabel(partner.paymentStatus) }}
                </span>
              </td>

              <td>
                <button
                  v-if="partner.paymentStatus === 'NOT_CREATED'"
                  type="button"
                  class="admin-partners-btn admin-partners-btn-primary"
                  :disabled="creatingId === partner.id"
                  @click="createAccount(partner)"
                >
                  {{
                    creatingId === partner.id
                      ? $t("admin.partners.creating")
                      : $t("admin.partners.createAccount")
                  }}
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </template>
  </div>
</template>

<script>
import {
  createPartnerPaymentAccount,
  getAdminPartners,
  updatePartnerCommissionRate
} from "../../services/AdminPartnerService";

export default {
  name: "AdminPartnersPage",

  data() {
    return {
      partners: [],
      // Valeur saisie par ligne, séparée de la donnée reçue :
      // une saisie non enregistrée ne modifie pas la liste affichée.
      rates: {},
      loading: true,
      savingId: null,
      creatingId: null,
      errorMessage: "",
      successMessage: ""
    };
  },

  mounted() {
    this.loadPartners();
  },

  methods: {
    async loadPartners() {
      try {
        this.loading = true;
        const response = await getAdminPartners();
        this.partners = response.data;
        this.rates = Object.fromEntries(
          this.partners.map(partner => [partner.id, Number(partner.commissionRate)])
        );
      } catch (error) {
        console.error(error);
        this.errorMessage = this.$t("admin.partners.loadError");
      } finally {
        this.loading = false;
      }
    },

    async saveRate(partner) {
      try {
        this.savingId = partner.id;
        this.clearMessages();

        const response = await updatePartnerCommissionRate(partner.id, this.rates[partner.id]);
        this.replacePartner(response.data);
        this.successMessage = this.$t("admin.partners.rateSaved", { name: partner.name });
      } catch (error) {
        console.error(error);
        this.errorMessage = error.response?.status === 400
          ? this.$t("admin.partners.rateError")
          : this.$t("admin.partners.saveError");
      } finally {
        this.savingId = null;
      }
    },

    async createAccount(partner) {
      const message = this.$t("admin.partners.confirmCreateAccount", { name: partner.name });
      if (!window.confirm(message)) return;

      try {
        this.creatingId = partner.id;
        this.clearMessages();

        const response = await createPartnerPaymentAccount(partner.id);
        this.replacePartner(response.data);
        this.successMessage = this.$t("admin.partners.accountCreated", { name: partner.name });
      } catch (error) {
        console.error(error);
        this.errorMessage = this.$t("admin.partners.createError");
      } finally {
        this.creatingId = null;
      }
    },

    replacePartner(updated) {
      this.partners = this.partners.map(partner =>
        partner.id === updated.id ? updated : partner
      );
      this.rates[updated.id] = Number(updated.commissionRate);
    },

    // null : Stripe n'a pas répondu, la liste s'affiche quand même
    statusLabel(status) {
      return this.$t(`admin.partners.statuses.${status || "UNAVAILABLE"}`);
    },

    statusClass(status) {
      if (status === "ACTIVE") return "admin-partners-status-active";
      if (status === "INCOMPLETE" || status === "PENDING_VERIFICATION") {
        return "admin-partners-status-pending";
      }
      return "admin-partners-status-none";
    },

    clearMessages() {
      this.errorMessage = "";
      this.successMessage = "";
    }
  }
};
</script>

<style scoped src="./AdminPartnersPage.css"></style>
