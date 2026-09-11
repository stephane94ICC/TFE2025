<template>
  <div class="profile-page">
    <section class="profile-card">
      <div class="profile-header">
        <h1>{{ $t("member.profile.title") }}</h1>
        <p>{{ $t("member.profile.subtitle") }}</p>
      </div>

      <div v-if="user" class="profile-content">
        <div class="profile-image-section">
          <img
              :src="profileImageUrl"
              :alt="$t('member.profile.profileImageAlt')"
              class="profile-image"
          />

          <div class="profile-image-actions">
            <input
                ref="fileInput"
                type="file"
                accept=".jpg,.jpeg,.png,.webp"
                @change="selectFile"
            />

            <button
                type="button"
                class="profile-image-button"
                :disabled="!selectedFile || uploading"
                @click="uploadImage"
            >
              {{ uploading ? $t("member.profile.uploading") : $t("member.profile.editPhoto") }}
            </button>
          </div>
        </div>

        <div v-if="successMessage" class="profile-success">
          {{ successMessage }}
        </div>

        <div v-if="errorMessage" class="profile-error">
          {{ errorMessage }}
        </div>

        <div class="profile-row">
          <label class="profile-label" for="firstName">
            {{ $t("member.profile.firstName") }}
          </label>
          <input
              id="firstName"
              v-model="form.firstName"
              type="text"
              maxlength="100"
              class="profile-input"
              :disabled="saving"
          />
        </div>

        <div class="profile-row">
          <label class="profile-label" for="lastName">
            {{ $t("member.profile.lastName") }}
          </label>
          <input
              id="lastName"
              v-model="form.lastName"
              type="text"
              maxlength="100"
              class="profile-input"
              :disabled="saving"
          />
        </div>

        <div class="profile-row">
          <label class="profile-label" for="phone">
            {{ $t("member.profile.phone") }}
          </label>
          <input
              id="phone"
              v-model="form.phone"
              type="tel"
              maxlength="20"
              :placeholder="$t('member.profile.optional')"
              class="profile-input"
              :disabled="saving"
          />
        </div>

        <div class="profile-row">
          <span class="profile-label">{{ $t("member.profile.email") }}</span>
          <span class="profile-value">{{ user.email }}</span>
        </div>

        <p class="profile-hint">
          {{ $t("member.profile.emailHint") }}
        </p>

        <div class="profile-row">
          <span class="profile-label">{{ $t("member.profile.roles") }}</span>

          <div class="profile-roles">
            <span
                v-for="role in user.roles"
                :key="role"
                class="profile-role"
            >
              {{ formatRole(role) }}
            </span>
          </div>
        </div>

        <div class="profile-actions">
          <button
              type="button"
              class="profile-save-button"
              :disabled="saving || !hasChanges"
              @click="saveProfile"
          >
            {{ saving ? $t("member.profile.saving") : $t("member.profile.save") }}
          </button>

          <button
              type="button"
              class="profile-reset-button"
              :disabled="saving || !hasChanges"
              @click="resetForm"
          >
            {{ $t("member.profile.cancel") }}
          </button>
        </div>

        <div v-if="canDeleteAccount" class="profile-danger-zone">
          <h2 class="profile-danger-title">
            {{ $t("member.profile.deleteAccountTitle") }}
          </h2>

          <p class="profile-danger-text">
            {{ $t("member.profile.deleteAccountText") }}
          </p>

          <button
              type="button"
              class="profile-delete-button"
              @click="openDeleteDialog"
          >
            {{ $t("member.profile.deleteAccount") }}
          </button>
        </div>
      </div>

      <div v-else class="profile-empty">
        <p>{{ $t("member.profile.noUser") }}</p>
      </div>
    </section>

    <div v-if="showDeleteDialog" class="profile-modal-overlay">
      <div class="profile-modal">
        <h2 class="profile-modal-title">
          {{ $t("member.profile.confirmDeleteTitle") }}
        </h2>

        <p class="profile-modal-text">
          {{ $t("member.profile.confirmDeleteText") }}
        </p>

        <div v-if="deleteErrorMessage" class="profile-error">
          {{ deleteErrorMessage }}
        </div>

        <input
            v-model="deletePassword"
            type="password"
            class="profile-modal-input"
            :placeholder="$t('member.profile.password')"
            autocomplete="current-password"
            :disabled="deleting"
            @keyup.enter="confirmDelete"
        />

        <div class="profile-modal-actions">
          <button
              type="button"
              class="profile-delete-button"
              :disabled="deleting || !deletePassword"
              @click="confirmDelete"
          >
            {{ deleting ? $t("member.profile.deleting") : $t("member.profile.deletePermanently") }}
          </button>

          <button
              type="button"
              class="profile-reset-button"
              :disabled="deleting"
              @click="closeDeleteDialog"
          >
            {{ $t("member.profile.cancel") }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import AuthService from "../../services/AuthService";
import {
  uploadProfileImage,
  getMemberProfile,
  updateMemberProfile,
  deleteMemberAccount
} from "../../services/UserService";
import "./ProfilePage.css";

export default {
  name: "ProfilePage",

  data() {
    return {
      user: null,
      form: {
        firstName: "",
        lastName: "",
        phone: ""
      },
      selectedFile: null,
      uploading: false,
      saving: false,
      successMessage: "",
      errorMessage: "",
      showDeleteDialog: false,
      deletePassword: "",
      deleting: false,
      deleteErrorMessage: ""
    };
  },

  computed: {
    profileImageUrl() {
      return this.user?.profileImageUrl
          || "/uploads/members/default-profile.png";
    },

    hasChanges() {
      if (!this.user) {
        return false;
      }

      return this.form.firstName !== (this.user.firstName || "")
          || this.form.lastName !== (this.user.lastName || "")
          || this.form.phone !== (this.user.phone || "");
    },

    canDeleteAccount() {
      const roles = this.user?.roles || [];

      return roles.length === 1 && roles[0] === "MEMBER";
    }
  },

  async mounted() {
    this.user = AuthService.getConnectedUser();

    if (!this.user) {
      return;
    }

    await this.loadProfile();
  },

  methods: {
    async loadProfile() {
      try {
        const response = await getMemberProfile();

        this.applyProfile(response.data);
      } catch (error) {
        console.error(error);
        this.errorMessage = error.response?.data?.error
            || this.$t("member.profile.loadError");
      }
    },

    applyProfile(profile) {
      this.user = { ...this.user, ...profile };
      AuthService.saveConnectedUser(this.user);
      this.resetForm();
    },

    resetForm() {
      this.form.firstName = this.user.firstName || "";
      this.form.lastName = this.user.lastName || "";
      this.form.phone = this.user.phone || "";
    },

    async saveProfile() {
      try {
        this.saving = true;
        this.successMessage = "";
        this.errorMessage = "";

        const response = await updateMemberProfile({
          firstName: this.form.firstName.trim(),
          lastName: this.form.lastName.trim(),
          phone: this.form.phone.trim()
        });

        this.applyProfile(response.data);
        this.successMessage = this.$t("member.profile.updateSuccess");
      } catch (error) {
        console.error(error);
        this.errorMessage = error.response?.data?.error
            || this.$t("member.profile.updateError");
      } finally {
        this.saving = false;
      }
    },

    openDeleteDialog() {
      this.deletePassword = "";
      this.deleteErrorMessage = "";
      this.showDeleteDialog = true;
    },

    closeDeleteDialog() {
      this.showDeleteDialog = false;
      this.deletePassword = "";
      this.deleteErrorMessage = "";
    },

    async confirmDelete() {
      if (!this.deletePassword || this.deleting) {
        return;
      }

      try {
        this.deleting = true;
        this.deleteErrorMessage = "";

        await deleteMemberAccount(this.deletePassword);

        AuthService.logout();
        this.$router.push("/");
      } catch (error) {
        console.error(error);
        this.deleteErrorMessage = error.response?.data?.error
            || this.$t("member.profile.deleteError");
      } finally {
        this.deleting = false;
      }
    },

    selectFile(event) {
      this.selectedFile = event.target.files[0] || null;
    },

    async uploadImage() {
      if (!this.selectedFile) {
        return;
      }

      try {
        this.uploading = true;
        this.successMessage = "";
        this.errorMessage = "";

        const response = await uploadProfileImage(this.selectedFile);

        this.user.profileImageUrl = response.data.profileImageUrl;
        AuthService.saveConnectedUser(this.user);

        this.selectedFile = null;
        this.$refs.fileInput.value = "";
        this.successMessage = this.$t("member.profile.photoSuccess");
      } catch (error) {
        console.error(error);
        this.errorMessage = error.response?.data?.error
            || this.$t("member.profile.photoError");
      } finally {
        this.uploading = false;
      }
    },

    formatRole(role) {
      if (role === "ADMIN") {
        return this.$t("member.profile.rolesLabels.ADMIN");
      }

      if (role === "PARTNER") {
        return this.$t("member.profile.rolesLabels.PARTNER");
      }

      if (role === "MEMBER") {
        return this.$t("member.profile.rolesLabels.MEMBER");
      }

      return role;
    }
  }
};
</script>