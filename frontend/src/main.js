import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import i18n from "./i18n";
import "./assets/styles/theme.css";
import { installAuthInterceptor } from "./services/AuthInterceptor";

const savedLanguage = localStorage.getItem("language");

if (savedLanguage && i18n.global.availableLocales.includes(savedLanguage)) {
    i18n.global.locale.value = savedLanguage;
}

document.documentElement.lang = i18n.global.locale.value;
document.title = i18n.global.t("navbar.documentTitle");

// Réponse 401 de l'API : session expirée, retour à la page de connexion.
installAuthInterceptor(router);

const app = createApp(App);

app.config.globalProperties.$price = (amount) =>
    i18n.global.n(Number(amount), "currency", "fr");

app
    .use(router)
    .use(i18n)
    .mount('#app');