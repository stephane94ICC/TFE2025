<template>
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
</template>

<script>
export default {
  name: "HomeCarousel",

  props: {
    activities: {
      type: Array,
      default: () => []
    },
    fallbackImage: {
      type: String,
      required: true
    }
  },

  data() {
    return {
      currentSlide: 0,
      carouselTimer: null
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
          imageUrls: [this.fallbackImage]
        }
      ];
    }
  },

  watch: {
    carouselSlides() {
      if (this.currentSlide >= this.carouselSlides.length) {
        this.currentSlide = 0;
      }
    }
  },

  mounted() {
    this.startCarousel();
  },

  beforeUnmount() {
    this.stopCarousel();
  },

  methods: {
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

<style scoped src="./HomeCarousel.css"></style>
