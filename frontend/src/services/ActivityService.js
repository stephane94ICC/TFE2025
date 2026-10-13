import axios from 'axios';

const PUBLIC_API_URL = '/api/activities';
const ADMIN_API_URL = '/api/admin/activities';

// PUBLIC
export function getActivities() {
    return axios.get(PUBLIC_API_URL);
}

export function getActivityById(id) {
    return axios.get(`${PUBLIC_API_URL}/${id}`);
}

export function getActivitySessions(activityId) {
    return axios.get(`${PUBLIC_API_URL}/${activityId}/sessions`);
}

// ADMIN
export function getAdminActivities() {
    return axios.get(ADMIN_API_URL);
}

export function getAdminActivityById(id) {
    return axios.get(`${ADMIN_API_URL}/${id}`);
}

export function createActivity(activity) {
    return axios.post(ADMIN_API_URL, activity);
}

export function updateActivity(id, activity) {
    return axios.put(`${ADMIN_API_URL}/${id}`, activity);
}

export function deleteActivity(id) {
    return axios.delete(`${ADMIN_API_URL}/${id}`);
}

export function getActivityImages(activityId) {
    return axios.get(`${ADMIN_API_URL}/${activityId}/images`);
}

export function reviewActivity(id, status, reviewComment = "") {
    return axios.patch(
        `${ADMIN_API_URL}/${id}/review`,
        { status, reviewComment }
    );
}

export function uploadActivityImage(activityId, file) {
    const formData = new FormData();
    formData.append("file", file);
    return axios.post(`${ADMIN_API_URL}/${activityId}/images`, formData);
}

export function deleteActivityImage(activityId, imageId) {
    return axios.delete(`${ADMIN_API_URL}/${activityId}/images/${imageId}`);
}