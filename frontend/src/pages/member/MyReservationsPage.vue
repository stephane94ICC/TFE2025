<template>
  <div class="my-reservations-page">
    <header class="my-reservations-header">
      <div>
        <h1>{{ $t("member.reservations.title") }}</h1>
        <p>{{ $t("member.reservations.subtitle") }}</p>
      </div>
    </header>

    <p v-if="errorMessage" class="my-reservations-error">
      {{ errorMessage }}
    </p>

    <p v-if="successMessage" class="my-reservations-success">
      {{ successMessage }}
    </p>

    <h2 class="my-reservations-section-title">
      {{ $t("member.reservations.reservationsTitle") }}
    </h2>

    <p v-if="loading">
      {{ $t("member.reservations.loading") }}
    </p>

    <section v-else class="my-reservations-card">
      <table>
        <thead>
          <tr>
            <th>{{ $t("member.reservations.reference") }}</th>
            <th>{{ $t("member.reservations.activity") }}</th>
            <th>{{ $t("member.reservations.session") }}</th>
            <th>{{ $t("member.reservations.places") }}</th>
            <th>{{ $t("member.reservations.total") }}</th>
            <th>{{ $t("member.reservations.status") }}</th>
            <th>{{ $t("member.reservations.bookedAt") }}</th>
            <th>{{ $t("member.reservations.action") }}</th>
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
                {{
                  cancellingId === reservation.id
                    ? $t("member.reservations.cancelling")
                    : $t("member.reservations.cancel")
                }}
              </button>

              <span
                v-else-if="reservation.status === 'CONFIRMED'"
                class="reservation-cancel-closed"
              >
                {{ $t("member.reservations.cancellationClosed") }}
              </span>

              <span v-else>—</span>
            </td>
          </tr>

          <tr v-if="reservations.length === 0">
            <td colspan="8" class="my-reservations-empty">
              {{ $t("member.reservations.empty") }}
            </td>
          </tr>
        </tbody>
      </table>
    </section>

    <h2 class="my-reservations-section-title">
      {{ $t("member.reservations.ordersTitle") }}
    </h2>

    <p v-if="ordersError" class="my-reservations-error">
      {{ ordersError }}
    </p>

    <p v-if="ordersLoading">
      {{ $t("member.reservations.ordersLoading") }}
    </p>

    <section v-else class="my-reservations-card">
      <table>
        <thead>
          <tr>
            <th>{{ $t("member.reservations.orderNumber") }}</th>
            <th>{{ $t("member.reservations.orderDate") }}</th>
            <th>{{ $t("member.reservations.items") }}</th>
            <th>{{ $t("member.reservations.total") }}</th>
            <th>{{ $t("member.reservations.status") }}</th>
            <th>{{ $t("member.reservations.paidAt") }}</th>
          </tr>
        </thead>

        <tbody>
          <tr v-for="order in orders" :key="order.id">
            <td class="reservation-reference">{{ order.id }}</td>
            <td>{{ formatDateTime(order.orderDate) }}</td>

            <td>
              <ul class="order-items">
                <li v-for="item in order.items" :key="item.id">
                  {{ item.quantity }} × {{ item.productName }}
                </li>
              </ul>
            </td>

            <td>{{ formatPrice(order.totalAmount) }}</td>

            <td>
              <span
                :class="[
                  'reservation-status',
                  statusClass(order.status)
                ]"
              >
                {{ orderStatusLabel(order.status) }}
              </span>
            </td>

            <td>{{ formatDateTime(order.paidAt) }}</td>
          </tr>

          <tr v-if="orders.length === 0">
            <td colspan="6" class="my-reservations-empty">
              {{ $t("member.reservations.ordersEmpty") }}
            </td>
          </tr>
        </tbody>
      </table>
    </section>
  </div>
</template>

<script>
import ReservationService from "../../services/ReservationService";
import MemberOrderService from "../../services/MemberOrderService";

import "./MyReservationsPage.css";

export default {
  name: "MyReservationsPage",

  data() {
    return {
      reservations: [],
      loading: true,
      errorMessage: "",
      successMessage: "",
      cancellingId: null,

      // Commandes chargées séparément : une erreur ici n'empêche pas
      // l'affichage des réservations, et inversement
      orders: [],
      ordersLoading: true,
      ordersError: ""
    };
  },

  mounted() {
    this.loadReservations();
    this.loadOrders();
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
        this.errorMessage = this.$t("member.reservations.loadError");
      } finally {
        this.loading = false;
      }
    },

    async loadOrders() {
      try {
        this.ordersLoading = true;
        this.ordersError = "";

        const response = await MemberOrderService.getMyOrders();
        this.orders = response.data;
      } catch (error) {
        console.error(error);
        this.ordersError = this.$t("member.reservations.ordersLoadError");
      } finally {
        this.ordersLoading = false;
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
      const message = this.$t(
        "member.reservations.cancelConfirmation",
        { reference: reservation.reference }
      );

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

        this.successMessage = this.$t(
          "member.reservations.cancelSuccess",
          { reference: reservation.reference }
        );

        await this.loadReservations();
      } catch (error) {
        console.error(error);

        this.errorMessage = this.$t("member.reservations.cancelError");
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

      return this.$price(price);
    },

    statusLabel(status) {
      const labels = {
        PENDING: this.$t("member.reservations.statuses.PENDING"),
        CONFIRMED: this.$t("member.reservations.statuses.CONFIRMED"),
        CANCELLED: this.$t("member.reservations.statuses.CANCELLED")
      };

      return labels[status] || status;
    },

    orderStatusLabel(status) {
      const key = `member.reservations.orderStatuses.${status}`;

      return this.$te(key) ? this.$t(key) : status;
    },

    statusClass(status) {
      return `reservation-status-${String(status).toLowerCase()}`;
    }
  }
};
</script>