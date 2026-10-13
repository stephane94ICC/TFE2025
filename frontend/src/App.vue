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

    <SessionExpiryNotice />
  </div>
</template>

<script>
import AppNavbar from './components/AppNavbar.vue';
import AppFooter from './components/AppFooter.vue';
import SessionExpiryNotice from './components/SessionExpiryNotice.vue';

export default {
  name: 'App',
  components: {
    AppNavbar,
    AppFooter,
    SessionExpiryNotice
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

.main-content--column {
  display: flex;
  flex-direction: column;
}
</style>
