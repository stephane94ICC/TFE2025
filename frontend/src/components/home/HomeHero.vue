<template>
  <div class="hero-content">
    <span class="hero-eyebrow">
      {{ $t("public.home.subtitle") }}
    </span>

    <h1>{{ $t("public.home.title") }}</h1>

    <p class="hero-description">
      {{ $t("public.home.description") }}
    </p>

    <div class="hero-actions">
      <router-link to="/activities" class="home-btn home-btn-primary">
        {{ $t("public.home.activitiesButton") }}
      </router-link>

      <router-link to="/shop" class="home-btn home-btn-secondary">
        {{ $t("public.home.shopButton") }}
      </router-link>
    </div>
  </div>
</template>

<script>
export default {
  name: "HomeHero"
};
</script>

<style scoped src="./HomeHero.css"></style>
