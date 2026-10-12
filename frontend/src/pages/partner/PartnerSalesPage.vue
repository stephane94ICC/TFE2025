<template>
  <div class="partner-sales-page">
    <header class="partner-sales-header">
      <h1>{{ $t("partner.sales.title") }}</h1>
      <p>{{ $t("partner.sales.subtitle") }}</p>
    </header>

    <p v-if="errorMessage" class="partner-sales-error" role="alert">
      {{ errorMessage }}
    </p>

    <p v-if="loading">
      {{ $t("partner.sales.loading") }}
    </p>

    <section v-else class="partner-sales-card">
      <table>
        <thead>
        <tr>
          <th>{{ $t("partner.sales.reference") }}</th>
          <th>{{ $t("partner.sales.activity") }}</th>
          <th>{{ $t("partner.sales.session") }}</th>
          <th>{{ $t("partner.sales.customer") }}</th>
          <th>{{ $t("partner.sales.places") }}</th>
          <th>{{ $t("partner.sales.total") }}</th>
          <th>{{ $t("partner.sales.paidAt") }}</th>
          <th>{{ $t("partner.sales.document") }}</th>
        </tr>
        </thead>

        <tbody>
        <tr v-for="sale in sales" :key="sale.id">
          <td class="partner-sale-reference">
            {{ sale.reference }}

            <span
                v-if="isRefunded(sale)"
                class="partner-sale-refunded"
                :title="$t('partner.sales.refundedHint')"
            >
                {{ $t("partner.sales.refunded") }}
              </span>
          </td>
          <td>{{ sale.activityTitle }}</td>
          <td>{{ formatDateTime(sale.sessionStartAt) }}</td>
          <td>{{ sale.customerName }}</td>
          <td>{{ sale.quantity }}</td>
          <td>{{ formatPrice(sale.totalPrice) }}</td>
          <td>{{ formatDateTime(sale.paidAt) }}</td>

          <td>
            <div v-if="sale.documentType" class="partner-sale-documents">
              <button
                  type="button"
                  class="partner-sale-document-button"
                  :disabled="downloadingKey !== null"
                  @click="download(sale, 'document')"
              >
                {{
                  isDownloading(sale, "document")
                      ? $t("partner.sales.downloading")
                      : documentLabel(sale)
                }}
              </button>

              <button
                  v-if="sale.creditNoteAvailable"
                  type="button"
                  class="partner-sale-document-button"
                  :disabled="downloadingKey !== null"
                  @click="download(sale, 'creditNote')"
              >
                {{
                  isDownloading(sale, "creditNote")
                      ? $t("partner.sales.downloading")
                      : $t("partner.sales.creditNote")
                }}
              </button>
            </div>

            <!-- Confort seulement : le serveur refuse aussi ces documents (409) s'ils sont demandés directement. -->
            <span
                v-else
                class="partner-sale-document-unavailable"
                :title="$t('partner.sales.unavailableHint')"
            >
                {{ $t("partner.sales.unavailable") }}
              </span>
          </td>
        </tr>

        <tr v-if="sales.length === 0">
          <td colspan="8" class="partner-sales-empty">
            {{ $t("partner.sales.empty") }}
          </td>
        </tr>
        </tbody>
      </table>
    </section>
  </div>
</template>

<script>
import i18n from "../../i18n";
import {
  downloadPartnerCreditNote,
  downloadPartnerReceipt,
  getPartnerSales
} from "../../services/PartnerService";

import "./PartnerSalesPage.css";

// Les deux documents d'une vente : le document de vente (facture, ou justificatif si
// l'adresse du client manque ; le serveur choisit) et la note de crédit qui annule la facture.
const DOWNLOADS = {
  document: downloadPartnerReceipt,
  creditNote: downloadPartnerCreditNote
};

// Codes renvoyés par le serveur avec un 409 : le message affiché est traduit ici.
const ERROR_KEYS = {
  RECEIPT_NOT_AVAILABLE: "partner.sales.errors.notAvailable",
  CREDIT_NOTE_NOT_AVAILABLE: "partner.sales.errors.creditNoteNotAvailable",
  PARTNER_LEGAL_ADDRESS_MISSING: "partner.sales.errors.legalAddressMissing"
};

export default {
  name: "PartnerSalesPage",

  data() {
    return {
      sales: [],
      loading: true,
      errorMessage: "",
      // Téléchargement en cours (« id-document » ou « id-creditNote ») : un seul à la fois.
      downloadingKey: null
    };
  },

  mounted() {
    this.loadSales();
  },

  methods: {
    async loadSales() {
      try {
        this.loading = true;
        this.errorMessage = "";

        const response = await getPartnerSales();
        this.sales = response.data;
      } catch (error) {
        console.error(error);
        this.errorMessage = this.$t("partner.sales.loadError");
      } finally {
        this.loading = false;
      }
    },

    // Seules les ventes remboursées qui ont une facture reviennent avec le statut CANCELLED.
    isRefunded(sale) {
      return sale.status === "CANCELLED";
    },

    documentLabel(sale) {
      return sale.documentType === "INVOICE"
          ? this.$t("partner.sales.invoice")
          : this.$t("partner.sales.receipt");
    },

    isDownloading(sale, kind) {
      return this.downloadingKey === `${sale.id}-${kind}`;
    },

    async download(sale, kind) {
      try {
        this.downloadingKey = `${sale.id}-${kind}`;
        this.errorMessage = "";

        // Le PDF sort dans la langue choisie dans la barre de navigation.
        const response = await DOWNLOADS[kind](sale.id, i18n.global.locale.value);
        this.saveFile(response.data, this.fileName(response, sale));
      } catch (error) {
        console.error(error);
        this.errorMessage = await this.errorText(error);
      } finally {
        this.downloadingKey = null;
      }
    },

    // Fait enregistrer le PDF reçu par le navigateur, sous le nom proposé par le serveur.
    saveFile(blob, fileName) {
      const url = URL.createObjectURL(blob);
      const link = document.createElement("a");

      link.href = url;
      link.download = fileName;
      document.body.appendChild(link);
      link.click();
      link.remove();

      // Laisse au navigateur le temps de démarrer le téléchargement.
      setTimeout(() => URL.revokeObjectURL(url), 1000);
    },

    // Nom donné par le serveur (en-tête Content-Disposition), par exemple facture-12-2026-0001.pdf.
    fileName(response, sale) {
      const header = response.headers["content-disposition"] || "";
      const match = header.match(/filename="?([^";]+)"?/);

      return match ? match[1] : `${sale.reference}.pdf`;
    },

    // Avec responseType "blob", la réponse d'erreur JSON arrive elle aussi en blob : on la relit.
    async errorText(error) {
      const status = error.response?.status;

      if (status === 404) {
        return this.$t("partner.sales.errors.notFound");
      }

      if (status === 409) {
        try {
          const body = JSON.parse(await error.response.data.text());
          const key = ERROR_KEYS[body.code];

          if (key) {
            return this.$t(key);
          }
        } catch (parseError) {
          console.error(parseError);
        }
      }

      return this.$t("partner.sales.errors.generic");
    },

    formatDateTime(value) {
      if (!value) {
        return "-";
      }

      return new Date(value).toLocaleString("fr-BE", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit"
      });
    },

    formatPrice(price) {
      if (price === null || price === undefined) {
        return "-";
      }

      return this.$price(price);
    }
  }
};
</script>