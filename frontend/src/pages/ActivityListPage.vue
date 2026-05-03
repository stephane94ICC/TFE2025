<template>
  <div class="activities-page">
    <div class="page-header">
      <div>
        <h1>Activités</h1>
        <p>Découvrez les activités disponibles sur la plateforme.</p>
      </div>
    </div>

    <div v-if="errorMessage" class="alert alert-error">
      {{ errorMessage }}
    </div>

    <p v-if="loading">Chargement des activités...</p>

    <div v-else-if="activities.length" class="activity-grid">
      <div v-for="activity in activities" :key="activity.id" class="activity-card">
        <div class="activity-image">
          <span>{{ activity.title.charAt(0) }}</span>
        </div>

        <div class="activity-content">
          <h2>{{ activity.title }}</h2>

          <p class="description">
            {{ activity.description }}
          </p>

          <div class="activity-info">
            <span>{{ activity.durationMinutes }} min</span>
            <span>{{ activity.price }} €</span>
          </div>

          <router-link :to="`/activities/${activity.id}`" class="btn btn-primary">
            Voir le détail
          </router-link>
        </div>
      </div>
    </div>

    <p v-else>Aucune activité disponible.</p>
  </div>
</template>

<script>
import { getActivities } from '../services/ActivityService';

export default {
  name: 'ActivityListPage',

  data() {
    return {
      activities: [],
      loading: false,
      errorMessage: ''
    };
  },

  mounted() {
    this.loadActivities();
  },

  methods: {
    loadActivities() {
      this.loading = true;
      this.errorMessage = '';

      getActivities()
          .then(res => {
            this.activities = res.data;
          })
          .catch(err => {
            console.error(err);
            this.errorMessage = "Impossible de charger les activités.";
          })
          .finally(() => {
            this.loading = false;
          });
    }
  }
};
</script>

<style scoped src="./ActivityListPage.css"></style>