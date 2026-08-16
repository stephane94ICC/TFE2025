<template>
  <div
    v-if="visible"
    class="partner-sessions-modal-overlay"
    @click.self="closeModal"
  >
    <section class="partner-sessions-modal">
      <header class="partner-sessions-modal-header">
        <h3>Créneaux — {{ activity ? activity.title : "" }}</h3>

        <button
          type="button"
          class="partner-sessions-modal-close"
          @click="closeModal"
        >
          ×
        </button>
      </header>

      <p v-if="loading">Chargement...</p>

      <template v-else>
        <p v-if="errorMessage" class="form-error">
          {{ errorMessage }}
        </p>

        <p v-if="successMessage" class="form-success">
          {{ successMessage }}
        </p>

        <form class="partner-session-form" @submit.prevent="submitSession">
          <h4>Ajouter un créneau</h4>

          <p v-if="locations.length === 0" class="partner-session-no-location">
            Vous devez d’abord enregistrer un lieu dans « Mes lieux ».
          </p>

          <template v-else>
            <div class="partner-session-form-grid">
              <label>
                Lieu
                <select v-model="form.locationId" required>
                  <option value="">-- Choisir un lieu --</option>
                  <option
                    v-for="location in locations"
                    :key="location.id"
                    :value="location.id"
                  >
                    {{ location.name }} ({{ location.city }})
                  </option>
                </select>
              </label>

              <label>
                Capacité
                <input v-model="form.capacity" type="number" min="1" required />
              </label>

              <label>
                Début
                <input v-model="form.startAt" type="datetime-local" required />
              </label>

              <label>
                Fin
                <input v-model="form.endAt" type="datetime-local" required />
              </label>

              <label>
                Clôture des réservations
                <select v-model="form.deadlineOffsetHours" required>
                  <option value="2">2 heures avant le début</option>
                  <option value="24">24 heures avant le début</option>
                  <option value="48">48 heures avant le début</option>
                  <option value="168">1 semaine avant le début</option>
                </select>
              </label>
            </div>

            <div class="partner-session-form-actions">
              <button type="submit" :disabled="saving">
                {{ saving ? "Enregistrement..." : "Ajouter le créneau" }}
              </button>
            </div>
          </template>
        </form>

        <table class="partner-sessions-table">
          <thead>
            <tr>
              <th>Début</th>
              <th>Fin</th>
              <th>Lieu</th>
              <th>Places</th>
              <th>Clôture</th>
              <th>Statut</th>
            </tr>
          </thead>

          <tbody>
            <tr v-for="session in sessions" :key="session.id">
              <td>{{ formatDateTime(session.startAt) }}</td>
              <td>{{ formatDateTime(session.endAt) }}</td>
              <td>{{ session.locationName }}</td>

              <td>
                <span :class="seatsClass(session)">
                  {{ session.remainingSeats }} / {{ session.capacity }}
                </span>
              </td>

              <td>{{ formatDateTime(session.bookingDeadline) }}</td>

              <td>
                <span
                  :class="[
                    'partner-session-status',
                    statusClass(session.status)
                  ]"
                >
                  {{ statusLabel(session.status) }}
                </span>
              </td>
            </tr>

            <tr v-if="sessions.length === 0">
              <td colspan="6" class="partner-sessions-empty">
                Aucun créneau pour cette activité.
              </td>
            </tr>
          </tbody>
        </table>
      </template>
    </section>
  </div>
</template>

<script>
import {
  addPartnerActivitySession,
  getPartnerActivitySessions,
  getPartnerLocations
} from "../../services/PartnerService";

import "./PartnerActivitySessionsModal.css";

function createEmptyForm() {
  return {
    locationId: "",
    startAt: "",
    endAt: "",
    capacity: "",
    deadlineOffsetHours: "24"
  };
}

export default {
  name: "PartnerActivitySessionsModal",

  props: {
    visible: {
      type: Boolean,
      default: false
    },

    activity: {
      type: Object,
      default: null
    }
  },

  emits: ["close"],

  data() {
    return {
      sessions: [],
      locations: [],
      form: createEmptyForm(),
      loading: false,
      saving: false,
      errorMessage: "",
      successMessage: ""
    };
  },

  watch: {
    visible(isVisible) {
      if (isVisible && this.activity) {
        this.form = createEmptyForm();
        this.successMessage = "";
        this.loadData();
      }
    }
  },

  methods: {
    async loadData() {
      try {
        this.loading = true;
        this.errorMessage = "";
        this.sessions = [];

        const [sessionsResponse, locationsResponse] = await Promise.all([
          getPartnerActivitySessions(this.activity.id),
          getPartnerLocations()
        ]);

        this.sessions = sessionsResponse.data;
        this.locations = locationsResponse.data;
      } catch (error) {
        console.error(error);
        this.errorMessage = "Impossible de charger les créneaux.";
      } finally {
        this.loading = false;
      }
    },

    async submitSession() {
      const start = new Date(this.form.startAt);
      const end = new Date(this.form.endAt);

      if (end <= start) {
        this.errorMessage = "La date de fin doit être après la date de début.";
        this.successMessage = "";
        return;
      }

      const deadline = new Date(
        start.getTime() - Number(this.form.deadlineOffsetHours) * 3600000
      );

      try {
        this.saving = true;
        this.errorMessage = "";
        this.successMessage = "";

        await addPartnerActivitySession(this.activity.id, {
          locationId: Number(this.form.locationId),
          startAt: this.toLocalIsoString(start),
          endAt: this.toLocalIsoString(end),
          capacity: Number(this.form.capacity),
          bookingDeadline: this.toLocalIsoString(deadline)
        });

        this.successMessage = "Créneau ajouté avec succès.";
        this.form = createEmptyForm();
        await this.loadData();
      } catch (error) {
        console.error(error);
        this.errorMessage =
          error.response?.data?.message || "Impossible d’ajouter le créneau.";
      } finally {
        this.saving = false;
      }
    },

    toLocalIsoString(date) {
      const pad = (value) => String(value).padStart(2, "0");

      return (
        `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}` +
        `T${pad(date.getHours())}:${pad(date.getMinutes())}:00`
      );
    },

    closeModal() {
      this.sessions = [];
      this.form = createEmptyForm();
      this.errorMessage = "";
      this.successMessage = "";
      this.$emit("close");
    },

    formatDateTime(value) {
      if (!value) {
        return "-";
      }

      const date = new Date(value);

      return date.toLocaleString("fr-BE", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit"
      });
    },

    seatsClass(session) {
      if (session.remainingSeats <= 0) {
        return "partner-session-seats-full";
      }

      return "partner-session-seats-ok";
    },

    statusLabel(status) {
      const labels = {
        SCHEDULED: "Programmé",
        CANCELLED: "Annulé",
        COMPLETED: "Terminé"
      };

      return labels[status] || status;
    },

    statusClass(status) {
      return `partner-session-status-${String(status).toLowerCase()}`;
    }
  }
};
</script>