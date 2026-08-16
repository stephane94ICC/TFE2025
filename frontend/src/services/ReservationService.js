import axios from "axios";
import AuthService from "./AuthService";

const RESERVATION_API_URL = "/api/member/reservations";

function getAuthConfig() {
  const token = AuthService.getToken();

  return {
    headers: {
      Authorization: `Bearer ${token}`
    }
  };
}

function createCheckoutSession(sessionId, quantity) {
  return axios.post(
    `${RESERVATION_API_URL}/checkout/create-session`,
    { sessionId, quantity },
    getAuthConfig()
  );
}

function cancelCheckoutSession(stripeSessionId) {
  return axios.patch(
    `${RESERVATION_API_URL}/checkout/cancel?session_id=${encodeURIComponent(stripeSessionId)}`,
    {},
    getAuthConfig()
  );
}

function getMyReservations() {
  return axios.get(RESERVATION_API_URL, getAuthConfig());
}

export default {
  createCheckoutSession,
  cancelCheckoutSession,
  getMyReservations
};