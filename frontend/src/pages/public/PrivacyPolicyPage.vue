<template>
  <div class="privacy-page">
    <div class="privacy-card">
      <h1>Politique de confidentialité</h1>
      <p class="subtitle">
        Cette page explique comment Bel'Loisirs collecte et traite vos données personnelles,
        conformément au Règlement Général sur la Protection des Données (RGPD).
      </p>

      <section>
        <h2>1. Responsable du traitement</h2>
        <p>[À COMPLÉTER : nom de l'entité responsable, adresse, email de contact]</p>
      </section>

      <section>
        <h2>2. Données collectées</h2>
        <p>Lors de la création d'un compte et de l'utilisation de la plateforme, les données suivantes sont collectées :</p>
        <ul>
          <li>Identité : nom, prénom, date de naissance</li>
          <li>Coordonnées : adresse e-mail, numéro de téléphone</li>
          <li>Mot de passe (stocké de façon chiffrée, jamais en clair)</li>
          <li>Photo de profil (facultative)</li>
          <li>Historique des commandes et réservations effectuées sur la plateforme</li>
          <li>Pour les partenaires : nom de l'entreprise, adresse(s) professionnelle(s), logo</li>
        </ul>
        <p>
          Les données de paiement (numéro de carte bancaire, etc.) ne sont jamais stockées par
          Bel'Loisirs : elles sont traitées directement par notre prestataire de paiement, Stripe.
        </p>
      </section>

      <section>
        <h2>3. Finalités du traitement</h2>
        <ul>
          <li>Gestion du compte utilisateur et authentification</li>
          <li>Réservation d'activités et achat de produits</li>
          <li>Traitement des paiements (via Stripe)</li>
          <li>Communication liée aux commandes et réservations</li>
        </ul>
      </section>

      <section>
        <h2>4. Base légale</h2>
        <p>
          Le traitement repose sur l'exécution du contrat (création de compte, achats, réservations)
          et sur votre consentement explicite, recueilli lors de l'inscription.
        </p>
      </section>

      <section>
        <h2>5. Durée de conservation</h2>
        <p>[À COMPLÉTER : durée de conservation des données après suppression du compte / dernière activité]</p>
      </section>

      <section>
        <h2>6. Destinataires des données</h2>
        <p>
          Vos données ne sont partagées qu'avec les prestataires nécessaires au fonctionnement de la
          plateforme : Stripe (paiement) et notre hébergeur.
          [À COMPLÉTER : nom de l'hébergeur une fois le déploiement final confirmé]
        </p>
      </section>

      <section>
        <h2>7. Vos droits</h2>
        <p>
          Conformément au RGPD, vous disposez d'un droit d'accès, de rectification, d'effacement,
          de limitation et de portabilité de vos données, ainsi que du droit de retirer votre
          consentement à tout moment.
        </p>
        <p>
          Pour exercer ces droits, contactez-nous à l'adresse suivante :
          [À COMPLÉTER : adresse email de contact].
        </p>
        <p>
          Vous disposez également du droit d'introduire une réclamation auprès de l'Autorité de
          protection des données (APD) en Belgique.
        </p>
      </section>

      <section>
        <h2>8. Sécurité</h2>
        <p>
          Les mots de passe sont chiffrés (BCrypt) et l'authentification repose sur des jetons
          JWT à durée limitée. Aucune donnée de carte bancaire ne transite par nos serveurs.
        </p>
      </section>

      <p class="last-update">Dernière mise à jour : [À COMPLÉTER]</p>
    </div>
  </div>
</template>

<script>
export default {
  name: "PrivacyPolicyPage"
};
</script>

<style scoped src="./PrivacyPolicyPage.css"></style>