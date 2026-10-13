import axios from "axios";

const RESERVATION_API_URL = "/api/member/reservations";

function createCheckoutSession(sessionId, quantity) {
  return axios.post(
    `${RESERVATION_API_URL}/checkout/create-session`,
    { sessionId, quantity }
  );
}

function cancelCheckoutSession(stripeSessionId) {
  return axios.patch(
    `${RESERVATION_API_URL}/checkout/cancel?session_id=${encodeURIComponent(stripeSessionId)}`,
    {}
  );
}

function getMyReservations() {
  return axios.get(RESERVATION_API_URL);
}
function cancelReservation(reservationId) {
  return axios.patch(
    `${RESERVATION_API_URL}/${reservationId}/cancel`,
    {}
  );
}
export default {
  createCheckoutSession,
  cancelCheckoutSession,
  cancelReservation,
  getMyReservations
};