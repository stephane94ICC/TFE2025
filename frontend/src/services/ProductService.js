import axios from "axios";

const PUBLIC_API_URL = "/api/products";
const ADMIN_API_URL = "/api/admin/products";

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
    return axios.get(ADMIN_API_URL);
}

// Détail admin d’un produit
export function getAdminProductById(id) {
    return axios.get(`${ADMIN_API_URL}/${id}`);
}

// Création admin
export function createProduct(product) {
    return axios.post(ADMIN_API_URL, product);
}

// Modification admin
export function updateProduct(id, product) {
    return axios.put(`${ADMIN_API_URL}/${id}`, product);
}

// Suppression admin
export function deleteProduct(id) {
    return axios.delete(`${ADMIN_API_URL}/${id}`);
}
// Liste des images d’un produit
export function getProductImages(productId) {
    return axios.get(`${ADMIN_API_URL}/${productId}/images`);
}

// Ajout d’une image à un produit
export function uploadProductImage(productId, file) {
    const formData = new FormData();
    formData.append("file", file);

    return axios.post(
        `${ADMIN_API_URL}/${productId}/images`,
        formData
    );
}

// Suppression d’une image d’un produit
export function deleteProductImage(productId, imageId) {
    return axios.delete(
        `${ADMIN_API_URL}/${productId}/images/${imageId}`
    );
}