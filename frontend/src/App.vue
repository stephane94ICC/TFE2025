<template>
  <div id="app">
    <AppNavbar />

    <main class="main-content">
      <router-view />
    </main>

    <AppFooter />
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
  }
};
</script>

<style>
/* Colonne pleine hauteur : le pied de page reste en bas, même sur une page courte */
#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--bl-background);
}

.main-content {
  flex: 1;
}
</style>