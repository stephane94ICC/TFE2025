import axios from 'axios';

const API_URL = '/api/orders';

export function getAllOrders() {
  return axios.get(API_URL);
}

export function getOrderById(id) {
  return axios.get(`${API_URL}/${id}`);
}

export function getOrdersByUserId(userId) {
  return axios.get(`${API_URL}/user/${userId}`);
}