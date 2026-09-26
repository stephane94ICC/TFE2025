import axios from "axios";
import AuthService from "./AuthService";

const ADMIN_PARTNERS_API_URL = "/api/admin/partners";

function getAuthHeaders() {
  const token = AuthService.getToken();

  return {
    headers: {
      Authorization: `Bearer ${token}`
    }
  };
}

export function getAdminPartners() {
  return axios.get(ADMIN_PARTNERS_API_URL, getAuthHeaders());
}

export function updatePartnerCommissionRate(partnerId, commissionRate) {
  return axios.put(
    `${ADMIN_PARTNERS_API_URL}/${partnerId}/commission-rate`,
    { commissionRate },
    getAuthHeaders()
  );
}

export function createPartnerPaymentAccount(partnerId) {
  return axios.post(
    `${ADMIN_PARTNERS_API_URL}/${partnerId}/payment-account`,
    null,
    getAuthHeaders()
  );
}