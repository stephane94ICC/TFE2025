const API_URL = "http://localhost:8080/api/auth";

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
}

export default new AuthService();
