<template>
  <section class="partner-addresses">
    <header class="partner-addresses-header">
      <div>
        <h2>{{ $t("partner.addressList.title") }}</h2>
        <p>{{ $t("partner.addressList.subtitle") }}</p>
      </div>

      <button type="button" @click="$emit('add')">
        {{ $t("partner.addressList.add") }}
      </button>
    </header>

    <p v-if="loading">{{ $t("partner.addressList.loading") }}</p>

    <div v-else class="partner-address-grid">
      <article
        v-for="address in addresses"
        :key="address.id"
        class="partner-address-card"
      >
        <strong>
          {{
            address.addressType === "LEGAL"
              ? $t("partner.addressList.legalAddress")
              : $t("partner.addressList.contactAddress")
          }}
        </strong>

        <p>
          {{ address.street }} {{ address.houseNumber }}
          <span v-if="address.box">
            {{ $t("partner.addressList.box") }} {{ address.box }}
          </span>
        </p>

        <p>{{ address.postalCode }} {{ address.city }} — {{ address.country }}</p>

        <div class="partner-address-actions">
          <button type="button" @click="$emit('edit', address)">
            {{ $t("partner.addressList.edit") }}
          </button>

          <button
            type="button"
            class="partner-delete-button"
            :disabled="deletingAddressId === address.id"
            @click="$emit('delete', address.id)"
          >
            {{
              deletingAddressId === address.id
                ? $t("partner.addressList.deleting")
                : $t("partner.addressList.delete")
            }}
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