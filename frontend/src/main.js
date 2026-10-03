import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import i18n from "./i18n";
import "./assets/styles/theme.css";

const savedLanguage = localStorage.getItem("language");

if (savedLanguage && i18n.global.availableLocales.includes(savedLanguage)) {
    i18n.global.locale.value = savedLanguage;
}

document.documentElement.lang = i18n.global.locale.value;
document.title = i18n.global.t("navbar.documentTitle");

const app = createApp(App);

app.config.globalProperties.$price = (amount) =>
    i18n.global.n(Number(amount), "currency", "fr");

app
    .use(router)
    .use(i18n)
    .mount('#app');