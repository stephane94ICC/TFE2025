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
        <span>{{ activity.title.charAt(0) }}</span>
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

        <button class="btn btn-primary">
          Réserver cette activité
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { getActivityById } from '../../services/ActivityService';

export default {
  name: 'ActivityDetailPage',

  data() {
    return {
      activity: null,
      loading: false,
      errorMessage: ''
    };
  },

  mounted() {
    this.loadActivity();
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
    }
  }
};
</script>

<style scoped src="./ActivityDetailPage.css"></style>