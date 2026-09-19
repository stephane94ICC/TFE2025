<template>
  <div class="navbar-search" @click.stop>
    <form class="navbar-search-form" @submit.prevent="submitSearch">
      <input
          v-model="searchTerm"
          type="search"
          :placeholder="$t('public.home.searchPlaceholder')"
          :aria-label="$t('public.home.searchPlaceholder')"
          @focus="searchOpen = true"
          @input="searchOpen = true"
      >

      <button type="submit">
        {{ $t("public.home.searchButton") }}
      </button>
    </form>

    <div
        v-if="searchOpen && searchTerm.trim().length >= 2"
        class="navbar-search-results"
    >
      <router-link
          v-for="result in searchResults"
          :key="`${result.type}-${result.id}`"
          :to="result.route"
          class="navbar-search-result"
          @click="closeSearch"
      >
        <img
            v-if="result.image"
            :src="result.image"
            alt=""
            @error="hideBrokenImage"
        >

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

      <p v-if="searchResults.length === 0" class="navbar-search-empty">
        {{ $t("public.home.noSearchResults") }}
      </p>
    </div>
  </div>
</template>

<script>
import { getActivities } from "../services/ActivityService";
import { getProducts } from "../services/ProductService";

export default {
  name: "NavbarSearch",

  data() {
    return {
      activities: [],
      products: [],
      searchTerm: "",
      searchOpen: false
    };
  },

  computed: {
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
    this.loadSearchContent();
    document.addEventListener("click", this.closeSearch);
  },

  beforeUnmount() {
    document.removeEventListener("click", this.closeSearch);
  },

  watch: {
    $route() {
      this.closeSearch();
    }
  },

  methods: {
    async loadSearchContent() {
      try {
        const [activitiesResponse, productsResponse] = await Promise.all([
          getActivities(),
          getProducts()
        ]);

        this.activities = activitiesResponse.data || [];
        this.products = productsResponse.data || [];
      } catch (error) {
        console.error("Impossible de charger les données de recherche :", error);
      }
    },

    // Le backend garantit au moins une image (image par défaut non supprimable).
    getActivityImage(activity) {
      return activity?.imageUrls?.[0] || "";
    },

    getProductImage(product) {
      return product?.imageUrls?.[0] || "";
    },

    // Fichier absent du disque (404) : on masque l'image plutôt que
    // d'afficher l'icône « image cassée ».
    hideBrokenImage(event) {
      event.target.style.display = "none";
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
    },

    closeSearch() {
      this.searchOpen = false;
    }
  }
};
</script>

<style scoped src="./NavbarSearch.css"></style>