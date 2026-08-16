<template>
  <div class="my-reservations-page">
    <header class="my-reservations-header">
      <div>
        <h1>Mes réservations</h1>
        <p>Retrouvez l’ensemble de vos réservations d’activités.</p>
      </div>
    </header>

    <p v-if="errorMessage" class="my-reservations-error">
      {{ errorMessage }}
    </p>

    <p v-if="loading">
      Chargement des réservations...
    </p>

    <section v-else class="my-reservations-card">
      <table>
        <thead>
          <tr>
            <th>Référence</th>
            <th>Activité</th>
            <th>Créneau</th>
            <th>Places</th>
            <th>Total</th>
            <th>Statut</th>
            <th>Réservé le</th>
          </tr>
        </thead>

        <tbody>
          <tr v-for="reservation in reservations" :key="reservation.id">
            <td class="reservation-reference">
              {{ reservation.reference }}
            </td>

            <td>{{ reservation.activityTitle }}</td>
            <td>{{ formatDateTime(reservation.sessionStartAt) }}</td>
            <td>{{ reservation.quantity }}</td>
            <td>{{ formatPrice(reservation.totalPrice) }}</td>

            <td>
              <span
                :class="[
                  'reservation-status',
                  statusClass(reservation.status)
                ]"
              >
                {{ statusLabel(reservation.status) }}
              </span>
            </td>

            <td>{{ formatDateTime(reservation.bookedAt) }}</td>
          </tr>

          <tr v-if="reservations.length === 0">
            <td colspan="7" class="my-reservations-empty">
              Vous n’avez encore aucune réservation.
            </td>
          </tr>
        </tbody>
      </table>
    </section>
  </div>
</template>

<script>
import ReservationService from "../../services/ReservationService";

import "./MyReservationsPage.css";

export default {
  name: "MyReservationsPage",

  data() {
    return {
      reservations: [],
      loading: true,
      errorMessage: ""
    };
  },

  mounted() {
    this.loadReservations();
  },

  methods: {
    async loadReservations() {
      try {
        this.loading = true;
        this.errorMessage = "";

        const response = await ReservationService.getMyReservations();
        this.reservations = response.data;
      } catch (error) {
        console.error(error);
        this.errorMessage = "Impossible de charger vos réservations.";
      } finally {
        this.loading = false;
      }
    },

    formatDateTime(value) {
      if (!value) {
        return "-";
      }

      return new Date(value).toLocaleString("fr-BE", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit"
      });
    },

    formatPrice(price) {
      if (price === null || price === undefined) {
        return "-";
      }

      return `${price} €`;
    },

    statusLabel(status) {
      const labels = {
        PENDING: "En attente de paiement",
        CONFIRMED: "Confirmée",
        CANCELLED: "Annulée"
      };

      return labels[status] || status;
    },

    statusClass(status) {
      return `reservation-status-${String(status).toLowerCase()}`;
    }
  }
};
</script>