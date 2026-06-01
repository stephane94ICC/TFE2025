<template>
  <div class="partner-activities-page">
    <header class="partner-activities-header">
      <div>
        <h1>Mes activités</h1>
        <p>Consultez vos activités et leur état de validation.</p>
      </div>

      <button
        type="button"
        class="partner-activity-add-link"
        @click="openCreateForm"
      >
        Ajouter une activité
      </button>
    </header>

    <p v-if="errorMessage" class="partner-activities-error">
      {{ errorMessage }}
    </p>

    <p v-if="loading">
      Chargement des activités...
    </p>

    <section v-else class="partner-activities-card">
      <table>
        <thead>
          <tr>
            <th>Titre</th>
            <th>Prix</th>
            <th>Durée</th>
            <th>Statut</th>
            <th>Actions</th>
          </tr>
        </thead>

        <tbody>
          <tr v-for="activity in activities" :key="activity.id">
            <td>{{ activity.title }}</td>
            <td>{{ formatPrice(activity.price) }}</td>
            <td>{{ activity.durationMinutes }} min</td>

            <td>
              <span
                :class="[
                  'partner-activity-status',
                  statusClass(activity.status)
                ]"
              >
                {{ statusLabel(activity.status) }}
              </span>
            </td>

            <td class="partner-activity-actions">
              <button
                type="button"
                class="partner-activity-edit-link"
                @click="openEditForm(activity)"
              >
                Modifier
              </button>

              <button
                v-if="activity.status === 'REJECTED'"
                type="button"
                class="partner-activity-rejection-link"
                @click="openRejectionModal(activity)"
              >
                Raison du refus
              </button>
            </td>
          </tr>

          <tr v-if="activities.length === 0">
            <td colspan="5" class="partner-activities-empty">
              Aucune activité trouvée.
            </td>
          </tr>
        </tbody>
      </table>
    </section>

    <PartnerActivityFormPage
      :visible="showActivityForm"
      :activity-to-edit="selectedActivity"
      @saved="handleActivitySaved"
      @close="closeActivityForm"
    />

    <PartnerActivityRejectionModal
      :visible="showRejectionModal"
      :review-comment="selectedReviewComment"
      @close="closeRejectionModal"
    />
  </div>
</template>

<script>
import { getPartnerActivities } from "../../services/PartnerService";
import PartnerActivityFormPage from "./PartnerActivityFormPage.vue";
import PartnerActivityRejectionModal from "./PartnerActivityRejectionModal.vue";
import "./PartnerActivitiesPage.css";

export default {
  name: "PartnerActivitiesPage",

  components: {
    PartnerActivityFormPage,
    PartnerActivityRejectionModal
  },

  data() {
    return {
      activities: [],
      loading: true,
      errorMessage: "",
      showActivityForm: false,
      selectedActivity: null,
      showRejectionModal: false,
      selectedReviewComment: ""
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

        const response = await getPartnerActivities();
        this.activities = response.data;
      } catch (error) {
        console.error(error);
        this.errorMessage = "Impossible de charger les activités.";
      } finally {
        this.loading = false;
      }
    },

    openCreateForm() {
      this.selectedActivity = null;
      this.showActivityForm = true;
    },

    openEditForm(activity) {
      this.selectedActivity = activity;
      this.showActivityForm = true;
    },

    closeActivityForm() {
      this.showActivityForm = false;
      this.selectedActivity = null;
    },

    async handleActivitySaved() {
      this.closeActivityForm();
      await this.loadActivities();
    },

    openRejectionModal(activity) {
      this.selectedReviewComment = activity.reviewComment || "";
      this.showRejectionModal = true;
    },

    closeRejectionModal() {
      this.showRejectionModal = false;
      this.selectedReviewComment = "";
    },

    formatPrice(price) {
      if (price === null || price === undefined) {
        return "-";
      }

      return `${price} €`;
    },

    statusLabel(status) {
      const labels = {
        PENDING_REVIEW: "En attente de validation",
        APPROVED: "Approuvée",
        REJECTED: "Refusée",
        DISABLED: "Désactivée"
      };

      return labels[status] || status;
    },

    statusClass(status) {
      return `partner-activity-status-${String(status).toLowerCase()}`;
    }
  }
};
</script>