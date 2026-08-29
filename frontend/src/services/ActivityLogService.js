import axios from "axios";
import AuthService from "./AuthService";

const API_URL = "/api/admin/activity-logs";

function getAuthHeaders() {
    const token = AuthService.getToken();

    return {
        headers: {
            Authorization: `Bearer ${token}`
        }
    };
}
export function getActivityLogs(filters = {}) {
    const params = {};

    if (filters.eventType) params.eventType = filters.eventType;
    if (filters.email) params.email = filters.email;
    if (filters.from) params.from = filters.from;
    if (filters.to) params.to = filters.to;

    params.page = filters.page ?? 0;
    params.size = filters.size ?? 50;

    return axios.get(API_URL, { ...getAuthHeaders(), params });
}

export function getEventTypes() {
    return axios.get(`${API_URL}/event-types`, getAuthHeaders());
}