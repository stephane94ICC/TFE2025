<template>
  <div id="app">
    <AppNavbar />

    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script>
import AppNavbar from './components/AppNavbar.vue';

export default {
  name: 'App',
  components: {
    AppNavbar
  }
};
</script>

<style>
#app {
  min-height: 100vh;
  background: var(--bl-background);
}

.main-content {
  min-height: calc(100vh - 88px);
}
</style>
