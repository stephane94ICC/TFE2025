<template>
  <main class="activities-page">
    <header class="page-header">
      <h1>{{ $t("public.activityList.title") }}</h1>
      <p>{{ $t("public.activityList.subtitle") }}</p>
    </header>

    <div v-if="errorMessage" class="alert alert-error">
      {{ errorMessage }}
    </div>

    <p v-if="loading" class="catalog-message">
      {{ $t("public.activityList.loading") }}
    </p>

    <div v-else class="activity-catalog-layout">
      <ActivityFiltersSidebar
        v-model="filters"
        :categories="availableCategories"
        :cities="availableCities"
        :partners="availablePartners"
      />

      <section
        class="activity-results"
        :aria-label="$t('public.activityList.results')"
      >
        <div class="results-header">
          <p>
            {{
              $t(
                "public.activityList.resultsCount",
                { count: filteredActivities.length }
              )
            }}
          </p>
        </div>

        <div
          v-if="filteredActivities.length"
          class="activity-grid"
        >
          <article
            v-for="activity in filteredActivities"
            :key="activity.id"
            class="activity-card"
          >
            <div class="activity-image">
              <img
                v-if="getActivityImage(activity)"
                :src="getActivityImage(activity)"
                :alt="activity.title"
              />

              <span v-else>
                {{ activity.title.charAt(0) }}
              </span>
            </div>

            <div class="activity-content">
              <div class="activity-card__main">
                <h2>
                  {{ activity.title }}
                </h2>

                <p
                  v-if="activity.partnerName"
                  class="activity-partner"
                >
                  {{ activity.partnerName }}
                </p>

                <p class="description">
                  {{ activity.description }}
                </p>
              </div>

              <div class="activity-card__footer">
                <div class="activity-card__meta">
                  <span class="activity-duration">
                    {{ activity.durationMinutes }} min
                  </span>

                  <strong>
                    {{ formatPrice(activity.price) }}
                  </strong>
                </div>

                <router-link
                  :to="`/activities/${activity.id}`"
                  class="btn btn-primary"
                >
                  {{ $t("public.activityList.details") }}
                </router-link>
              </div>
            </div>
          </article>
        </div>

        <p
          v-else
          class="catalog-message"
        >
          {{ $t("public.activityList.empty") }}
        </p>
      </section>
    </div>
  </main>
</template>

<script>
import { getActivities } from "../../services/ActivityService";
import ActivityFiltersSidebar
  from "../../components/activity/ActivityFiltersSidebar.vue";

export default {
  name: "ActivityListPage",

  components: {
    ActivityFiltersSidebar
  },

  data() {
    return {
      activities: [],
      loading: false,
      errorMessage: "",

      filters: {
        category: "",
        city: "",
        partnerId: "",
        minPrice: "",
        maxPrice: "",
        availableOnly: false
      }
    };
  },

  computed: {
    availableCategories() {
      const matchingActivities = this.activities.filter(activity =>
        this.matchesFilters(
          activity,
          {
            ignoreCategory: true
          }
        )
      );

      const categories = matchingActivities.flatMap(
        activity => activity.categories || []
      );

      return [...new Set(categories)]
        .sort((a, b) => a.localeCompare(b));
    },

    availableCities() {
      const matchingActivities = this.activities.filter(activity =>
        this.matchesFilters(
          activity,
          {
            ignoreCity: true
          }
        )
      );

      const cities = matchingActivities.flatMap(
        activity => activity.cities || []
      );

      return [...new Set(cities)]
        .sort((a, b) => a.localeCompare(b));
    },

    availablePartners() {
      const matchingActivities = this.activities.filter(activity =>
        this.matchesFilters(
          activity,
          {
            ignorePartner: true
          }
        )
      );

      const partnersById = new Map();

      matchingActivities.forEach(activity => {
        if (
          activity.partnerId != null &&
          activity.partnerName
        ) {
          partnersById.set(
            activity.partnerId,
            {
              id: activity.partnerId,
              name: activity.partnerName
            }
          );
        }
      });

      return [...partnersById.values()]
        .sort((a, b) =>
          a.name.localeCompare(b.name)
        );
    },

    filteredActivities() {
      return this.activities.filter(activity =>
        this.matchesFilters(activity)
      );
    }
  },

  mounted() {
    this.loadActivities();
  },

  methods: {
    loadActivities() {
      this.loading = true;
      this.errorMessage = "";

      getActivities()
        .then(res => {
          this.activities = res.data;
        })
        .catch(err => {
          console.error(err);

          this.errorMessage =
            this.$t(
              "public.activityList.loadError"
            );
        })
        .finally(() => {
          this.loading = false;
        });
    },

    matchesFilters(activity, options = {}) {
      const {
        ignoreCategory = false,
        ignoreCity = false,
        ignorePartner = false
      } = options;

      if (
        !ignoreCategory &&
        this.filters.category &&
        !this.hasCategory(
          activity,
          this.filters.category
        )
      ) {
        return false;
      }

      if (
        !ignoreCity &&
        this.filters.city &&
        !this.hasCity(
          activity,
          this.filters.city
        )
      ) {
        return false;
      }

      if (
        !ignorePartner &&
        this.filters.partnerId &&
        String(activity.partnerId) !==
        String(this.filters.partnerId)
      ) {
        return false;
      }

      const price = Number(activity.price);

      if (
        this.filters.minPrice !== "" &&
        price < Number(this.filters.minPrice)
      ) {
        return false;
      }

      if (
        this.filters.maxPrice !== "" &&
        price > Number(this.filters.maxPrice)
      ) {
        return false;
      }

      if (
        this.filters.availableOnly &&
        activity.available !== true
      ) {
        return false;
      }

      return true;
    },

    hasCategory(activity, category) {
      return Array.isArray(activity.categories) &&
        activity.categories.includes(category);
    },

    hasCity(activity, city) {
      return Array.isArray(activity.cities) &&
        activity.cities.includes(city);
    },

    getActivityImage(activity) {
      if (
        activity.imageUrls &&
        activity.imageUrls.length > 0
      ) {
        return activity.imageUrls[0];
      }

      return null;
    },

    formatPrice(price) {
      const numericPrice = Number(price);

      if (Number.isNaN(numericPrice)) {
        return `${price} €`;
      }

      return `${numericPrice
        .toFixed(2)
        .replace(".00", "")} €`;
    }
  }
};
</script>

<style scoped src="./ActivityListPage.css"></style>