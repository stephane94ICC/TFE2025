<template>
  <div class="partner-activities-page">
    <header class="partner-activities-header">
      <div>
        <h1>{{ $t("partner.activities.title") }}</h1>
        <p>{{ $t("partner.activities.subtitle") }}</p>
      </div>

      <button
        type="button"
        class="partner-activity-add-link"
        @click="openCreateForm"
      >
        {{ $t("partner.activities.add") }}
      </button>
    </header>

    <p v-if="errorMessage" class="partner-activities-error">
      {{ errorMessage }}
    </p>

    <p v-if="loading">
      {{ $t("partner.activities.loading") }}
    </p>

    <section v-else class="partner-activities-card">
      <table>
        <thead>
          <tr>
            <th>{{ $t("partner.activities.activityTitle") }}</th>
            <th>{{ $t("partner.activities.price") }}</th>
            <th>{{ $t("partner.activities.duration") }}</th>
            <th>{{ $t("partner.activities.status") }}</th>
            <th>{{ $t("partner.activities.actions") }}</th>
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
                {{ $t("partner.activities.edit") }}
              </button>

              <button
                type="button"
                class="partner-activity-images-link"
                @click="openImagesModal(activity)"
              >
                {{ $t("partner.activities.manageImages") }}
              </button>

              <button
                type="button"
                class="partner-activity-sessions-link"
                @click="openSessionsModal(activity)"
              >
                {{ $t("partner.activities.manageSessions") }}
              </button>

              <button
                v-if="activity.status === 'REJECTED'"
                type="button"
                class="partner-activity-rejection-link"
                @click="openRejectionModal(activity)"
              >
                {{ $t("partner.activities.rejectionReason") }}
              </button>
            </td>
          </tr>

          <tr v-if="activities.length === 0">
            <td colspan="5" class="partner-activities-empty">
              {{ $t("partner.activities.empty") }}
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

    <PartnerActivityImagesModal
      :visible="showImagesModal"
      :activity="selectedActivityForImages"
      @close="closeImagesModal"
    />

    <PartnerActivitySessionsModal
      :visible="showSessionsModal"
      :activity="selectedActivityForSessions"
      @close="closeSessionsModal"
    />
  </div>
</template>

<script>
import { getPartnerActivities } from "../../services/PartnerService";
import PartnerActivityFormPage from "./PartnerActivityFormPage.vue";
import PartnerActivityRejectionModal from "./PartnerActivityRejectionModal.vue";
import PartnerActivityImagesModal from "./PartnerActivityImagesModal.vue";
import PartnerActivitySessionsModal from "./PartnerActivitySessionsModal.vue";
import "./PartnerActivitiesPage.css";

export default {
  name: "PartnerActivitiesPage",

  components: {
    PartnerActivityFormPage,
    PartnerActivityRejectionModal,
    PartnerActivityImagesModal,
    PartnerActivitySessionsModal
  },

  data() {
    return {
      activities: [],
      loading: true,
      errorMessage: "",
      showActivityForm: false,
      selectedActivity: null,
      showRejectionModal: false,
      selectedReviewComment: "",
      showImagesModal: false,
      selectedActivityForImages: null,
      showSessionsModal: false,
      selectedActivityForSessions: null
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
        this.errorMessage = this.$t("partner.activities.loadError");
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

    openImagesModal(activity) {
      this.selectedActivityForImages = activity;
      this.showImagesModal = true;
    },

    closeImagesModal() {
      this.showImagesModal = false;
      this.selectedActivityForImages = null;
    },

    openSessionsModal(activity) {
      this.selectedActivityForSessions = activity;
      this.showSessionsModal = true;
    },

    closeSessionsModal() {
      this.showSessionsModal = false;
      this.selectedActivityForSessions = null;
    },

    formatPrice(price) {
      if (price === null || price === undefined) {
        return "-";
      }

      return `${price} €`;
    },

    statusLabel(status) {
      const labels = {
        PENDING_REVIEW: this.$t("partner.activities.statuses.PENDING_REVIEW"),
        APPROVED: this.$t("partner.activities.statuses.APPROVED"),
        REJECTED: this.$t("partner.activities.statuses.REJECTED"),
        DISABLED: this.$t("partner.activities.statuses.DISABLED")
      };

      return labels[status] || status;
    },

    statusClass(status) {
      return `partner-activity-status-${String(status).toLowerCase()}`;
    }
  }
};
</script>