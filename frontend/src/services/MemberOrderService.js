import axios from "axios";

const MEMBER_ORDER_API_URL = "/api/member/orders";

function getMyOrders() {
  return axios.get(MEMBER_ORDER_API_URL);
}

export default {
  getMyOrders
};