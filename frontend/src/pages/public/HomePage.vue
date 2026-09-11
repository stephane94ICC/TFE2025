<template>
  <main class="home-page">
    <!-- HERO + CAROUSEL -->
    <section class="home-hero">
      <div class="hero-content">
        <span class="hero-eyebrow">
          {{ $t("public.home.subtitle") }}
        </span>

        <h1>{{ $t("public.home.title") }}</h1>

        <p class="hero-description">
          {{ $t("public.home.description") }}
        </p>

        <!-- RECHERCHE -->
        <form class="home-search" @submit.prevent="submitSearch">
          <input
              v-model="searchTerm"
              type="search"
              :placeholder="$t('public.home.searchPlaceholder')"
              @input="searchOpen = true"
          />

          <button type="submit">
            {{ $t("public.home.searchButton") }}
          </button>

          <div
              v-if="searchOpen && searchTerm.trim().length >= 2"
              class="search-results"
          >
            <router-link
                v-for="result in searchResults"
                :key="`${result.type}-${result.id}`"
                :to="result.route"
                class="search-result"
                @click="searchOpen = false"
            >
              <img :src="result.image" :alt="result.title">

              <div>
                <strong>{{ result.title }}</strong>
                <span>
                  {{
                    result.type === "activity"
                      ? $t("public.home.activity")
                      : $t("public.home.product")
                  }}
                </span>
              </div>
            </router-link>

            <p v-if="searchResults.length === 0" class="search-empty">
              {{ $t("public.home.noSearchResults") }}
            </p>
          </div>
        </form>

        <div class="hero-actions">
          <router-link to="/activities" class="home-btn home-btn-primary">
            {{ $t("public.home.activitiesButton") }}
          </router-link>

          <router-link to="/shop" class="home-btn home-btn-secondary">
            {{ $t("public.home.shopButton") }}
          </router-link>
        </div>
      </div>

      <!-- CAROUSEL -->
      <div class="home-carousel">
        <div class="carousel-window">
          <article
              v-for="(slide, index) in carouselSlides"
              :key="slide.id"
              :class="[
                'carousel-slide',
                { 'carousel-slide-active': index === currentSlide }
              ]"
          >
            <img
                :src="getActivityImage(slide)"
                :alt="slide.title"
                class="carousel-image"
            >

            <div class="carousel-overlay"></div>

            <div class="carousel-content">
              <span class="carousel-label">
                {{ $t("public.home.featured") }}
              </span>

              <h2>{{ slide.title }}</h2>

              <p v-if="slide.description">
                {{ truncate(slide.description, 115) }}
              </p>

              <div class="carousel-bottom">
                <strong v-if="slide.price !== null && slide.price !== undefined">
                  {{ $t("public.home.from") }} {{ slide.price }} €
                </strong>

                <router-link
                    v-if="slide.id !== 'fallback'"
                    :to="`/activities/${slide.id}`"
                    class="carousel-action"
                >
                  {{ $t("public.home.book") }}
                </router-link>

                <router-link
                    v-else
                    to="/activities"
                    class="carousel-action"
                >
                  {{ $t("public.home.discover") }}
                </router-link>
              </div>
            </div>
          </article>

          <button
              v-if="carouselSlides.length > 1"
              type="button"
              class="carousel-control carousel-control-left"
              :aria-label="$t('public.home.previous')"
              @click="previousSlide"
          >
            ‹
          </button>

          <button
              v-if="carouselSlides.length > 1"
              type="button"
              class="carousel-control carousel-control-right"
              :aria-label="$t('public.home.next')"
              @click="nextSlide"
          >
            ›
          </button>

          <div v-if="carouselSlides.length > 1" class="carousel-indicators">
            <button
                v-for="(slide, index) in carouselSlides"
                :key="`indicator-${slide.id}`"
                type="button"
                :class="{ active: index === currentSlide }"
                :aria-label="`${$t('public.home.slide')} ${index + 1}`"
                @click="goToSlide(index)"
            ></button>
          </div>
        </div>
      </div>
    </section>

    <!-- CATEGORIES -->
    <section class="home-categories">
      <span class="category category-wellness">
        {{ $t("public.home.categories.wellness") }}
      </span>

      <span class="category category-sport">
        {{ $t("public.home.categories.sport") }}
      </span>

      <span class="category category-bars">
        {{ $t("public.home.categories.bars") }}
      </span>

      <span class="category category-culture">
        {{ $t("public.home.categories.culture") }}
      </span>

      <span class="category category-shop">
        {{ $t("public.home.categories.shop") }}
      </span>
    </section>

    <!-- SUGGESTIONS -->
    <section class="home-suggestions">
      <div class="section-heading">
        <div>
          <span class="section-kicker">
            {{ $t("public.home.selection") }}
          </span>

          <h2>{{ $t("public.home.suggestionsTitle") }}</h2>
        </div>

        <router-link to="/activities" class="section-link">
          {{ $t("public.home.viewAll") }} →
        </router-link>
      </div>

      <div v-if="loading" class="home-loading">
        {{ $t("public.home.loading") }}
      </div>

      <div v-else class="suggestion-grid">
        <article
            v-for="activity in featuredActivities"
            :key="`activity-${activity.id}`"
            class="suggestion-card"
        >
          <router-link
              :to="`/activities/${activity.id}`"
              class="suggestion-image-wrapper"
          >
            <img
                :src="getActivityImage(activity)"
                :alt="activity.title"
                class="suggestion-image"
            >
          </router-link>

          <div class="suggestion-content">
            <span class="suggestion-type">
              {{ $t("public.home.activity") }}
            </span>

            <h3>{{ activity.title }}</h3>

            <p v-if="activity.description">
              {{ truncate(activity.description, 75) }}
            </p>

            <div class="suggestion-footer">
              <strong>{{ activity.price }} €</strong>

              <router-link
                  :to="`/activities/${activity.id}`"
                  class="suggestion-action"
              >
                {{ $t("public.home.book") }}
              </router-link>
            </div>
          </div>
        </article>

        <article
            v-if="featuredProduct"
            class="suggestion-card suggestion-product"
        >
          <router-link
              :to="`/products/${featuredProduct.id}`"
              class="suggestion-image-wrapper"
          >
            <img
                :src="getProductImage(featuredProduct)"
                :alt="featuredProduct.name"
                class="suggestion-image"
            >
          </router-link>

          <div class="suggestion-content">
            <span class="suggestion-type">
              {{ $t("public.home.product") }}
            </span>

            <h3>{{ featuredProduct.name }}</h3>

            <p v-if="featuredProduct.description">
              {{ truncate(featuredProduct.description, 75) }}
            </p>

            <div class="suggestion-footer">
              <strong>{{ featuredProduct.price }} €</strong>

              <router-link
                  :to="`/products/${featuredProduct.id}`"
                  class="suggestion-action suggestion-action-product"
              >
                {{ $t("public.home.view") }}
              </router-link>
            </div>
          </div>
        </article>
      </div>

      <div
          v-if="!loading && featuredActivities.length === 0 && !featuredProduct"
          class="suggestions-empty"
      >
        <h3>{{ $t("public.home.emptyTitle") }}</h3>
        <p>{{ $t("public.home.emptyText") }}</p>
      </div>
    </section>
  </main>
</template>

<script>
import { getActivities } from "../../services/ActivityService";
import { getProducts } from "../../services/ProductService";
import logo from "../../assets/logo.png";

export default {
  name: "HomePage",

  data() {
    return {
      activities: [],
      products: [],
      loading: true,
      currentSlide: 0,
      carouselTimer: null,
      searchTerm: "",
      searchOpen: false,
      logo
    };
  },

  computed: {
    carouselSlides() {
      if (this.activities.length > 0) {
        return this.activities.slice(0, 3);
      }

      return [
        {
          id: "fallback",
          title: this.$t("public.home.title"),
          description: this.$t("public.home.description"),
          price: null,
          imageUrls: [this.logo]
        }
      ];
    },

    featuredActivities() {
      return this.activities.slice(0, 3);
    },

    featuredProduct() {
      return this.products.length > 0 ? this.products[0] : null;
    },

    searchResults() {
      const query = this.normalizeText(this.searchTerm);

      if (query.length < 2) {
        return [];
      }

      const activityResults = this.activities
          .filter(activity => {
            return (
                this.normalizeText(activity.title).includes(query) ||
                this.normalizeText(activity.description).includes(query)
            );
          })
          .slice(0, 4)
          .map(activity => ({
            id: activity.id,
            type: "activity",
            title: activity.title,
            route: `/activities/${activity.id}`,
            image: this.getActivityImage(activity)
          }));

      const productResults = this.products
          .filter(product => {
            return (
                this.normalizeText(product.name).includes(query) ||
                this.normalizeText(product.description).includes(query)
            );
          })
          .slice(0, 2)
          .map(product => ({
            id: product.id,
            type: "product",
            title: product.name,
            route: `/products/${product.id}`,
            image: this.getProductImage(product)
          }));

      return [...activityResults, ...productResults].slice(0, 6);
    }
  },

  mounted() {
    this.loadHomeContent();
    this.startCarousel();
  },

  beforeUnmount() {
    this.stopCarousel();
  },

  methods: {
    async loadHomeContent() {
      try {
        this.loading = true;

        const [activitiesResponse, productsResponse] = await Promise.all([
          getActivities(),
          getProducts()
        ]);

        this.activities = activitiesResponse.data || [];
        this.products = productsResponse.data || [];

        if (this.currentSlide >= this.carouselSlides.length) {
          this.currentSlide = 0;
        }
      } catch (error) {
        console.error("Impossible de charger le contenu de l'accueil :", error);
      } finally {
        this.loading = false;
      }
    },

    startCarousel() {
      this.stopCarousel();

      this.carouselTimer = window.setInterval(() => {
        if (this.carouselSlides.length > 1) {
          this.nextSlide();
        }
      }, 5000);
    },

    stopCarousel() {
      if (this.carouselTimer) {
        window.clearInterval(this.carouselTimer);
        this.carouselTimer = null;
      }
    },

    nextSlide() {
      this.currentSlide =
          (this.currentSlide + 1) % this.carouselSlides.length;

      this.restartCarousel();
    },

    previousSlide() {
      this.currentSlide =
          (this.currentSlide - 1 + this.carouselSlides.length) %
          this.carouselSlides.length;

      this.restartCarousel();
    },

    goToSlide(index) {
      this.currentSlide = index;
      this.restartCarousel();
    },

    restartCarousel() {
      this.startCarousel();
    },

    getActivityImage(activity) {
      if (activity?.imageUrls?.length > 0) {
        return activity.imageUrls[0];
      }

      return this.logo;
    },

    getProductImage(product) {
      if (product?.imageUrls?.length > 0) {
        return product.imageUrls[0];
      }

      if (product?.imageUrl) {
        return product.imageUrl;
      }

      return this.logo;
    },

    truncate(value, maxLength) {
      if (!value) {
        return "";
      }

      return value.length > maxLength
          ? `${value.substring(0, maxLength).trim()}…`
          : value;
    },

    normalizeText(value) {
      return String(value || "")
          .normalize("NFD")
          .replace(/[\u0300-\u036f]/g, "")
          .toLowerCase();
    },

    submitSearch() {
      this.searchOpen = true;

      if (this.searchResults.length > 0) {
        this.$router.push(this.searchResults[0].route);
      }
    }
  }
};
</script>

<style scoped src="./HomePage.css"></style>