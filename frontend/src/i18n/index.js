import { createI18n } from "vue-i18n";

import navbarFr from "./locales/navbar/fr";
import navbarNl from "./locales/navbar/nl";
import navbarEn from "./locales/navbar/en";

import authFr from "./locales/auth/fr";
import authNl from "./locales/auth/nl";
import authEn from "./locales/auth/en";

import adminFr from "./locales/admin/fr";
import adminNl from "./locales/admin/nl";
import adminEn from "./locales/admin/en";

import memberFr from "./locales/member/fr";
import memberNl from "./locales/member/nl";
import memberEn from "./locales/member/en";

import partnerFr from "./locales/partner/fr";
import partnerNl from "./locales/partner/nl";
import partnerEn from "./locales/partner/en";

import publicFr from "./locales/public/fr";
import publicNl from "./locales/public/nl";
import publicEn from "./locales/public/en";

import paymentFr from "./locales/payment/fr";
import paymentNl from "./locales/payment/nl";
import paymentEn from "./locales/payment/en";

const i18n = createI18n({
  legacy: false,
  locale: "fr",
  fallbackLocale: "fr",

  messages: {
    fr: {
      navbar: navbarFr,
      auth: authFr,
      admin: adminFr,
      member: memberFr,
      partner: partnerFr,
      public: publicFr,
      payment: paymentFr
    },

    nl: {
      navbar: navbarNl,
      auth: authNl,
      admin: adminNl,
      member: memberNl,
      partner: partnerNl,
      public: publicNl,
      payment: paymentNl
    },

    en: {
      navbar: navbarEn,
      auth: authEn,
      admin: adminEn,
      member: memberEn,
      partner: partnerEn,
      public: publicEn,
      payment: paymentEn
    }
  }
});

export default i18n;