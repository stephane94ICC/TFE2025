import axios from "axios";

const PUBLIC_PARTNER_API_URL = "/api/partners";

export function getPublicPartner(slug) {
    return axios.get(`${PUBLIC_PARTNER_API_URL}/${encodeURIComponent(slug)}`);
}