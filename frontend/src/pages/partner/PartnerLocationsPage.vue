<template>
  <div class="partner-locations-page">
    <header class="partner-locations-header">
      <div>
        <h1>{{ $t("partner.locations.title") }}</h1>
        <p>
          {{ $t("partner.locations.subtitle") }}
        </p>
      </div>

      <button
        type="button"
        class="partner-location-add-link"
        @click="openCreateForm"
      >
        {{ $t("partner.locations.add") }}
      </button>
    </header>

    <p v-if="successMessage" class="partner-locations-success">
      {{ successMessage }}
    </p>

    <p v-if="errorMessage" class="partner-locations-error">
      {{ errorMessage }}
    </p>

    <p v-if="loading">
      {{ $t("partner.locations.loading") }}
    </p>

    <section v-else class="partner-locations-card">
      <table>
        <thead>
          <tr>
            <th>{{ $t("partner.locations.name") }}</th>
            <th>{{ $t("partner.locations.address") }}</th>
            <th>{{ $t("partner.locations.city") }}</th>
            <th>{{ $t("partner.locations.coordinates") }}</th>
            <th>{{ $t("partner.locations.access") }}</th>
            <th>{{ $t("partner.locations.actions") }}</th>
          </tr>
        </thead>

        <tbody>
          <tr v-for="location in locations" :key="location.id">
            <td>{{ location.name }}</td>
            <td>{{ formatStreet(location) }}</td>
            <td>{{ location.postalCode }} {{ location.city }}</td>

            <td>
              <span v-if="hasCoordinates(location)" class="partner-location-gps">
                {{ location.latitude }}, {{ location.longitude }}
              </span>
              <span v-else class="partner-location-gps-missing">
                {{ $t("partner.locations.notProvided") }}
              </span>
            </td>

            <td>{{ location.accessInformation || "-" }}</td>

            <td class="partner-location-actions">
              <button
                type="button"
                class="partner-location-edit-link"
                @click="openEditForm(location)"
              >
                {{ $t("partner.locations.edit") }}
              </button>
            </td>
          </tr>

          <tr v-if="locations.length === 0">
            <td colspan="6" class="partner-locations-empty">
              {{ $t("partner.locations.empty") }}
            </td>
          </tr>
        </tbody>
      </table>
    </section>

    <PartnerLocationForm
      :visible="showLocationForm"
      :location-to-edit="selectedLocation"
      :saving="savingLocation"
      @save="saveLocation"
      @cancel="closeLocationForm"
    />
  </div>
</template>

<script>
import {
  addPartnerLocation,
  getPartnerLocations,
  updatePartnerLocation
} from "../../services/PartnerService";

import PartnerLocationForm from "./PartnerLocationForm.vue";

import "./PartnerLocationsPage.css";

export default {
  name: "PartnerLocationsPage",

  components: {
    PartnerLocationForm
  },

  data() {
    return {
      locations: [],
      selectedLocation: null,
      showLocationForm: false,
      savingLocation: false,
      loading: true,
      successMessage: "",
      errorMessage: ""
    };
  },

  mounted() {
    this.loadLocations();
  },

  methods: {
    async loadLocations() {
      try {
        this.loading = true;
        this.errorMessage = "";

        const response = await getPartnerLocations();
        this.locations = response.data;
      } catch (error) {
        console.error(error);
        this.errorMessage = this.$t("partner.locations.loadError");
      } finally {
        this.loading = false;
      }
    },

    openCreateForm() {
      this.selectedLocation = null;
      this.showLocationForm = true;
    },

    openEditForm(location) {
      this.selectedLocation = location;
      this.showLocationForm = true;
    },

    closeLocationForm() {
      this.showLocationForm = false;
      this.selectedLocation = null;
    },

    async saveLocation(form) {
      try {
        this.savingLocation = true;
        this.errorMessage = "";
        this.successMessage = "";

        if (this.selectedLocation) {
          await updatePartnerLocation(this.selectedLocation.id, form);
          this.successMessage = this.$t("partner.locations.updateSuccess");
        } else {
          await addPartnerLocation(form);
          this.successMessage = this.$t("partner.locations.addSuccess");
        }

        this.closeLocationForm();
        await this.loadLocations();
      } catch (error) {
        console.error(error);
        this.errorMessage = this.$t("partner.locations.saveError");
      } finally {
        this.savingLocation = false;
      }
    },

    formatStreet(location) {
      const box = location.box
        ? ` ${this.$t("partner.locations.boxShort")} ${location.box}`
        : "";

      return `${location.street} ${location.houseNumber}${box}`;
    },

    hasCoordinates(location) {
      return location.latitude !== null && location.longitude !== null;
    }
  }
};
</script>