import axios from "axios";
import AuthService from "./AuthService";

const PROFILE_API_URL = "/api/partner/profile";
const ADDRESSES_API_URL = "/api/partner/addresses";
const ACTIVITIES_API_URL = "/api/partner/activities";

function getAuthHeaders() {
  const token = AuthService.getToken();

  return {
    headers: {
      Authorization: `Bearer ${token}`
    }
  };
}

export function getPartnerProfile() {
  return axios.get(PROFILE_API_URL, getAuthHeaders());
}

export function updatePartnerProfile(partner) {
  return axios.put(PROFILE_API_URL, partner, getAuthHeaders());
}

export function uploadPartnerLogo(file) {
  const formData = new FormData();
  formData.append("file", file);

  return axios.put(
    `${PROFILE_API_URL}/logo`,
    formData,
    getAuthHeaders()
  );
}

export function getPartnerAddresses() {
  return axios.get(ADDRESSES_API_URL, getAuthHeaders());
}

export function addPartnerAddress(address) {
  return axios.post(ADDRESSES_API_URL, address, getAuthHeaders());
}

export function updatePartnerAddress(id, address) {
  return axios.put(`${ADDRESSES_API_URL}/${id}`, address, getAuthHeaders());
}

export function deletePartnerAddress(id) {
  return axios.delete(`${ADDRESSES_API_URL}/${id}`, getAuthHeaders());
}

export function getPartnerActivities() {
  return axios.get(ACTIVITIES_API_URL, getAuthHeaders());
}

export function getPartnerActivity(id) {
  return axios.get(`${ACTIVITIES_API_URL}/${id}`, getAuthHeaders());
}

export function addPartnerActivity(activity) {
  return axios.post(ACTIVITIES_API_URL, activity, getAuthHeaders());
}

export function updatePartnerActivity(id, activity) {
  return axios.put(`${ACTIVITIES_API_URL}/${id}`, activity, getAuthHeaders());
}