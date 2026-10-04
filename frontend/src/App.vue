<template>
  <div id="app">
    <AppNavbar />

    <main
      class="main-content"
      :class="{ 'main-content--column': hasPartnerFooter }"
    >
      <router-view />
    </main>

    <AppFooter v-if="!hasPartnerFooter" />
  </div>
</template>

<script>
import AppNavbar from './components/AppNavbar.vue';
import AppFooter from './components/AppFooter.vue';

export default {
  name: 'App',
  components: {
    AppNavbar,
    AppFooter
  },

  computed: {
    hasPartnerFooter() {
      return this.$route.meta.partnerFooter === true;
    }
  }
};
</script>

<style>
#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--bl-background);
}

.main-content {
  flex: 1;
}

/* Vitrine partenaire : la page remplit la hauteur, son pied de page reste en bas */
.main-content--column {
  display: flex;
  flex-direction: column;
}
</style>