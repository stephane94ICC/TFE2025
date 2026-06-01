<template>
  <div class="admin-activities-page">
    <div class="admin-header">
      <div>
        <h1>Gestion des activités</h1>
        <p>Consultez les activités et validez les propositions des partenaires.</p>
      </div>

      <router-link to="/admin/activities/new" class="btn-add">
        Ajouter une activité
      </router-link>
    </div>

    <div v-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>

    <div v-if="successMessage" class="success-message">
      {{ successMessage }}
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
            <th>Statut</th>
            <th class="actions-col">Actions</th>
          </tr>
        </thead>

        <tbody>
          <tr v-for="activity in activities" :key="activity.id">
            <td>{{ activity.id }}</td>
            <td>{{ activity.title }}</td>
            <td>{{ formatPrice(activity.price) }}</td>
            <td>{{ activity.partnerName || `Partenaire #${activity.partnerId}` }}</td>

            <td>
              <span :class="['activity-status', statusClass(activity.status)]">
                {{ statusLabel(activity.status) }}
              </span>
            </td>

            <td class="actions">
              <router-link
                :to="`/admin/activities/edit/${activity.id}`"
                class="btn-edit"
              >
                Modifier
              </router-link>

              <router-link
                :to="`/admin/activities/${activity.id}/images`"
                class="btn-images"
              >
                Images
              </router-link>

              <button
                v-if="activity.status !== 'APPROVED'"
                type="button"
                class="btn-approve"
                @click="reviewSelectedActivity(activity.id, 'APPROVED')"
              >
                Approuver
              </button>

              <button
                v-if="activity.status !== 'REJECTED'"
                type="button"
                class="btn-reject"
                @click="openRejectModal(activity.id)"
              >
                Refuser
              </button>

              <button
                v-if="activity.status !== 'DISABLED'"
                type="button"
                class="btn-disable"
                @click="reviewSelectedActivity(activity.id, 'DISABLED')"
              >
                Désactiver
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

    <AdminActivityReviewModal
      :visible="showRejectModal"
      @confirm="confirmRejection"
      @close="closeRejectModal"
    />
  </div>
</template>

<script>
import {
  getAdminActivities,
  reviewActivity
} from "@/services/ActivityService";

import AdminActivityReviewModal from "./AdminActivityReviewModal.vue";

export default {
  name: "AdminActivitiesPage",

  components: {
    AdminActivityReviewModal
  },

  data() {
    return {
      activities: [],
      loading: true,
      errorMessage: "",
      successMessage: "",
      showRejectModal: false,
      selectedActivityId: null
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

    async reviewSelectedActivity(id, status) {
      const labels = {
        APPROVED: "approuver",
        DISABLED: "désactiver"
      };

      const confirmed = confirm(
        `Voulez-vous vraiment ${labels[status]} cette activité ?`
      );

      if (!confirmed) {
        return;
      }

      await this.updateActivityStatus(id, status);
    },

    openRejectModal(id) {
      this.selectedActivityId = id;
      this.showRejectModal = true;
    },

    closeRejectModal() {
      this.showRejectModal = false;
      this.selectedActivityId = null;
    },

    async confirmRejection(reviewComment) {
      if (!this.selectedActivityId) {
        return;
      }

      await this.updateActivityStatus(
        this.selectedActivityId,
        "REJECTED",
        reviewComment
      );

      this.closeRejectModal();
    },

    async updateActivityStatus(id, status, reviewComment = "") {
      try {
        this.errorMessage = "";
        this.successMessage = "";

        await reviewActivity(id, status, reviewComment);
        await this.loadActivities();

        this.successMessage = "Le statut de l’activité a été mis à jour.";
      } catch (error) {
        console.error(error);
        this.errorMessage = "Impossible de modifier le statut de l’activité.";
      }
    },

    formatPrice(price) {
      if (price === null || price === undefined) {
        return "-";
      }

      return `${price} €`;
    },

    statusLabel(status) {
      const labels = {
        PENDING_REVIEW: "En attente",
        APPROVED: "Approuvée",
        REJECTED: "Refusée",
        DISABLED: "Désactivée"
      };

      return labels[status] || status;
    },

    statusClass(status) {
      return `activity-status-${String(status).toLowerCase()}`;
    }
  }
};
</script>

<style scoped src="./AdminActivitiesPage.css"></style>