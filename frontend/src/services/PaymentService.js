import axios from "axios";
import AuthService from "./AuthService";

const CHECKOUT_API_URL = "/api/member/checkout";

function getAuthConfig() {
  const token = AuthService.getToken();

  return {
    headers: {
      Authorization: `Bearer ${token}`
    }
  };
}

function createCheckoutSession(cartItems) {
  const items = cartItems.map(item => ({
    productId: item.id,
    quantity: item.quantity
  }));

  return axios.post(
    `${CHECKOUT_API_URL}/create-session`,
    { items },
    getAuthConfig()
  );
}

function cancelCheckoutSession(sessionId) {
  return axios.patch(
    `${CHECKOUT_API_URL}/cancel?session_id=${encodeURIComponent(sessionId)}`,
    {},
    getAuthConfig()
  );
}

export default {
  createCheckoutSession,
  cancelCheckoutSession
};