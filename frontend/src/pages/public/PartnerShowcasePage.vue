<template>
  <div class="partner-showcase-page">
    <!-- Logo du partenaire en fond de page, très atténué (décoratif) -->
    <div
        v-if="backgroundStyle"
        class="showcase-background"
        :style="backgroundStyle"
        aria-hidden="true"
    ></div>

    <div class="partner-showcase">
      <router-link to="/activities" class="back-link">
        ← {{ $t("public.partnerShowcase.back") }}
      </router-link>

      <p v-if="loading" class="showcase-message">
        {{ $t("public.partnerShowcase.loading") }}
      </p>

      <!-- Slug inconnu ou partenaire désactivé : le back renvoie 404 -->
      <section v-else-if="notFound" class="showcase-not-found">
        <h1>{{ $t("public.partnerShowcase.notFoundTitle") }}</h1>
        <p>{{ $t("public.partnerShowcase.notFoundText") }}</p>
        <router-link to="/activities" class="btn btn-primary">
          {{ $t("public.partnerShowcase.browseActivities") }}
        </router-link>
      </section>

      <div v-else-if="errorMessage" class="alert alert-error">
        {{ errorMessage }}
      </div>

      <template v-else-if="partner">
        <!-- 1. Titre seul, dans un cadre ajusté et centré -->
        <header class="showcase-header">
          <h1>{{ partner.name }}</h1>
        </header>

        <!-- 2. Présentation : texte brut, jamais v-html (contenu saisi par le partenaire) -->
        <section
            v-if="partner.description"
            class="showcase-section showcase-about"
            aria-labelledby="showcase-about-title"
        >
          <h2 id="showcase-about-title">
            {{ $t("public.partnerShowcase.about") }}
          </h2>
          <p class="showcase-description">{{ partner.description }}</p>
        </section>

        <!-- 3. Activités : cartes horizontales, l'une sous l'autre -->
        <section
            class="showcase-section"
            aria-labelledby="showcase-activities-title"
        >
          <h2 id="showcase-activities-title">
            {{ $t("public.partnerShowcase.activitiesTitle") }}
          </h2>

          <div v-if="activitiesError" class="alert alert-error">
            {{ activitiesError }}
          </div>

          <p v-else-if="!activities.length" class="showcase-message">
            {{ $t("public.partnerShowcase.activitiesEmpty") }}
          </p>

          <ul v-else class="showcase-list">
            <li v-for="activity in activities" :key="activity.id">
              <article class="showcase-card">
                <div class="showcase-card__image">
                  <img
                      v-if="getActivityImage(activity)"
                      :src="getActivityImage(activity)"
                      :alt="activity.title"
                  />
                  <span v-else aria-hidden="true">
                    {{ activity.title.charAt(0) }}
                  </span>
                </div>

                <div class="showcase-card__content">
                  <h3>{{ activity.title }}</h3>

                  <p class="showcase-card__description">
                    {{ activity.description }}
                  </p>

                  <p class="showcase-card__meta">
                    <span>{{ activity.durationMinutes }} min</span>
                    <span v-if="activity.cities && activity.cities.length">
                      {{ activity.cities.join(", ") }}
                    </span>
                    <span
                        class="showcase-badge"
                        :class="activity.available ? 'is-available' : 'is-unavailable'"
                    >
                      {{
                        activity.available
                            ? $t("public.partnerShowcase.available")
                            : $t("public.partnerShowcase.unavailable")
                      }}
                    </span>
                  </p>
                </div>

                <div class="showcase-card__aside">
                  <strong class="showcase-card__price">
                    {{ $price(activity.price) }}
                  </strong>
                  <router-link
                      :to="`/activities/${activity.id}`"
                      class="btn btn-primary"
                  >
                    {{ $t("public.partnerShowcase.details") }}
                  </router-link>
                </div>
              </article>
            </li>
          </ul>
        </section>
      </template>
    </div>

    <!-- 4. Pied de page du partenaire : remplace celui du site sur cette page
         (route meta.partnerFooter). Les liens légaux de la plateforme restent
         accessibles dans la dernière ligne : obligation sur toutes les pages. -->
    <footer
        v-if="partner"
        class="partner-footer"
        :aria-label="$t('public.partnerShowcase.footerLabel', { name: partner.name })"
    >
      <div class="partner-footer__inner">
        <div class="partner-footer__identity">
          <h2>{{ partner.name }}</h2>
          <p v-if="partner.enterpriseNumber">
            {{
              $t("public.partnerShowcase.enterpriseNumber", {
                number: partner.enterpriseNumber
              })
            }}
          </p>
        </div>

        <dl v-if="hasContact" class="contact-list">
          <div v-if="websiteHref">
            <dt>{{ $t("public.partnerShowcase.website") }}</dt>
            <dd>
              <a :href="websiteHref" target="_blank" rel="noopener noreferrer">
                {{ websiteLabel }}
              </a>
            </dd>
          </div>

          <div v-if="partner.phone">
            <dt>{{ $t("public.partnerShowcase.phone") }}</dt>
            <dd>
              <a :href="phoneHref">{{ partner.phone }}</a>
            </dd>
          </div>

          <div v-if="partner.email">
            <dt>{{ $t("public.partnerShowcase.email") }}</dt>
            <dd>
              <a :href="`mailto:${partner.email}`">{{ partner.email }}</a>
            </dd>
          </div>

          <div v-for="(address, index) in partner.addresses" :key="index">
            <dt>{{ $t("public.partnerShowcase.address") }}</dt>
            <dd>
              <address>
                {{ formatStreet(address) }}<br />
                {{ address.postalCode }} {{ address.city }}<br />
                {{ address.country }}
              </address>
            </dd>
          </div>
        </dl>
      </div>

      <!-- Ligne plateforme : qui héberge la page + informations légales -->
      <div class="partner-footer__platform">
        <router-link to="/">
          {{ $t("public.partnerShowcase.hostedBy") }}
        </router-link>
        <router-link to="/conditions-generales">
          {{ $t("footer.terms") }}
        </router-link>
        <router-link to="/politique-confidentialite">
          {{ $t("footer.privacy") }}
        </router-link>
      </div>
    </footer>
  </div>
</template>

<script>
import { getPublicPartner } from "../../services/PublicPartnerService";
import { getActivities } from "../../services/ActivityService";

// Chemin attribué par Partner.prePersist quand aucun logo n'est envoyé
const DEFAULT_LOGO_URL = "/uploads/partners/default-logo.png";

export default {
  name: "PartnerShowcasePage",

  data() {
    return {
      partner: null,
      activities: [],
      loading: false,
      notFound: false,
      errorMessage: "",
      activitiesError: ""
    };
  },

  computed: {
    /**
     * Logo réel uniquement : le logo par défaut donnerait le même fond partout.
     * Le chemin vient du serveur (upload) ; les guillemets sont retirés par précaution.
     */
    backgroundStyle() {
      const logo = this.partner && this.partner.logoUrl;

      if (!logo || logo === DEFAULT_LOGO_URL) {
        return null;
      }

      return { backgroundImage: `url("${logo.replace(/["\\]/g, "")}")` };
    },

    /**
     * Le site est saisi librement par le partenaire : seuls http et https
     * sont acceptés. Un lien "javascript:..." ne doit jamais devenir cliquable.
     */
    websiteHref() {
      const raw = (this.partner && this.partner.website || "").trim();
      if (!raw) {
        return null;
      }

      try {
        const url = new URL(raw.includes("://") ? raw : `https://${raw}`);
        return ["http:", "https:"].includes(url.protocol) ? url.href : null;
      } catch (e) {
        return null;
      }
    },

    // Affichage lisible : bruxelles-yoga.example plutôt que https://bruxelles-yoga.example/
    websiteLabel() {
      return this.websiteHref
          ? new URL(this.websiteHref).host.replace(/^www\./, "")
          : "";
    },

    phoneHref() {
      return `tel:${(this.partner.phone || "").replace(/[^+\d]/g, "")}`;
    },

    hasContact() {
      return Boolean(
          this.websiteHref ||
          this.partner.phone ||
          this.partner.email ||
          (this.partner.addresses && this.partner.addresses.length)
      );
    }
  },

  watch: {
    // Même composant réutilisé si l'on passe d'une vitrine à une autre
    "$route.params.slug"(newSlug) {
      if (newSlug) {
        this.loadShowcase();
      }
    }
  },

  mounted() {
    this.loadShowcase();
  },

  methods: {
    loadShowcase() {
      const slug = this.$route.params.slug;

      this.loading = true;
      this.notFound = false;
      this.errorMessage = "";
      this.activitiesError = "";
      this.partner = null;
      this.activities = [];

      // Deux appels en parallèle ; l'échec des activités n'empêche pas la vitrine
      Promise.allSettled([getPublicPartner(slug), getActivities()])
          .then(([partnerResult, activitiesResult]) => {
            if (partnerResult.status === "rejected") {
              const status = partnerResult.reason?.response?.status;

              if (status === 404) {
                this.notFound = true;
              } else {
                console.error(partnerResult.reason);
                this.errorMessage = this.$t("public.partnerShowcase.loadError");
              }
              return;
            }

            this.partner = partnerResult.value.data;

            if (activitiesResult.status === "fulfilled") {
              // Limite assumée : tout le catalogue public est chargé puis filtré
              this.activities = activitiesResult.value.data.filter(
                  activity => activity.partnerSlug === this.partner.slug
              );
            } else {
              console.error(activitiesResult.reason);
              this.activitiesError = this.$t(
                  "public.partnerShowcase.activitiesLoadError"
              );
            }
          })
          .finally(() => {
            this.loading = false;
          });
    },

    formatStreet(address) {
      const street = `${address.street} ${address.houseNumber}`;

      return address.box
          ? `${street} ${this.$t("public.partnerShowcase.box", { box: address.box })}`
          : street;
    },

    getActivityImage(activity) {
      return activity.imageUrls && activity.imageUrls.length > 0
          ? activity.imageUrls[0]
          : null;
    }
  }
};
</script>

<style scoped src="./PartnerShowcasePage.css"></style>