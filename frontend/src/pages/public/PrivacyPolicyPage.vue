<template>
  <div class="privacy-page">
    <div class="privacy-card">
      <h1>{{ $t("public.privacy.title") }}</h1>

      <p class="subtitle">
        {{ $t("public.privacy.subtitle") }}
      </p>

      <section>
        <h2>{{ $t("public.privacy.controllerTitle") }}</h2>
        <p>{{ $t("public.privacy.controllerText") }}</p>
      </section>

      <section>
        <h2>{{ $t("public.privacy.dataTitle") }}</h2>

        <p>{{ $t("public.privacy.dataIntro") }}</p>

        <ul>
          <li>{{ $t("public.privacy.dataIdentity") }}</li>
          <li>{{ $t("public.privacy.dataContact") }}</li>
          <li>{{ $t("public.privacy.dataPassword") }}</li>
          <li>{{ $t("public.privacy.dataProfilePicture") }}</li>
          <li>{{ $t("public.privacy.dataHistory") }}</li>
          <li>{{ $t("public.privacy.dataPartners") }}</li>
        </ul>

        <p>
          {{ $t("public.privacy.paymentData") }}
        </p>
      </section>

      <section>
        <h2>{{ $t("public.privacy.purposesTitle") }}</h2>

        <ul>
          <li>{{ $t("public.privacy.purposeAccount") }}</li>
          <li>{{ $t("public.privacy.purposeReservations") }}</li>
          <li>{{ $t("public.privacy.purposePayments") }}</li>
          <li>{{ $t("public.privacy.purposeCommunication") }}</li>
        </ul>
      </section>

      <section>
        <h2>{{ $t("public.privacy.legalBasisTitle") }}</h2>

        <p>
          {{ $t("public.privacy.legalBasisText") }}
        </p>
      </section>

      <section>
        <h2>{{ $t("public.privacy.retentionTitle") }}</h2>
        <p>{{ $t("public.privacy.retentionText") }}</p>
      </section>

      <section>
        <h2>{{ $t("public.privacy.recipientsTitle") }}</h2>

        <p>
          {{ $t("public.privacy.recipientsText") }}
        </p>
      </section>

      <section>
        <h2>{{ $t("public.privacy.rightsTitle") }}</h2>

        <p>
          {{ $t("public.privacy.rightsText") }}
        </p>

        <p>
          {{ $t("public.privacy.rightsContact") }}
        </p>

        <p>
          {{ $t("public.privacy.complaint") }}
        </p>
      </section>

      <section>
        <h2>{{ $t("public.privacy.securityTitle") }}</h2>

        <p>
          {{ $t("public.privacy.securityText") }}
        </p>
      </section>

      <p class="last-update">
        {{ $t("public.privacy.lastUpdate") }}
      </p>
    </div>
  </div>
</template>

<script>
export default {
  name: "PrivacyPolicyPage"
};
</script>

<style scoped src="./PrivacyPolicyPage.css"></style>