<template>
  <div class="terms-page">
    <div class="terms-card">
      <h1>{{ $t("public.terms.title") }}</h1>

      <p class="subtitle">
        {{ $t("public.terms.subtitle") }}
      </p>

      <section>
        <h2>{{ $t("public.terms.identityTitle") }}</h2>
        <p>{{ $t("public.terms.identityText") }}</p>
      </section>

      <!-- Deux régimes : le partenaire vend l'activité, la plateforme vend les articles -->
      <section>
        <h2>{{ $t("public.terms.rolesTitle") }}</h2>
        <ul>
          <li>{{ $t("public.terms.rolesActivities") }}</li>
          <li>{{ $t("public.terms.rolesProducts") }}</li>
        </ul>
      </section>

      <section>
        <h2>{{ $t("public.terms.pricesTitle") }}</h2>
        <p>{{ $t("public.terms.pricesText") }}</p>
      </section>

      <section>
        <h2>{{ $t("public.terms.paymentTitle") }}</h2>
        <p>{{ $t("public.terms.paymentText") }}</p>
      </section>

      <section>
        <h2>{{ $t("public.terms.reservationsTitle") }}</h2>
        <ul>
          <li>{{ $t("public.terms.reservationsWithdrawal") }}</li>
          <li>{{ $t("public.terms.reservationsCancellation") }}</li>
          <li>{{ $t("public.terms.reservationsAfterDeadline") }}</li>
        </ul>
      </section>

      <section>
        <h2>{{ $t("public.terms.productsTitle") }}</h2>
        <ul>
          <li>{{ $t("public.terms.productsWithdrawal") }}</li>
          <li>{{ $t("public.terms.productsHow") }}</li>
          <li>{{ $t("public.terms.productsRefund") }}</li>
        </ul>
      </section>

      <section>
        <h2>{{ $t("public.terms.contactTitle") }}</h2>
        <p>{{ $t("public.terms.contactText") }}</p>
      </section>

      <p class="last-update">
        {{ $t("public.terms.lastUpdate") }}
      </p>
    </div>
  </div>
</template>

<script>
export default {
  name: "TermsPage"
};
</script>

<style scoped src="./TermsPage.css"></style>