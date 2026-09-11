<template>
  <div class="activities-page">
    <div class="page-header">
      <div>
        <h1>{{ $t("public.activityList.title") }}</h1>
        <p>{{ $t("public.activityList.subtitle") }}</p>
      </div>
    </div>

    <div v-if="errorMessage" class="alert alert-error">
      {{ errorMessage }}
    </div>

    <p v-if="loading">{{ $t("public.activityList.loading") }}</p>

    <div v-else-if="activities.length" class="activity-grid">
      <div v-for="activity in activities" :key="activity.id" class="activity-card">
        <div class="activity-image">
          <img
              v-if="getActivityImage(activity)"
              :src="getActivityImage(activity)"
              :alt="activity.title"
          />

          <span v-else>{{ activity.title.charAt(0) }}</span>
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
            {{ $t("public.activityList.details") }}
          </router-link>
        </div>
      </div>
    </div>

    <p v-else>{{ $t("public.activityList.empty") }}</p>
  </div>
</template>

<script>
import { getActivities } from '../../services/ActivityService';

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
            this.errorMessage = this.$t("public.activityList.loadError");
          })
          .finally(() => {
            this.loading = false;
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

<style scoped src="./ActivityListPage.css"></style>