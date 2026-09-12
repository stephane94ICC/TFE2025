<template>
  <main class="home-page">
    <section class="home-hero">
      <HomeHero />
      <HomeCarousel :activities="activities" :fallback-image="logo" />
    </section>

    <HomeCategories />

    <HomeSuggestions
        :activities="activities"
        :products="products"
        :loading="loading"
        :fallback-image="logo"
    />
  </main>
</template>

<script>
import { getActivities } from "../../services/ActivityService";
import { getProducts } from "../../services/ProductService";
import logo from "../../assets/logo.png";
import HomeHero from "../../components/home/HomeHero.vue";
import HomeCarousel from "../../components/home/HomeCarousel.vue";
import HomeCategories from "../../components/home/HomeCategories.vue";
import HomeSuggestions from "../../components/home/HomeSuggestions.vue";

export default {
  name: "HomePage",

  components: {
    HomeHero,
    HomeCarousel,
    HomeCategories,
    HomeSuggestions
  },

  data() {
    return {
      activities: [],
      products: [],
      loading: true,
      logo
    };
  },

  mounted() {
    this.loadHomeContent();
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
      } catch (error) {
        console.error("Impossible de charger le contenu de l'accueil :", error);
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>

<style scoped src="./HomePage.css"></style>
