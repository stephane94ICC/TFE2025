import axios from "axios";

const PROFILE_API_URL = "/api/partner/profile";
const ADDRESSES_API_URL = "/api/partner/addresses";
const ACTIVITIES_API_URL = "/api/partner/activities";
const LOCATIONS_API_URL = "/api/partner/locations";
const PAYMENT_ACCOUNT_API_URL = "/api/partner/payment-account";
const RESERVATIONS_API_URL = "/api/partner/reservations";

export function getPartnerProfile() {
  return axios.get(PROFILE_API_URL);
}

export function updatePartnerProfile(partner) {
  return axios.put(PROFILE_API_URL, partner);
}

export function uploadPartnerLogo(file) {
  const formData = new FormData();
  formData.append("file", file);

  return axios.put(
    `${PROFILE_API_URL}/logo`,
    formData
  );
}

export function getPartnerAddresses() {
  return axios.get(ADDRESSES_API_URL);
}

export function addPartnerAddress(address) {
  return axios.post(ADDRESSES_API_URL, address);
}

export function updatePartnerAddress(id, address) {
  return axios.put(`${ADDRESSES_API_URL}/${id}`, address);
}

export function deletePartnerAddress(id) {
  return axios.delete(`${ADDRESSES_API_URL}/${id}`);
}

export function getPartnerActivities() {
  return axios.get(ACTIVITIES_API_URL);
}

export function getPartnerActivity(id) {
  return axios.get(`${ACTIVITIES_API_URL}/${id}`);
}

export function addPartnerActivity(activity) {
  return axios.post(ACTIVITIES_API_URL, activity);
}

export function updatePartnerActivity(id, activity) {
  return axios.put(`${ACTIVITIES_API_URL}/${id}`, activity);
}

export function getPartnerActivityImages(activityId) {
  return axios.get(`${ACTIVITIES_API_URL}/${activityId}/images`);
}

export function uploadPartnerActivityImage(activityId, file) {
  const formData = new FormData();
  formData.append("file", file);

  return axios.post(
    `${ACTIVITIES_API_URL}/${activityId}/images`,
    formData
  );
}

export function deletePartnerActivityImage(activityId, imageId) {
  return axios.delete(
    `${ACTIVITIES_API_URL}/${activityId}/images/${imageId}`
  );
}

export function getPartnerLocations() {
  return axios.get(LOCATIONS_API_URL);
}

export function addPartnerLocation(location) {
  return axios.post(LOCATIONS_API_URL, location);
}

export function updatePartnerLocation(id, location) {
  return axios.put(`${LOCATIONS_API_URL}/${id}`, location);
}

export function getPartnerActivitySessions(activityId) {
  return axios.get(
    `${ACTIVITIES_API_URL}/${activityId}/sessions`
  );
}

export function addPartnerActivitySession(activityId, session) {
  return axios.post(
    `${ACTIVITIES_API_URL}/${activityId}/sessions`,
    session
  );
}

export function getPartnerPaymentAccount() {
  return axios.get(PAYMENT_ACCOUNT_API_URL);
}

export function createPartnerOnboardingLink() {
  return axios.post(
    `${PAYMENT_ACCOUNT_API_URL}/onboarding-link`,
    null
  );
}

export function getPartnerSales() {
  return axios.get(RESERVATIONS_API_URL);
}

export function downloadPartnerReceipt(reservationId, lang) {
  return axios.get(`${RESERVATIONS_API_URL}/${reservationId}/receipt`, {
    params: { lang },
    responseType: "blob"
  });
}

export function downloadPartnerCreditNote(reservationId, lang) {
  return axios.get(`${RESERVATIONS_API_URL}/${reservationId}/credit-note`, {
    params: { lang },
    responseType: "blob"
  });
}