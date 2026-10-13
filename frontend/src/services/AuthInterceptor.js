import axios from "axios";
import AuthService from "./AuthService";

// Sur ces routes, un 401 veut dire « identifiants incorrects », pas « session expirée » :
// la page de connexion doit afficher son message d'erreur, pas être rechargée.
const AUTH_API_PREFIX = "/api/auth/";


export function installAuthInterceptor(router) {
  axios.interceptors.response.use(
    response => response,
    error => {
      const status = error.response?.status;
      const url = error.config?.url ?? "";

      if (status === 401 && !url.startsWith(AUTH_API_PREFIX) && AuthService.isLoggedIn()) {
        AuthService.clearConnectedUser();

        if (router.currentRoute.value.path !== "/login") {
          router.push("/login");
        }
      }

      return Promise.reject(error);
    }
  );
}