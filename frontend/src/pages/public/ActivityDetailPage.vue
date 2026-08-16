<template>
  <div class="activity-detail-page">
    <router-link to="/activities" class="back-link">
      ← Retour aux activités
    </router-link>

    <div v-if="errorMessage" class="alert alert-error">
      {{ errorMessage }}
    </div>

    <p v-if="loading">Chargement de l’activité...</p>

    <div v-else-if="activity" class="detail-card">
      <div class="detail-image">
        <img
            v-if="getActivityImage(activity)"
            :src="getActivityImage(activity)"
            :alt="activity.title"
        />

        <span v-else>{{ activity.title.charAt(0) }}</span>
      </div>

      <div class="detail-content">
        <h1>{{ activity.title }}</h1>

        <p class="description">
          {{ activity.description }}
        </p>

        <div class="info-grid">
          <div class="info-box">
            <strong>Prix</strong>
            <span>{{ activity.price }} €</span>
          </div>

          <div class="info-box">
            <strong>Durée</strong>
            <span>{{ activity.durationMinutes }} minutes</span>
          </div>

          <div class="info-box">
            <strong>Partenaire</strong>
            <span>ID partenaire : {{ activity.partnerId }}</span>
          </div>
        </div>
      </div>
    </div>

    <section v-if="activity" class="sessions-section">
      <h2>Créneaux disponibles</h2>

      <p v-if="loadingSessions">Chargement des créneaux...</p>

      <p v-else-if="sessions.length === 0" class="sessions-empty">
        Aucun créneau n’est proposé pour cette activité pour le moment.
      </p>

      <div v-else class="sessions-list">
        <article
            v-for="session in sessions"
            :key="session.id"
            :class="['session-card', { 'session-card-disabled': !isBookable(session) }]"
        >
          <div class="session-main">
            <p class="session-date">
              {{ formatDate(session.startAt) }}
            </p>

            <p class="session-hours">
              {{ formatTime(session.startAt) }} – {{ formatTime(session.endAt) }}
            </p>

            <p class="session-location">
              📍 {{ session.locationName }}
            </p>
          </div>

          <div class="session-aside">
            <p v-if="session.remainingSeats > 0" class="session-seats">
              {{ session.remainingSeats }}
              {{ session.remainingSeats > 1 ? "places restantes" : "place restante" }}
            </p>

            <template v-if="isBookable(session)">
              <button class="btn btn-primary" @click="bookSession(session)">
                Réserver
              </button>

              <p class="session-deadline">
                Réservation jusqu’au {{ formatDateTime(session.bookingDeadline) }}
              </p>
            </template>

            <p v-else class="session-unavailable">
              {{ unavailableReason(session) }}
            </p>
          </div>
        </article>
      </div>
    </section>
  </div>
</template>

<script>
import { getActivityById, getActivitySessions } from '../../services/ActivityService';

export default {
  name: 'ActivityDetailPage',

  data() {
    return {
      activity: null,
      sessions: [],
      loading: false,
      loadingSessions: false,
      errorMessage: ''
    };
  },

  mounted() {
    this.loadActivity();
    this.loadSessions();
  },

  methods: {
    loadActivity() {
      const id = this.$route.params.id;

      this.loading = true;
      this.errorMessage = '';

      getActivityById(id)
          .then(res => {
            this.activity = res.data;
          })
          .catch(err => {
            console.error(err);
            this.errorMessage = "Impossible de charger le détail de l’activité.";
          })
          .finally(() => {
            this.loading = false;
          });
    },

    loadSessions() {
      const id = this.$route.params.id;

      this.loadingSessions = true;

      getActivitySessions(id)
          .then(res => {
            this.sessions = res.data;
          })
          .catch(err => {
            console.error(err);
            this.sessions = [];
          })
          .finally(() => {
            this.loadingSessions = false;
          });
    },

    isDeadlinePassed(session) {
      return new Date(session.bookingDeadline) < new Date();
    },

    isBookable(session) {
      return session.remainingSeats > 0 && !this.isDeadlinePassed(session);
    },

    unavailableReason(session) {
      if (session.remainingSeats <= 0) {
        return "Complet";
      }

      return `Réservations clôturées le ${this.formatDateTime(session.bookingDeadline)}`;
    },

    bookSession(session) {
      console.log("Réservation du créneau", session.id);
    },

    formatDate(value) {
      if (!value) return "-";

      return new Date(value).toLocaleDateString("fr-BE", {
        weekday: "long",
        day: "2-digit",
        month: "long",
        year: "numeric"
      });
    },

    formatTime(value) {
      if (!value) return "-";

      return new Date(value).toLocaleTimeString("fr-BE", {
        hour: "2-digit",
        minute: "2-digit"
      });
    },

    formatDateTime(value) {
      if (!value) return "-";

      return new Date(value).toLocaleString("fr-BE", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit"
      });
    },

    getActivityImage(activity) {
      if (activity.imageUrls && activity.imageUrls.length > 0) {
        return activity.imageUrls[0];
      }

      return null;
    }
  }
};
</script>

<style scoped src="./ActivityDetailPage.css"></style>