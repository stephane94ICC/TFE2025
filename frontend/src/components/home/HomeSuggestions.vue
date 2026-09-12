<template>
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
</template>

<script>
export default {
  name: "HomeSuggestions",

  props: {
    activities: {
      type: Array,
      default: () => []
    },
    products: {
      type: Array,
      default: () => []
    },
    loading: {
      type: Boolean,
      default: false
    },
    fallbackImage: {
      type: String,
      required: true
    }
  },

  computed: {
    featuredActivities() {
      return this.activities.slice(0, 3);
    },

    featuredProduct() {
      return this.products.length > 0 ? this.products[0] : null;
    }
  },

  methods: {
    getActivityImage(activity) {
      if (activity?.imageUrls?.length > 0) {
        return activity.imageUrls[0];
      }

      return this.fallbackImage;
    },

    getProductImage(product) {
      if (product?.imageUrls?.length > 0) {
        return product.imageUrls[0];
      }

      if (product?.imageUrl) {
        return product.imageUrl;
      }

      return this.fallbackImage;
    },

    truncate(value, maxLength) {
      if (!value) {
        return "";
      }

      return value.length > maxLength
          ? `${value.substring(0, maxLength).trim()}…`
          : value;
    }
  }
};
</script>

<style scoped src="./HomeSuggestions.css"></style>
