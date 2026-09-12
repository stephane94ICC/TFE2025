<template>
  <aside
      class="activity-filters"
      :aria-label="$t('public.activityList.filters.title')"
  >
    <div class="activity-filters__heading">
      <h2>{{ $t("public.activityList.filters.title") }}</h2>

      <button
          type="button"
          class="activity-filters__reset"
          @click="resetFilters"
      >
        {{ $t("public.activityList.filters.reset") }}
      </button>
    </div>

    <div class="activity-filters__list">

      <!-- Catégorie -->
      <div class="activity-filter-group">
        <label for="activity-filter-category">
          {{ $t("public.activityList.filters.type") }}
        </label>

        <select
            id="activity-filter-category"
            :value="modelValue.category"
            @change="updateFilter('category', $event.target.value)"
        >
          <option value="">
            {{ $t("public.activityList.filters.all") }}
          </option>

          <option
              v-for="category in categories"
              :key="category"
              :value="category"
          >
            {{ category }}
          </option>
        </select>
      </div>

      <!-- Ville -->
      <div class="activity-filter-group">
        <label for="activity-filter-city">
          {{ $t("public.activityList.filters.location") }}
        </label>

        <select
            id="activity-filter-city"
            :value="modelValue.city"
            @change="updateFilter('city', $event.target.value)"
        >
          <option value="">
            {{ $t("public.activityList.filters.all") }}
          </option>

          <option
              v-for="city in cities"
              :key="city"
              :value="city"
          >
            {{ city }}
          </option>
        </select>
      </div>

      <!-- Partenaire -->
      <div class="activity-filter-group">
        <label for="activity-filter-partner">
          {{ $t("public.activityList.filters.partner") }}
        </label>

        <select
            id="activity-filter-partner"
            :value="modelValue.partnerId"
            @change="updateFilter('partnerId', $event.target.value)"
        >
          <option value="">
            {{ $t("public.activityList.filters.all") }}
          </option>

          <option
              v-for="partner in partners"
              :key="partner.id"
              :value="String(partner.id)"
          >
            {{ partner.name }}
          </option>
        </select>
      </div>

      <!-- Prix -->
      <div class="activity-filter-group">
        <span class="activity-filter-label">
          {{ $t("public.activityList.filters.price") }}
        </span>

        <div class="activity-price-fields">
          <label>
            <span>{{ $t("public.activityList.filters.minPrice") }}</span>

            <input
                type="number"
                min="0"
                step="1"
                :value="modelValue.minPrice"
                @input="updateFilter('minPrice', $event.target.value)"
            />
          </label>

          <label>
            <span>{{ $t("public.activityList.filters.maxPrice") }}</span>

            <input
                type="number"
                min="0"
                step="1"
                :value="modelValue.maxPrice"
                @input="updateFilter('maxPrice', $event.target.value)"
            />
          </label>
        </div>
      </div>

      <!-- Disponibilité -->
      <div class="activity-filter-group activity-filter-group--checkbox">
        <label>
          <input
              type="checkbox"
              :checked="modelValue.availableOnly"
              @change="updateFilter('availableOnly', $event.target.checked)"
          />

          <span>
            {{ $t("public.activityList.filters.availableOnly") }}
          </span>
        </label>
      </div>

    </div>
  </aside>
</template>

<script>
export default {
  name: "ActivityFiltersSidebar",

  props: {
    modelValue: {
      type: Object,
      required: true
    },

    categories: {
      type: Array,
      default: () => []
    },

    cities: {
      type: Array,
      default: () => []
    },

    partners: {
      type: Array,
      default: () => []
    }
  },

  emits: [
    "update:modelValue"
  ],

  methods: {
    updateFilter(key, value) {
      this.$emit("update:modelValue", {
        ...this.modelValue,
        [key]: value
      });
    },

    resetFilters() {
      this.$emit("update:modelValue", {
        category: "",
        city: "",
        partnerId: "",
        minPrice: "",
        maxPrice: "",
        availableOnly: false
      });
    }
  }
};
</script>

<style scoped src="./ActivityFiltersSidebar.css"></style>