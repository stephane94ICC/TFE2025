<template>
  <div class="home-page">
    <section class="hero">
      <div class="hero-content">
        <p class="subtitle">Plateforme de réservation et d’achat</p>

        <h1>Découvrez des activités de loisirs simplement</h1>

        <p class="description">
          La plateforme permet aux utilisateurs de consulter des activités, de réserver des services
          et d’accéder à une boutique liée aux loisirs.
        </p>

        <div class="hero-actions">
          <router-link to="/activities" class="btn btn-primary">
            Voir les activités
          </router-link>

          <router-link to="/shop" class="btn btn-secondary">
            Voir la boutique
          </router-link>
        </div>
      </div>
    </section>

    <section class="features">
      <div class="feature-card">
        <h2>Activités</h2>
        <p>Consultez les activités sportives, culturelles et de bien-être proposées.</p>
      </div>

      <div class="feature-card">
        <h2>Réservations</h2>
        <p>Réservez facilement une activité depuis l’interface utilisateur.</p>
      </div>

      <div class="feature-card">
        <h2>Boutique</h2>
        <p>Découvrez des produits liés aux activités et aux loisirs.</p>
      </div>
    </section>
  </div>
</template>

<script>
export default {
  name: 'HomePage'
};
</script>

<style scoped src="./HomePage.css"></style>