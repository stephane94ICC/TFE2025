import axios from 'axios';

const API_URL = '/api/activities';

export function getActivities() {
    return axios.get(API_URL);
}

export function getActivityById(id) {
    return axios.get(`${API_URL}/${id}`);
}

export function createActivity(activity) {
    return axios.post(API_URL, activity);
}

export function updateActivity(id, activity) {
    return axios.put(`${API_URL}/${id}`, activity);
}

export function deleteActivity(id) {
    return axios.delete(`${API_URL}/${id}`);
}