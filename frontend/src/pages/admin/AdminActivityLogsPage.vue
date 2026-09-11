<template>
  <div class="page-container">
    <div class="header">
      <div>
        <h1>{{ $t("admin.activityLogs.title") }}</h1>
        <p>{{ $t("admin.activityLogs.subtitle") }}</p>
      </div>

      <button class="btn btn-secondary" @click="resetFilters">
        {{ $t("admin.activityLogs.resetFilters") }}
      </button>
    </div>

    <div v-if="errorMessage" class="alert alert-error">
      {{ errorMessage }}
    </div>

    <!-- Filtres -->
    <div class="card">
      <h2>{{ $t("admin.activityLogs.filters") }}</h2>

      <div class="form-grid">
        <div class="form-group">
          <label>{{ $t("admin.activityLogs.eventType") }}</label>
          <select v-model="filters.eventType">
            <option value="">{{ $t("admin.activityLogs.allTypes") }}</option>
            <option v-for="type in eventTypes" :key="type" :value="type">
              {{ formatEventType(type) }}
            </option>
          </select>
        </div>

        <div class="form-group">
          <label>{{ $t("admin.activityLogs.email") }}</label>
          <input
              v-model="filters.email"
              type="text"
              :placeholder="$t('admin.activityLogs.partialSearch')"
          >
        </div>

        <div class="form-group">
          <label>{{ $t("admin.activityLogs.from") }}</label>
          <input v-model="filters.from" type="date">
        </div>

        <div class="form-group">
          <label>{{ $t("admin.activityLogs.to") }}</label>
          <input v-model="filters.to" type="date">
        </div>
      </div>

      <div class="actions">
        <button class="btn btn-primary" @click="applyFilters">
          {{ $t("admin.activityLogs.search") }}
        </button>
      </div>
    </div>

    <!-- Résultats -->
    <div class="card">
      <div class="results-header">
        <h2>{{ $t("admin.activityLogs.events") }}</h2>
        <span v-if="!loading" class="results-count">
          {{ $t("admin.activityLogs.entryCount", { count: totalElements }) }}
        </span>
      </div>

      <p v-if="loading">{{ $t("admin.activityLogs.loading") }}</p>

      <table v-else-if="logs.length" class="log-table">
        <thead>
        <tr>
          <th>{{ $t("admin.activityLogs.date") }}</th>
          <th>{{ $t("admin.activityLogs.event") }}</th>
          <th>{{ $t("admin.activityLogs.user") }}</th>
          <th>{{ $t("admin.activityLogs.details") }}</th>
          <th>{{ $t("admin.activityLogs.ipAddress") }}</th>
        </tr>
        </thead>

        <tbody>
        <tr v-for="entry in logs" :key="entry.id">
          <td class="cell-date">{{ formatDate(entry.createdAt) }}</td>

          <td>
            <span :class="badgeClass(entry.eventType)">
              {{ formatEventType(entry.eventType) }}
            </span>
          </td>

          <td>{{ entry.userEmail || '—' }}</td>
          <td>{{ entry.details || '—' }}</td>
          <td class="cell-ip">{{ entry.ipAddress || '—' }}</td>
        </tr>
        </tbody>
      </table>

      <p v-else>{{ $t("admin.activityLogs.empty") }}</p>

      <!-- Pagination -->
      <div v-if="totalPages > 1" class="pagination">
        <button
            class="btn btn-small btn-secondary"
            :disabled="page === 0"
            @click="goToPage(page - 1)">
          {{ $t("admin.activityLogs.previous") }}
        </button>

        <span class="pagination-info">
          {{ $t("admin.activityLogs.page", { current: page + 1, total: totalPages }) }}
        </span>

        <button
            class="btn btn-small btn-secondary"
            :disabled="page + 1 >= totalPages"
            @click="goToPage(page + 1)">
          {{ $t("admin.activityLogs.next") }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { getActivityLogs, getEventTypes } from '../../services/ActivityLogService';

export default {
  name: 'AdminActivityLogsPage',

  data() {
    return {
      logs: [],
      eventTypes: [],
      loading: false,
      errorMessage: '',

      page: 0,
      size: 50,
      totalPages: 0,
      totalElements: 0,

      filters: {
        eventType: '',
        email: '',
        from: '',
        to: ''
      }
    };
  },

  mounted() {
    this.loadEventTypes();
    this.loadLogs();
  },

  methods: {
    formatEventType(eventType) {
      const key = `admin.activityLogs.eventTypes.${eventType}`;

      return this.$te(key)
          ? this.$t(key)
          : eventType;
    },

    loadEventTypes() {
      getEventTypes()
          .then(res => {
            this.eventTypes = res.data;
          })
          .catch(err => {
            console.error(err);
          });
    },

    loadLogs() {
      this.loading = true;
      this.errorMessage = '';

      getActivityLogs({
        ...this.filters,
        page: this.page,
        size: this.size
      })
          .then(res => {
            this.logs = res.data.content;
            this.totalPages = res.data.totalPages;
            this.totalElements = res.data.totalElements;
          })
          .catch(err => {
            console.error(err);
            this.errorMessage = this.$t("admin.activityLogs.loadError");
          })
          .finally(() => {
            this.loading = false;
          });
    },

    applyFilters() {
      this.page = 0;
      this.loadLogs();
    },

    resetFilters() {
      this.filters = {
        eventType: '',
        email: '',
        from: '',
        to: ''
      };
      this.page = 0;
      this.loadLogs();
    },

    goToPage(newPage) {
      this.page = newPage;
      this.loadLogs();
    },

    formatDate(value) {
      if (!value) return '—';

      const date = new Date(value);

      return date.toLocaleString('fr-BE', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      });
    },

    badgeClass(eventType) {
      if (eventType === 'LOGIN_FAILURE' || eventType === 'ACCESS_DENIED_INACTIVE_ACCOUNT') {
        return 'badge badge-danger';
      }
      if (eventType === 'ACCOUNT_DELETION_REQUESTED' || eventType === 'ACCOUNT_ANONYMIZED') {
        return 'badge badge-warning';
      }
      return 'badge badge-role';
    }
  }
};
</script>

<style scoped src="./AdminActivityLogsPage.css"></style>