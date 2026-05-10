import axios from "axios";
import AuthService from "./AuthService";

const PUBLIC_API_URL = "/api/products";
const ADMIN_API_URL = "/api/admin/products";

function getAuthConfig() {
    const token = AuthService.getToken();

    return {
        headers: {
            Authorization: `Bearer ${token}`
        }
    };
}

// Produits publics pour la boutique
export function getProducts() {
    return axios.get(PUBLIC_API_URL);
}

// Détail public d’un produit
export function getProductById(id) {
    return axios.get(`${PUBLIC_API_URL}/${id}`);
}

// Produits admin : tous les produits
export function getAllProducts() {
    return axios.get(ADMIN_API_URL, getAuthConfig());
}

// Détail admin d’un produit
export function getAdminProductById(id) {
    return axios.get(`${ADMIN_API_URL}/${id}`, getAuthConfig());
}

// Création admin
export function createProduct(product) {
    return axios.post(ADMIN_API_URL, product, getAuthConfig());
}

// Modification admin
export function updateProduct(id, product) {
    return axios.put(`${ADMIN_API_URL}/${id}`, product, getAuthConfig());
}

// Suppression admin
export function deleteProduct(id) {
    return axios.delete(`${ADMIN_API_URL}/${id}`, getAuthConfig());
}