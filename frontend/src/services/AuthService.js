import axios from "axios";

const API_URL = "/api/auth";
const CONNECTED_USER_KEY = "connectedUser";

// Avant le passage au cookie HttpOnly, le jeton était stocké sous cette clé.
const LEGACY_TOKEN_KEY = "token";


class AuthService {

  constructor() {
    localStorage.removeItem(LEGACY_TOKEN_KEY);

    const user = this.getConnectedUser();

    if (user) {
      this.saveConnectedUser(user);
    }
  }

  async register(registerData) {
    try {
      const response = await axios.post(`${API_URL}/register`, registerData);
      return response.data;
    } catch (error) {
      throw toAuthError(error, "REGISTER_FAILED");
    }
  }

  async login(loginData) {
    try {
      const response = await axios.post(`${API_URL}/login`, loginData);
      return response.data;
    } catch (error) {
      const authError = toAuthError(error, "LOGIN_FAILED");
      // 429 : délai avant de réessayer, en secondes (en-tête Retry-After).
      authError.retryAfter = Number(error.response?.headers?.["retry-after"]) || 0;
      throw authError;
    }
  }

  /** Demande au serveur d'effacer le cookie, puis vide l'interface. */
  async logout() {
    try {
      await axios.post(`${API_URL}/logout`);
    } catch (error) {
      // Serveur injoignable ou cookie déjà expiré : l'interface est déconnectée quand même.
      console.warn("Déconnexion côté serveur impossible :", error.response?.status);
    } finally {
      this.clearConnectedUser();
    }
  }

  /** Liste fermée de champs : le téléphone ou toute autre donnée du profil ne sont jamais stockés ici. */
  saveConnectedUser(user) {
    const { id, firstName, lastName, email, roles, profileImageUrl } = user;

    localStorage.setItem(
      CONNECTED_USER_KEY,
      JSON.stringify({ id, firstName, lastName, email, roles, profileImageUrl })
    );
  }

  getConnectedUser() {
    const user = localStorage.getItem(CONNECTED_USER_KEY);

    if (!user) {
      return null;
    }

    try {
      return JSON.parse(user);
    } catch {
      // Valeur illisible (modifiée à la main) : on repart d'un état déconnecté.
      this.clearConnectedUser();
      return null;
    }
  }

  /** Vide l'interface sans appeler le serveur (cookie déjà effacé par lui, ou expiré). */
  clearConnectedUser() {
    localStorage.removeItem(CONNECTED_USER_KEY);
  }

  isLoggedIn() {
    return this.getConnectedUser() !== null;
  }

  getRoles() {
    const user = this.getConnectedUser();

    if (!user || !user.roles) {
      return [];
    }

    return user.roles;
  }

  hasRole(role) {
    return this.getRoles().includes(role);
  }
}

/** Même forme d'erreur qu'avant : message = code d'erreur du serveur, status = code HTTP. */
function toAuthError(error, fallbackCode) {
  const data = error.response?.data;
  const authError = new Error(data?.error || fallbackCode);
  authError.status = error.response?.status ?? 0;
  return authError;
}

export default new AuthService();