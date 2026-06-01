<template>
  <section class="partner-addresses">
    <header class="partner-addresses-header">
      <div>
        <h2>Mes adresses</h2>
        <p>Gérez les adresses légales et de contact de votre entreprise.</p>
      </div>

      <button type="button" @click="$emit('add')">
        Ajouter une adresse
      </button>
    </header>

    <p v-if="loading">Chargement des adresses...</p>

    <div v-else class="partner-address-grid">
      <article
        v-for="address in addresses"
        :key="address.id"
        class="partner-address-card"
      >
        <strong>
          {{ address.addressType === "LEGAL" ? "Adresse légale" : "Adresse de contact" }}
        </strong>

        <p>
          {{ address.street }} {{ address.houseNumber }}
          <span v-if="address.box"> boîte {{ address.box }}</span>
        </p>

        <p>{{ address.postalCode }} {{ address.city }} — {{ address.country }}</p>

        <div class="partner-address-actions">
          <button type="button" @click="$emit('edit', address)">
            Modifier
          </button>

          <button
            type="button"
            class="partner-delete-button"
            :disabled="deletingAddressId === address.id"
            @click="$emit('delete', address.id)"
          >
            {{ deletingAddressId === address.id ? "Suppression..." : "Supprimer" }}
          </button>
        </div>
      </article>
    </div>
  </section>
</template>

<script>
import "./PartnerAddressList.css";

export default {
  name: "PartnerAddressList",

  props: {
    addresses: { type: Array, default: () => [] },
    loading: { type: Boolean, default: false },
    deletingAddressId: { type: Number, default: null }
  },

  emits: ["add", "edit", "delete"]
};
</script>