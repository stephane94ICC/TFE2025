import axios from "axios";

const CHECKOUT_API_URL = "/api/member/checkout";

function createCheckoutSession(cartItems) {
  const items = cartItems.map(item => ({
    productId: item.id,
    quantity: item.quantity
  }));

  return axios.post(
    `${CHECKOUT_API_URL}/create-session`,
    { items }
  );
}

function cancelCheckoutSession(sessionId) {
  return axios.patch(
    `${CHECKOUT_API_URL}/cancel?session_id=${encodeURIComponent(sessionId)}`,
    {}
  );
}

export default {
  createCheckoutSession,
  cancelCheckoutSession
};