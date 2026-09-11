<template>
  <div class="partner-page">
    <header class="partner-page-header">
      <h1>{{ $t("partner.page.title") }}</h1>
      <p>{{ $t("partner.page.subtitle") }}</p>
    </header>

    <p v-if="successMessage" class="partner-success">{{ successMessage }}</p>
    <p v-if="errorMessage" class="partner-error">{{ errorMessage }}</p>

    <p v-if="loading">{{ $t("partner.page.loading") }}</p>

    <template v-else-if="partner">
      <section class="partner-section-card">
        <h2>{{ $t("partner.page.logoTitle") }}</h2>

        <PartnerLogoForm
          :logo-url="logoUrl"
          :uploading="uploading"
          @upload="uploadLogo"
        />
      </section>

      <section class="partner-section-card">
        <PartnerCompanyForm
          :partner="partner"
          :saving="savingProfile"
          @save="saveProfile"
        />
      </section>

      <section class="partner-section-card">
        <PartnerAddressList
          :addresses="addresses"
          :loading="loadingAddresses"
          :deleting-address-id="deletingAddressId"
          @add="openAddAddress"
          @edit="openEditAddress"
          @delete="removeAddress"
        />
      </section>

      <PartnerAddressForm
        :visible="showAddressModal"
        :address-to-edit="addressToEdit"
        :saving="savingAddress"
        @save="saveAddress"
        @cancel="closeAddressModal"
      />
    </template>

    <p v-else class="partner-error">
      {{ $t("partner.page.companyLoadError") }}
    </p>
  </div>
</template>

<script>
import {
  addPartnerAddress,
  deletePartnerAddress,
  getPartnerAddresses,
  getPartnerProfile,
  updatePartnerAddress,
  updatePartnerProfile,
  uploadPartnerLogo
} from "../../services/PartnerService";

import PartnerAddressForm from "./PartnerAddressForm.vue";
import PartnerAddressList from "./PartnerAddressList.vue";
import PartnerCompanyForm from "./PartnerCompanyForm.vue";
import PartnerLogoForm from "./PartnerLogoForm.vue";

import "./PartnerPage.css";

export default {
  name: "PartnerPage",

  components: {
    PartnerAddressForm,
    PartnerAddressList,
    PartnerCompanyForm,
    PartnerLogoForm
  },

  data() {
    return {
      partner: null,
      addresses: [],
      addressToEdit: null,
      showAddressModal: false,
      deletingAddressId: null,
      loading: true,
      loadingAddresses: true,
      savingProfile: false,
      savingAddress: false,
      uploading: false,
      successMessage: "",
      errorMessage: ""
    };
  },

  computed: {
    logoUrl() {
      return this.partner?.logoUrl || "/uploads/partners/default-logo.png";
    }
  },

  mounted() {
    this.loadProfile();
    this.loadAddresses();
  },

  methods: {
    async loadProfile() {
      try {
        const response = await getPartnerProfile();
        this.partner = response.data;
      } catch (error) {
        console.error(error);
        this.errorMessage = this.$t("partner.page.loadError");
      } finally {
        this.loading = false;
      }
    },

    async loadAddresses() {
      try {
        this.loadingAddresses = true;
        const response = await getPartnerAddresses();
        this.addresses = response.data;
      } catch (error) {
        console.error(error);
        this.errorMessage = this.$t("partner.page.addressesLoadError");
      } finally {
        this.loadingAddresses = false;
      }
    },

    async saveProfile(form) {
      try {
        this.savingProfile = true;
        this.errorMessage = "";

        const response = await updatePartnerProfile(form);
        this.partner = response.data;
        this.successMessage = this.$t("partner.page.profileUpdateSuccess");
      } catch (error) {
        console.error(error);
        this.errorMessage = this.$t("partner.page.profileUpdateError");
      } finally {
        this.savingProfile = false;
      }
    },

    async uploadLogo(file) {
      try {
        this.uploading = true;
        this.errorMessage = "";

        const response = await uploadPartnerLogo(file);
        this.partner = response.data;
        this.successMessage = this.$t("partner.page.logoUpdateSuccess");
      } catch (error) {
        console.error(error);
        this.errorMessage = this.$t("partner.page.logoUpdateError");
      } finally {
        this.uploading = false;
      }
    },

    openAddAddress() {
      this.addressToEdit = null;
      this.showAddressModal = true;
    },

    openEditAddress(address) {
      this.addressToEdit = address;
      this.showAddressModal = true;
    },

    closeAddressModal() {
      this.addressToEdit = null;
      this.showAddressModal = false;
    },

    async saveAddress(form) {
      try {
        this.savingAddress = true;
        this.errorMessage = "";

        if (this.addressToEdit) {
          await updatePartnerAddress(this.addressToEdit.id, form);
          this.successMessage = this.$t("partner.page.addressUpdateSuccess");
        } else {
          await addPartnerAddress(form);
          this.successMessage = this.$t("partner.page.addressAddSuccess");
        }

        this.closeAddressModal();
        await this.loadAddresses();
      } catch (error) {
        console.error(error);
        this.errorMessage = this.$t("partner.page.addressSaveError");
      } finally {
        this.savingAddress = false;
      }
    },

    async removeAddress(addressId) {
      if (!window.confirm(this.$t("partner.page.confirmAddressDelete"))) return;

      try {
        this.deletingAddressId = addressId;
        this.errorMessage = "";

        await deletePartnerAddress(addressId);
        this.successMessage = this.$t("partner.page.addressDeleteSuccess");
        await this.loadAddresses();
      } catch (error) {
        console.error(error);
        this.errorMessage = this.$t("partner.page.addressDeleteError");
      } finally {
        this.deletingAddressId = null;
      }
    }
  }
};
</script>