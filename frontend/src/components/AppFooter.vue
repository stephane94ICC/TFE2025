<template>
  <footer class="app-footer" :aria-label="$t('footer.ariaLabel')">
    <div class="app-footer-inner">
      <nav class="app-footer-links">
        <router-link to="/conditions-generales">
          {{ $t("footer.terms") }}
        </router-link>

        <router-link to="/politique-confidentialite">
          {{ $t("footer.privacy") }}
        </router-link>

        <!-- Adresse fictive, identique à celle de la page RGPD -->
        <a href="mailto:contact@belloisirs.example">
          {{ $t("footer.contact") }} : contact@belloisirs.example
        </a>
      </nav>

      <p class="app-footer-project">
        {{ $t("footer.project") }}
      </p>
    </div>
  </footer>
</template>

<script>
// Pied de page commun à toutes les pages : informations légales toujours accessibles.
export default {
  name: "AppFooter"
};
</script>

<style scoped src="./AppFooter.css"></style>