import axios from "axios";
import AuthService from "./AuthService";

const API_URL = "/api/users";

function getAuthHeaders() {
    const token = AuthService.getToken();

    return {
        headers: {
            Authorization: `Bearer ${token}`
        }
    };
}

export function getUsers() {
    return axios.get(API_URL, getAuthHeaders());
}

export function getUser(id) {
    return axios.get(`${API_URL}/${id}`, getAuthHeaders());
}

export function createUser(user) {
    return axios.post(API_URL, user, getAuthHeaders());
}

export function updateUser(id, user) {
    return axios.put(`${API_URL}/${id}`, user, getAuthHeaders());
}

export function deleteUser(id) {
    return axios.delete(`${API_URL}/${id}`, getAuthHeaders());
}