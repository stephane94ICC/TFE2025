import axios from 'axios';

const API_URL = '/api/products';

export function getProducts() {
    return axios.get(API_URL);
}

export function getAllProducts() {
    return axios.get(`${API_URL}/all`);
}

export function getProductById(id) {
    return axios.get(`${API_URL}/${id}`);
}

export function createProduct(product) {
    return axios.post(API_URL, product);
}

export function updateProduct(id, product) {
    return axios.put(`${API_URL}/${id}`, product);
}

export function deleteProduct(id) {
    return axios.delete(`${API_URL}/${id}`);
}