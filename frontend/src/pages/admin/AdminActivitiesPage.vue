<template>
  <div class="admin-activities-page">
    <div class="admin-header">
      <div>
        <h1>Gestion des activités</h1>
        <p>Liste des activités disponibles sur la plateforme.</p>
      </div>

      <router-link to="/admin/activities/new" class="btn-add">
        Ajouter une activité
      </router-link>
    </div>

    <div v-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>

    <div v-if="loading" class="loading">
      Chargement des activités...
    </div>

    <div v-else class="table-card">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Titre</th>
            <th>Prix</th>
            <th>Partenaire</th>
            <th>Ville</th>
            <th class="actions-col">Actions</th>
          </tr>
        </thead>

        <tbody>
          <tr v-for="activity in activities" :key="activity.id">
            <td>{{ activity.id }}</td>
            <td>{{ activity.title }}</td>
            <td>{{ formatPrice(activity.price) }}</td>
            <td>{{ activity.partnerId || '-' }}</td>
            <td>{{ activity.city || '-' }}</td>
            <td class="actions">
              <router-link
                :to="`/admin/activities/edit/${activity.id}`"
                class="btn-edit"
              >
                Modifier
              </router-link>

              <button
                type="button"
                class="btn-delete"
                @click="deleteSelectedActivity(activity.id)"
              >
                Supprimer
              </button>
            </td>
          </tr>

          <tr v-if="activities.length === 0">
            <td colspan="6" class="empty">
              Aucune activité trouvée.
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
import {
  getAdminActivities,
  deleteActivity
} from "@/services/ActivityService";

export default {
  name: "AdminActivitiesPage",

  data() {
    return {
      activities: [],
      loading: true,
      errorMessage: ""
    };
  },

  mounted() {
    this.loadActivities();
  },

  methods: {
    async loadActivities() {
      try {
        this.loading = true;
        this.errorMessage = "";

        const response = await getAdminActivities();
        this.activities = response.data;
      } catch (error) {
        console.error(error);
        this.errorMessage = "Impossible de charger les activités.";
      } finally {
        this.loading = false;
      }
    },

    async deleteSelectedActivity(id) {
      const confirmed = confirm("Voulez-vous vraiment supprimer cette activité ?");

      if (!confirmed) {
        return;
      }

      try {
        await deleteActivity(id);
        this.activities = this.activities.filter(activity => activity.id !== id);
      } catch (error) {
        console.error(error);
        this.errorMessage = "Impossible de supprimer cette activité.";
      }
    },

    formatPrice(price) {
      if (price === null || price === undefined) {
        return "-";
      }

      return `${price} €`;
    }
  }
};
</script>

<style scoped src="./AdminActivitiesPage.css"></style>