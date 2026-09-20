import axios from "axios";
import AuthService from "./AuthService";

const MEMBER_ORDER_API_URL = "/api/member/orders";

function getAuthConfig() {
  const token = AuthService.getToken();

  return {
    headers: {
      Authorization: `Bearer ${token}`
    }
  };
}

function getMyOrders() {
  return axios.get(MEMBER_ORDER_API_URL, getAuthConfig());
}

export default {
  getMyOrders
};