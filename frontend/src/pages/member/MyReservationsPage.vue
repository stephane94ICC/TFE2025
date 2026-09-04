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

    <p v-if="successMessage" class="my-reservations-success">
      {{ successMessage }}
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
            <th>Action</th>
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

            <td>
              <button
                v-if="isCancellable(reservation)"
                type="button"
                class="reservation-cancel-button"
                :disabled="cancellingId === reservation.id"
                @click="confirmCancel(reservation)"
              >
                {{ cancellingId === reservation.id ? "Annulation..." : "Annuler" }}
              </button>

              <span
                v-else-if="reservation.status === 'CONFIRMED'"
                class="reservation-cancel-closed"
              >
                Annulation clôturée
              </span>

              <span v-else>—</span>
            </td>
          </tr>

          <tr v-if="reservations.length === 0">
            <td colspan="8" class="my-reservations-empty">
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
      errorMessage: "",
      successMessage: "",
      cancellingId: null
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

    /*
     * Contrôle de confort uniquement : le serveur applique les mêmes
     * règles et reste seul juge. Masquer le bouton évite au membre un
     * clic voué à l'échec, mais ne constitue pas une sécurité.
     */
    isCancellable(reservation) {
      if (reservation.status !== "CONFIRMED") {
        return false;
      }

      if (!reservation.bookingDeadline) {
        return false;
      }

      return new Date(reservation.bookingDeadline) > new Date();
    },

    async confirmCancel(reservation) {
      const message =
        `Annuler la réservation ${reservation.reference} ?\n\n` +
        `Les réservations annulées avant la clôture des réservations ` +
        `sont intégralement remboursées. La place sera remise à disposition.`;

      if (!window.confirm(message)) {
        return;
      }

      await this.cancelReservation(reservation);
    },

    async cancelReservation(reservation) {
      try {
        this.cancellingId = reservation.id;
        this.errorMessage = "";
        this.successMessage = "";

        await ReservationService.cancelReservation(reservation.id);

        this.successMessage = `La réservation ${reservation.reference} a été annulée.`;

        await this.loadReservations();
      } catch (error) {
        console.error(error);

        this.errorMessage =
          error.response?.data?.error ||
          "Impossible d’annuler cette réservation.";
      } finally {
        this.cancellingId = null;
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