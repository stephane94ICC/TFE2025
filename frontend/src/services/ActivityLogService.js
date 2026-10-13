import axios from "axios";

const API_URL = "/api/admin/activity-logs";

export function getActivityLogs(filters = {}) {
    const params = {};

    if (filters.eventType) params.eventType = filters.eventType;
    if (filters.email) params.email = filters.email;
    if (filters.from) params.from = filters.from;
    if (filters.to) params.to = filters.to;

    params.page = filters.page ?? 0;
    params.size = filters.size ?? 50;

    return axios.get(API_URL, { params });
}

export function getEventTypes() {
    return axios.get(`${API_URL}/event-types`);
}