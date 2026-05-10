import axios from 'axios';

const API_URL = '/api/users';

export function getUsers() {
    return axios.get(API_URL);
}

export function getUser(id) {
    return axios.get(`${API_URL}/${id}`);
}

export function createUser(user) {
    return axios.post(API_URL, user);
}

export function updateUser(id, user) {
    return axios.put(`${API_URL}/${id}`, user);
}

export function deleteUser(id) {
    return axios.delete(`${API_URL}/${id}`);
}
