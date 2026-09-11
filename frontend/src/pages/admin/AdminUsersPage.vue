<template>
  <div class="page-container">
    <div class="header">
      <div>
        <h1>{{ $t("admin.users.title") }}</h1>
        <p>{{ $t("admin.users.subtitle") }}</p>
      </div>

      <button class="btn btn-primary" @click="toggleCreateForm">
        {{ showCreateForm ? $t("admin.users.cancel") : $t("admin.users.addUser") }}
      </button>
    </div>

    <div v-if="errorMessage" class="alert alert-error">
      {{ errorMessage }}
    </div>

    <div v-if="successMessage" class="alert alert-success">
      {{ successMessage }}
    </div>

    <!-- Formulaire de création -->
    <div v-if="showCreateForm" class="card">
      <h2>{{ $t("admin.users.createTitle") }}</h2>

      <div class="form-grid">
        <div class="form-group">
          <label>{{ $t("admin.users.email") }}</label>
          <input
              v-model="newUser.email"
              type="email"
              :placeholder="$t('admin.users.emailPlaceholder')"
          >
        </div>

        <div class="form-group">
          <label>{{ $t("admin.users.firstName") }}</label>
          <input
              v-model="newUser.firstName"
              type="text"
              :placeholder="$t('admin.users.firstNamePlaceholder')"
          >
        </div>

        <div class="form-group">
          <label>{{ $t("admin.users.lastName") }}</label>
          <input
              v-model="newUser.lastName"
              type="text"
              :placeholder="$t('admin.users.lastNamePlaceholder')"
          >
        </div>

        <div class="form-group">
          <label>{{ $t("admin.users.password") }}</label>
          <input
              v-model="newUser.password"
              type="password"
              :placeholder="$t('admin.users.passwordPlaceholder')"
          >
        </div>

        <div class="form-group">
          <label>{{ $t("admin.users.role") }}</label>
          <select v-model="newUser.role">
            <option value="MEMBER">{{ $t("admin.users.roles.MEMBER") }}</option>
            <option value="PARTNER">{{ $t("admin.users.roles.PARTNER") }}</option>
            <option value="ADMIN">{{ $t("admin.users.roles.ADMIN") }}</option>
          </select>
        </div>
      </div>

      <p class="info-text">
        {{ $t("admin.users.rgpdInfo") }}
      </p>

      <div class="actions">
        <button class="btn btn-primary" @click="createUser">
          {{ $t("admin.users.create") }}
        </button>

        <button class="btn btn-secondary" @click="cancelCreate">
          {{ $t("admin.users.cancel") }}
        </button>
      </div>
    </div>

    <!-- Formulaire de modification -->
    <div v-if="editingUser" class="card">
      <h2>{{ $t("admin.users.editTitle") }}</h2>

      <div class="form-grid">
        <div class="form-group">
          <label>{{ $t("admin.users.email") }}</label>
          <input v-model="editForm.email" type="email">
        </div>

        <div class="form-group">
          <label>{{ $t("admin.users.firstName") }}</label>
          <input v-model="editForm.firstName" type="text">
        </div>

        <div class="form-group">
          <label>{{ $t("admin.users.lastName") }}</label>
          <input v-model="editForm.lastName" type="text">
        </div>

        <div class="form-group">
          <label>{{ $t("admin.users.newPassword") }}</label>
          <input
              v-model="editForm.password"
              type="password"
              :placeholder="$t('admin.users.unchangedPasswordPlaceholder')"
          >
        </div>

        <div class="form-group">
          <label>{{ $t("admin.users.role") }}</label>
          <select v-model="editForm.role">
            <option value="MEMBER">{{ $t("admin.users.roles.MEMBER") }}</option>
            <option value="PARTNER">{{ $t("admin.users.roles.PARTNER") }}</option>
            <option value="ADMIN">{{ $t("admin.users.roles.ADMIN") }}</option>
          </select>
        </div>
      </div>

      <div class="actions">
        <button class="btn btn-primary" @click="updateUser">
          {{ $t("admin.users.save") }}
        </button>

        <button class="btn btn-secondary" @click="cancelEdit">
          {{ $t("admin.users.cancel") }}
        </button>
      </div>
    </div>

    <!-- Liste des utilisateurs -->
    <div class="card">
      <h2>{{ $t("admin.users.listTitle") }}</h2>

      <p v-if="loading">{{ $t("admin.users.loading") }}</p>

      <table v-else-if="users.length" class="user-table">
        <thead>
        <tr>
          <th>{{ $t("admin.users.id") }}</th>
          <th>{{ $t("admin.users.email") }}</th>
          <th>{{ $t("admin.users.firstName") }}</th>
          <th>{{ $t("admin.users.lastName") }}</th>
          <th>{{ $t("admin.users.role") }}</th>
          <th>{{ $t("admin.users.rgpd") }}</th>
          <th>{{ $t("admin.users.actions") }}</th>
        </tr>
        </thead>

        <tbody>
        <tr v-for="user in users" :key="user.id">
          <td>{{ user.id }}</td>
          <td>{{ user.email }}</td>
          <td>{{ user.firstName }}</td>
          <td>{{ user.lastName }}</td>

          <td>
            <span class="badge badge-role">
              {{ formatRole(user.role) }}
            </span>
          </td>

          <td>
            <span :class="user.consentRgpd ? 'badge badge-success' : 'badge badge-danger'">
              {{ user.consentRgpd ? $t("admin.users.yes") : $t("admin.users.no") }}
            </span>
          </td>

          <td class="table-actions">
            <button class="btn btn-small btn-secondary" @click="startEdit(user)">
              {{ $t("admin.users.edit") }}
            </button>

            <button class="btn btn-small btn-danger" @click="deleteUser(user.id)">
              {{ $t("admin.users.delete") }}
            </button>
          </td>
        </tr>
        </tbody>
      </table>

      <p v-else>{{ $t("admin.users.empty") }}</p>
    </div>
  </div>
</template>

<script>
import {
  getUsers,
  createUser as apiCreateUser,
  updateUser as apiUpdateUser,
  deleteUser as apiDeleteUser
} from '../../services/UserService';

export default {
  name: 'AdminUsersPage',

  data() {
    return {
      users: [],
      loading: false,
      showCreateForm: false,
      editingUser: null,
      errorMessage: '',
      successMessage: '',

      newUser: {
        email: '',
        firstName: '',
        lastName: '',
        password: '',
        role: 'MEMBER'
      },

      editForm: {
        email: '',
        firstName: '',
        lastName: '',
        password: '',
        role: 'MEMBER'
      }
    };
  },

  mounted() {
    this.loadUsers();
  },

  methods: {
    loadUsers() {
      this.loading = true;
      this.errorMessage = '';

      getUsers()
          .then(res => {
            this.users = res.data;
          })
          .catch(err => {
            console.error(err);
            this.errorMessage = this.$t("admin.users.loadError");
          })
          .finally(() => {
            this.loading = false;
          });
    },

    toggleCreateForm() {
      this.showCreateForm = !this.showCreateForm;
      this.editingUser = null;
      this.clearMessages();
    },

    createUser() {
      this.clearMessages();

      if (!this.newUser.email || !this.newUser.firstName || !this.newUser.lastName || !this.newUser.password) {
        this.errorMessage = this.$t("admin.users.requiredCreate");
        return;
      }

      apiCreateUser(this.newUser)
          .then(() => {
            this.successMessage = this.$t("admin.users.createSuccess");
            this.cancelCreate();
            this.loadUsers();
          })
          .catch(err => {
            console.error(err);
            this.errorMessage = this.$t("admin.users.createError");
          });
    },

    cancelCreate() {
      this.newUser = {
        email: '',
        firstName: '',
        lastName: '',
        password: '',
        role: 'MEMBER'
      };

      this.showCreateForm = false;
    },

    startEdit(user) {
      this.clearMessages();
      this.showCreateForm = false;
      this.editingUser = user;

      this.editForm = {
        email: user.email,
        firstName: user.firstName,
        lastName: user.lastName,
        password: '',
        role: user.role || 'MEMBER'
      };
    },

    updateUser() {
      this.clearMessages();

      if (!this.editForm.email || !this.editForm.firstName || !this.editForm.lastName) {
        this.errorMessage = this.$t("admin.users.requiredEdit");
        return;
      }

      const payload = {
        email: this.editForm.email,
        firstName: this.editForm.firstName,
        lastName: this.editForm.lastName,
        role: this.editForm.role
      };

      if (this.editForm.password && this.editForm.password.trim() !== '') {
        payload.password = this.editForm.password;
      }

      apiUpdateUser(this.editingUser.id, payload)
          .then(() => {
            this.successMessage = this.$t("admin.users.updateSuccess");
            this.cancelEdit();
            this.loadUsers();
          })
          .catch(err => {
            console.error(err);
            this.errorMessage = this.$t("admin.users.updateError");
          });
    },

    cancelEdit() {
      this.editingUser = null;

      this.editForm = {
        email: '',
        firstName: '',
        lastName: '',
        password: '',
        role: 'MEMBER'
      };
    },

    deleteUser(id) {
      this.clearMessages();

      if (confirm(this.$t("admin.users.confirmDelete"))) {
        apiDeleteUser(id)
            .then(() => {
              this.successMessage = this.$t("admin.users.deleteSuccess");
              this.loadUsers();
            })
            .catch(err => {
              console.error(err);
              this.errorMessage = this.$t("admin.users.deleteError");
            });
      }
    },

    formatRole(role) {
      if (role === 'ADMIN') return this.$t("admin.users.roles.ADMIN");
      if (role === 'PARTNER') return this.$t("admin.users.roles.PARTNER");
      return this.$t("admin.users.roles.MEMBER");
    },

    clearMessages() {
      this.errorMessage = '';
      this.successMessage = '';
    }
  }
};
</script>

<style scoped src="./AdminUsersPage.css"></style>