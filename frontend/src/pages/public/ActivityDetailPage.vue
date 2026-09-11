<template>
  <div class="activity-detail-page">
    <router-link to="/activities" class="back-link">
      ← {{ $t("public.activityDetail.back") }}
    </router-link>

    <div v-if="errorMessage" class="alert alert-error">
      {{ errorMessage }}
    </div>

    <p v-if="loading">{{ $t("public.activityDetail.loading") }}</p>

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
            <strong>{{ $t("public.activityDetail.price") }}</strong>
            <span>{{ activity.price }} €</span>
          </div>

          <div class="info-box">
            <strong>{{ $t("public.activityDetail.duration") }}</strong>
            <span>
              {{ activity.durationMinutes }} {{ $t("public.activityDetail.minutes") }}
            </span>
          </div>

          <div class="info-box">
            <strong>{{ $t("public.activityDetail.partner") }}</strong>
            <span>
              {{ $t("public.activityDetail.partnerId") }} : {{ activity.partnerId }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <section v-if="activity" class="sessions-section">
      <h2>{{ $t("public.activityDetail.sessionsTitle") }}</h2>

      <p v-if="bookingError" class="alert alert-error">
        {{ bookingError }}
      </p>

      <p v-if="loadingSessions">
        {{ $t("public.activityDetail.sessionsLoading") }}
      </p>

      <p v-else-if="sessions.length === 0" class="sessions-empty">
        {{ $t("public.activityDetail.sessionsEmpty") }}
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
              {{
                session.remainingSeats > 1
                  ? $t("public.activityDetail.remainingSeatsPlural")
                  : $t("public.activityDetail.remainingSeatsSingular")
              }}
            </p>

            <template v-if="isBookable(session)">
              <div v-if="selectedSessionId === session.id" class="session-booking">
                <label>
                  {{ $t("public.activityDetail.quantity") }}
                  <input
                      v-model="quantity"
                      type="number"
                      min="1"
                      :max="session.remainingSeats"
                  />
                </label>

                <button
                    class="btn btn-primary"
                    :disabled="booking"
                    @click="confirmBooking(session)"
                >
                  {{
                    booking
                      ? $t("public.activityDetail.redirecting")
                      : $t("public.activityDetail.pay")
                  }}
                </button>

                <button class="btn btn-secondary" @click="cancelBooking">
                  {{ $t("public.activityDetail.cancel") }}
                </button>
              </div>

              <button
                  v-else
                  class="btn btn-primary"
                  @click="bookSession(session)"
              >
                {{ $t("public.activityDetail.book") }}
              </button>

              <p class="session-deadline">
                {{ $t("public.activityDetail.bookingUntil") }}
                {{ formatDateTime(session.bookingDeadline) }}
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
import AuthService from '../../services/AuthService';
import ReservationService from '../../services/ReservationService';

export default {
  name: 'ActivityDetailPage',

  data() {
    return {
      activity: null,
      sessions: [],
      loading: false,
      loadingSessions: false,
      errorMessage: '',
      selectedSessionId: null,
      quantity: 1,
      booking: false,
      bookingError: ''
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
            this.errorMessage = this.$t("public.activityDetail.loadError");
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
        return this.$t("public.activityDetail.full");
      }

      return this.$t("public.activityDetail.closedOn", {
        date: this.formatDateTime(session.bookingDeadline)
      });
    },

    bookSession(session) {
      if (!AuthService.getToken()) {
        this.$router.push("/login");
        return;
      }

      this.selectedSessionId = session.id;
      this.quantity = 1;
      this.bookingError = '';
    },

    cancelBooking() {
      this.selectedSessionId = null;
      this.quantity = 1;
    },

    async confirmBooking(session) {
      const quantity = Number(this.quantity);

      if (!quantity || quantity < 1 || quantity > session.remainingSeats) {
        this.bookingError = this.$t("public.activityDetail.invalidQuantity");
        return;
      }

      try {
        this.booking = true;
        this.bookingError = '';

        const response = await ReservationService.createCheckoutSession(
            session.id,
            quantity
        );

        window.location.href = response.data.checkoutUrl;
      } catch (error) {
        console.error(error);
        this.bookingError =
            error.response?.data?.message ||
            this.$t("public.activityDetail.bookingError");
        this.booking = false;
      }
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