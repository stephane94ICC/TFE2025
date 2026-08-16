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
import { getPartnerActivitySessions } from "../../services/PartnerService";

import "./PartnerActivitySessionsModal.css";

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
      loading: false,
      errorMessage: ""
    };
  },

  watch: {
    visible(isVisible) {
      if (isVisible && this.activity) {
        this.loadSessions();
      }
    }
  },

  methods: {
    async loadSessions() {
      try {
        this.loading = true;
        this.errorMessage = "";
        this.sessions = [];

        const response = await getPartnerActivitySessions(this.activity.id);
        this.sessions = response.data;
      } catch (error) {
        console.error(error);
        this.errorMessage = "Impossible de charger les créneaux.";
      } finally {
        this.loading = false;
      }
    },

    closeModal() {
      this.sessions = [];
      this.errorMessage = "";
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