<template>
  <div class="partner-locations-page">
    <header class="partner-locations-header">
      <div>
        <h1>Mes lieux</h1>
        <p>
          Lieux où se déroulent vos activités.
          Un lieu peut être réutilisé pour plusieurs créneaux.
        </p>
      </div>

      <button
        type="button"
        class="partner-location-add-link"
        @click="openCreateForm"
      >
        Ajouter un lieu
      </button>
    </header>

    <p v-if="successMessage" class="partner-locations-success">
      {{ successMessage }}
    </p>

    <p v-if="errorMessage" class="partner-locations-error">
      {{ errorMessage }}
    </p>

    <p v-if="loading">
      Chargement des lieux...
    </p>

    <section v-else class="partner-locations-card">
      <table>
        <thead>
          <tr>
            <th>Nom</th>
            <th>Adresse</th>
            <th>Ville</th>
            <th>Coordonnées</th>
            <th>Accès</th>
            <th>Actions</th>
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
                Non renseignées
              </span>
            </td>

            <td>{{ location.accessInformation || "-" }}</td>

            <td class="partner-location-actions">
              <button
                type="button"
                class="partner-location-edit-link"
                @click="openEditForm(location)"
              >
                Modifier
              </button>
            </td>
          </tr>

          <tr v-if="locations.length === 0">
            <td colspan="6" class="partner-locations-empty">
              Aucun lieu enregistré.
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
        this.errorMessage = "Impossible de charger les lieux.";
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
          this.successMessage = "Lieu modifié avec succès.";
        } else {
          await addPartnerLocation(form);
          this.successMessage = "Lieu ajouté avec succès.";
        }

        this.closeLocationForm();
        await this.loadLocations();
      } catch (error) {
        console.error(error);
        this.errorMessage = "Impossible d’enregistrer le lieu.";
      } finally {
        this.savingLocation = false;
      }
    },

    formatStreet(location) {
      const box = location.box ? ` bte ${location.box}` : "";

      return `${location.street} ${location.houseNumber}${box}`;
    },

    hasCoordinates(location) {
      return location.latitude !== null && location.longitude !== null;
    }
  }
};
</script>