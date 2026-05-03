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
body {
  margin: 0;
  background: #f3f4f6;
  font-family: Arial, sans-serif;
}

.main-content {
  min-height: calc(100vh - 70px);
}
</style>