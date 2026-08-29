<template>
  <div class="page-container">
    <div class="header">
      <div>
        <h1>Journal d'activité</h1>
        <p>Historique des événements enregistrés sur la plateforme.</p>
      </div>

      <button class="btn btn-secondary" @click="resetFilters">
        Réinitialiser les filtres
      </button>
    </div>

    <div v-if="errorMessage" class="alert alert-error">
      {{ errorMessage }}
    </div>

    <!-- Filtres -->
    <div class="card">
      <h2>Filtres</h2>

      <div class="form-grid">
        <div class="form-group">
          <label>Type d'événement</label>
          <select v-model="filters.eventType">
            <option value="">Tous les types</option>
            <option v-for="type in eventTypes" :key="type" :value="type">
              {{ formatEventType(type) }}
            </option>
          </select>
        </div>

        <div class="form-group">
          <label>Adresse e-mail</label>
          <input v-model="filters.email" type="text" placeholder="Recherche partielle">
        </div>

        <div class="form-group">
          <label>Du</label>
          <input v-model="filters.from" type="date">
        </div>

        <div class="form-group">
          <label>Au</label>
          <input v-model="filters.to" type="date">
        </div>
      </div>

      <div class="actions">
        <button class="btn btn-primary" @click="applyFilters">
          Rechercher
        </button>
      </div>
    </div>

    <!-- Résultats -->
    <div class="card">
      <div class="results-header">
        <h2>Événements</h2>
        <span v-if="!loading" class="results-count">
          {{ totalElements }} entrée(s)
        </span>
      </div>

      <p v-if="loading">Chargement du journal...</p>

      <table v-else-if="logs.length" class="log-table">
        <thead>
        <tr>
          <th>Date</th>
          <th>Événement</th>
          <th>Utilisateur</th>
          <th>Détails</th>
          <th>Adresse IP</th>
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

      <p v-else>Aucun événement ne correspond aux critères.</p>

      <!-- Pagination -->
      <div v-if="totalPages > 1" class="pagination">
        <button
            class="btn btn-small btn-secondary"
            :disabled="page === 0"
            @click="goToPage(page - 1)">
          Précédent
        </button>

        <span class="pagination-info">
          Page {{ page + 1 }} sur {{ totalPages }}
        </span>

        <button
            class="btn btn-small btn-secondary"
            :disabled="page + 1 >= totalPages"
            @click="goToPage(page + 1)">
          Suivant
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { getActivityLogs, getEventTypes } from '../../services/ActivityLogService';
import { formatEventType } from '../../services/activityEventLabels';

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
    formatEventType,

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
            this.errorMessage = "Impossible de charger le journal d'activité.";
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