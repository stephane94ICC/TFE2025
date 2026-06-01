import axios from 'axios';
import AuthService from "./AuthService";

const PUBLIC_API_URL = '/api/activities';
const ADMIN_API_URL = '/api/admin/activities';

function getAuthConfig() {
    const token = AuthService.getToken();

    return {
        headers: {
            Authorization: `Bearer ${token}`
        }
    };
}

// PUBLIC
export function getActivities() {
    return axios.get(PUBLIC_API_URL);
}

export function getActivityById(id) {
    return axios.get(`${PUBLIC_API_URL}/${id}`);
}

// ADMIN
export function getAdminActivities() {
    return axios.get(ADMIN_API_URL, getAuthConfig());
}

export function getAdminActivityById(id) {
    return axios.get(`${ADMIN_API_URL}/${id}`, getAuthConfig());
}

export function createActivity(activity) {
    return axios.post(ADMIN_API_URL, activity, getAuthConfig());
}

export function updateActivity(id, activity) {
    return axios.put(`${ADMIN_API_URL}/${id}`, activity, getAuthConfig());
}

export function deleteActivity(id) {
    return axios.delete(`${ADMIN_API_URL}/${id}`, getAuthConfig());
}

export function getActivityImages(activityId) {
    return axios.get(`${ADMIN_API_URL}/${activityId}/images`, getAuthConfig());
}
export function reviewActivity(id, status, reviewComment = "") {
    return axios.patch(
        `${ADMIN_API_URL}/${id}/review`,
        { status, reviewComment },
        getAuthConfig()
    );
}
export function uploadActivityImage(activityId, file) {
    const formData = new FormData();
    formData.append("file", file);
    return axios.post(`${ADMIN_API_URL}/${activityId}/images`, formData, getAuthConfig());
}

export function deleteActivityImage(activityId, imageId) {
    return axios.delete(`${ADMIN_API_URL}/${activityId}/images/${imageId}`, getAuthConfig());
}