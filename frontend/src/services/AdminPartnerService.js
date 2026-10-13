import axios from "axios";

const ADMIN_PARTNERS_API_URL = "/api/admin/partners";

export function getAdminPartners() {
  return axios.get(ADMIN_PARTNERS_API_URL);
}

export function updatePartnerCommissionRate(partnerId, commissionRate) {
  return axios.put(
    `${ADMIN_PARTNERS_API_URL}/${partnerId}/commission-rate`,
    { commissionRate }
  );
}

export function createPartnerPaymentAccount(partnerId) {
  return axios.post(
    `${ADMIN_PARTNERS_API_URL}/${partnerId}/payment-account`,
    null
  );
}

export function createPartner(payload) {
  return axios.post(ADMIN_PARTNERS_API_URL, payload);
}