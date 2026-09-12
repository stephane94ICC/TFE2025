<template>
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
</template>

<script>
export default {
  name: "HomeCategories"
};
</script>

<style scoped src="./HomeCategories.css"></style>
