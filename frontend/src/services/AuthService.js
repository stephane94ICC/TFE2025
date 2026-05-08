const API_URL = "http://localhost:8080/api/auth";
const CONNECTED_USER_KEY = "connectedUser";
const TOKEN_KEY = "token";

class AuthService {
  register(registerData) {
    return fetch(`${API_URL}/register`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(registerData)
    }).then(response => {
      if (!response.ok) {
        throw new Error("Erreur lors de l'inscription.");
      }

      return response.json();
    });
  }

  login(loginData) {
    return fetch(`${API_URL}/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(loginData)
    }).then(response => {
      if (!response.ok) {
        throw new Error("Erreur lors de la connexion.");
      }

      return response.json();
    });
  }

  saveConnectedUser(user) {
    localStorage.setItem(CONNECTED_USER_KEY, JSON.stringify(user));

    if (user.token) {
      localStorage.setItem(TOKEN_KEY, user.token);
    }
  }

  getConnectedUser() {
    const user = localStorage.getItem(CONNECTED_USER_KEY);

    if (!user) {
      return null;
    }

    return JSON.parse(user);
  }

  getToken() {
    return localStorage.getItem(TOKEN_KEY);
  }

  isLoggedIn() {
    return this.getConnectedUser() !== null && this.getToken() !== null;
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

  logout() {
    localStorage.removeItem(CONNECTED_USER_KEY);
    localStorage.removeItem(TOKEN_KEY);
  }
}

export default new AuthService();