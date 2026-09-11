<template>
  <div
    v-if="visible"
    class="partner-sessions-modal-overlay"
    @click.self="closeModal"
  >
    <section class="partner-sessions-modal">
      <header class="partner-sessions-modal-header">
        <h3>
          {{ $t("partner.activitySessions.title") }} — {{ activity ? activity.title : "" }}
        </h3>

        <button
          type="button"
          class="partner-sessions-modal-close"
          @click="closeModal"
        >
          ×
        </button>
      </header>

      <p v-if="loading">{{ $t("partner.activitySessions.loading") }}</p>

      <template v-else>
        <p v-if="errorMessage" class="form-error">
          {{ errorMessage }}
        </p>

        <p v-if="successMessage" class="form-success">
          {{ successMessage }}
        </p>

        <form class="partner-session-form" @submit.prevent="submitSession">
          <h4>{{ $t("partner.activitySessions.addTitle") }}</h4>

          <p v-if="locations.length === 0" class="partner-session-no-location">
            {{ $t("partner.activitySessions.noLocation") }}
          </p>

          <template v-else>
            <div class="partner-session-form-grid">
              <label>
                {{ $t("partner.activitySessions.location") }}
                <select v-model="form.locationId" required>
                  <option value="">
                    {{ $t("partner.activitySessions.chooseLocation") }}
                  </option>
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
                {{ $t("partner.activitySessions.capacity") }}
                <input v-model="form.capacity" type="number" min="1" required />
              </label>

              <label>
                {{ $t("partner.activitySessions.start") }}
                <input v-model="form.startAt" type="datetime-local" required />
              </label>

              <label>
                {{ $t("partner.activitySessions.end") }}
                <input v-model="form.endAt" type="datetime-local" required />
              </label>

              <label>
                {{ $t("partner.activitySessions.bookingDeadline") }}
                <select v-model="form.deadlineOffsetHours" required>
                  <option value="2">
                    {{ $t("partner.activitySessions.deadline2Hours") }}
                  </option>
                  <option value="24">
                    {{ $t("partner.activitySessions.deadline24Hours") }}
                  </option>
                  <option value="48">
                    {{ $t("partner.activitySessions.deadline48Hours") }}
                  </option>
                  <option value="168">
                    {{ $t("partner.activitySessions.deadline1Week") }}
                  </option>
                </select>
              </label>
            </div>

            <div class="partner-session-form-actions">
              <button type="submit" :disabled="saving">
                {{
                  saving
                    ? $t("partner.activitySessions.saving")
                    : $t("partner.activitySessions.add")
                }}
              </button>
            </div>
          </template>
        </form>

        <table class="partner-sessions-table">
          <thead>
            <tr>
              <th>{{ $t("partner.activitySessions.start") }}</th>
              <th>{{ $t("partner.activitySessions.end") }}</th>
              <th>{{ $t("partner.activitySessions.location") }}</th>
              <th>{{ $t("partner.activitySessions.seats") }}</th>
              <th>{{ $t("partner.activitySessions.deadline") }}</th>
              <th>{{ $t("partner.activitySessions.status") }}</th>
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
                {{ $t("partner.activitySessions.empty") }}
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
        this.errorMessage = this.$t("partner.activitySessions.loadError");
      } finally {
        this.loading = false;
      }
    },

    async submitSession() {
      const start = new Date(this.form.startAt);
      const end = new Date(this.form.endAt);

      if (end <= start) {
        this.errorMessage = this.$t("partner.activitySessions.endBeforeStart");
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

        this.successMessage = this.$t("partner.activitySessions.addSuccess");
        this.form = createEmptyForm();
        await this.loadData();
      } catch (error) {
        console.error(error);
        this.errorMessage =
          error.response?.data?.message ||
          this.$t("partner.activitySessions.addError");
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
        SCHEDULED: this.$t("partner.activitySessions.statuses.SCHEDULED"),
        CANCELLED: this.$t("partner.activitySessions.statuses.CANCELLED"),
        COMPLETED: this.$t("partner.activitySessions.statuses.COMPLETED")
      };

      return labels[status] || status;
    },

    statusClass(status) {
      return `partner-session-status-${String(status).toLowerCase()}`;
    }
  }
};
</script>