<template>
  <div class="admin-activities-page">
    <div class="admin-header">
      <div>
        <h1>{{ $t("admin.activities.title") }}</h1>
        <p>{{ $t("admin.activities.subtitle") }}</p>
      </div>

      <router-link to="/admin/activities/new" class="btn-add">
        {{ $t("admin.activities.add") }}
      </router-link>
    </div>

    <div v-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>

    <div v-if="successMessage" class="success-message">
      {{ successMessage }}
    </div>

    <div v-if="loading" class="loading">
      {{ $t("admin.activities.loading") }}
    </div>

    <div v-else class="table-card">
      <table>
        <thead>
          <tr>
            <th>{{ $t("admin.activities.id") }}</th>
            <th>{{ $t("admin.activities.activityTitle") }}</th>
            <th>{{ $t("admin.activities.price") }}</th>
            <th>{{ $t("admin.activities.partner") }}</th>
            <th>{{ $t("admin.activities.status") }}</th>
            <th class="actions-col">{{ $t("admin.activities.actions") }}</th>
          </tr>
        </thead>

        <tbody>
          <tr v-for="activity in activities" :key="activity.id">
            <td>{{ activity.id }}</td>
            <td>{{ activity.title }}</td>
            <td>{{ formatPrice(activity.price) }}</td>
            <td>{{ activity.partnerName || $t("admin.activities.partnerFallback", { id: activity.partnerId }) }}</td>

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
                {{ $t("admin.activities.edit") }}
              </router-link>

              <router-link
                :to="`/admin/activities/${activity.id}/images`"
                class="btn-images"
              >
                {{ $t("admin.activities.images") }}
              </router-link>

              <button
                v-if="activity.status !== 'APPROVED'"
                type="button"
                class="btn-approve"
                @click="reviewSelectedActivity(activity.id, 'APPROVED')"
              >
                {{ $t("admin.activities.approve") }}
              </button>

              <button
                v-if="activity.status !== 'REJECTED'"
                type="button"
                class="btn-reject"
                @click="openRejectModal(activity.id)"
              >
                {{ $t("admin.activities.reject") }}
              </button>

              <button
                v-if="activity.status !== 'DISABLED'"
                type="button"
                class="btn-disable"
                @click="reviewSelectedActivity(activity.id, 'DISABLED')"
              >
                {{ $t("admin.activities.disable") }}
              </button>
            </td>
          </tr>

          <tr v-if="activities.length === 0">
            <td colspan="6" class="empty">
              {{ $t("admin.activities.empty") }}
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
        this.errorMessage = this.$t("admin.activities.loadError");
      } finally {
        this.loading = false;
      }
    },

    async reviewSelectedActivity(id, status) {
      const labels = {
        APPROVED: this.$t("admin.activities.approveAction"),
        DISABLED: this.$t("admin.activities.disableAction")
      };

      const confirmed = confirm(
        this.$t("admin.activities.confirmReview", { action: labels[status] })
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

        this.successMessage = this.$t("admin.activities.updateSuccess");
      } catch (error) {
        console.error(error);
        this.errorMessage = this.$t("admin.activities.updateError");
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
        PENDING_REVIEW: this.$t("admin.activities.statuses.PENDING_REVIEW"),
        APPROVED: this.$t("admin.activities.statuses.APPROVED"),
        REJECTED: this.$t("admin.activities.statuses.REJECTED"),
        DISABLED: this.$t("admin.activities.statuses.DISABLED")
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