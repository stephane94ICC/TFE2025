<template>
  <div class="page-container">
    <div class="header">
      <div>
        <h1>Gestion des utilisateurs</h1>
        <p>Liste des utilisateurs enregistrés sur la plateforme.</p>
      </div>

      <button class="btn btn-primary" @click="toggleCreateForm">
        {{ showCreateForm ? 'Annuler' : 'Ajouter un utilisateur' }}
      </button>
    </div>

    <!-- Message d'erreur -->
    <div v-if="errorMessage" class="alert alert-error">
      {{ errorMessage }}
    </div>

    <!-- Message de succès -->
    <div v-if="successMessage" class="alert alert-success">
      {{ successMessage }}
    </div>

    <!-- Formulaire de création -->
    <div v-if="showCreateForm" class="card">
      <h2>Créer un utilisateur</h2>

      <div class="form-grid">
        <div class="form-group">
          <label>Email</label>
          <input v-model="newUser.email" type="email" placeholder="exemple@email.com">
        </div>

        <div class="form-group">
          <label>Prénom</label>
          <input v-model="newUser.firstName" type="text" placeholder="Prénom">
        </div>

        <div class="form-group">
          <label>Nom</label>
          <input v-model="newUser.lastName" type="text" placeholder="Nom">
        </div>

        <div class="form-group">
          <label>Mot de passe</label>
          <input v-model="newUser.password" type="password" placeholder="Mot de passe">
        </div>
      </div>

      <label class="checkbox-line">
        <input type="checkbox" v-model="newUser.consentRgpd">
        J’accepte le traitement de mes données personnelles.
      </label>

      <div class="actions">
        <button class="btn btn-primary" @click="createUser">
          Créer
        </button>

        <button class="btn btn-secondary" @click="cancelCreate">
          Annuler
        </button>
      </div>
    </div>

    <!-- Formulaire de modification -->
    <div v-if="editingUser" class="card">
      <h2>Modifier l’utilisateur</h2>

      <div class="form-grid">
        <div class="form-group">
          <label>Email</label>
          <input v-model="editForm.email" type="email">
        </div>

        <div class="form-group">
          <label>Prénom</label>
          <input v-model="editForm.firstName" type="text">
        </div>

        <div class="form-group">
          <label>Nom</label>
          <input v-model="editForm.lastName" type="text">
        </div>

        <div class="form-group">
          <label>Nouveau mot de passe</label>
          <input v-model="editForm.password" type="password" placeholder="Laisser vide si inchangé">
        </div>
      </div>

      <div class="actions">
        <button class="btn btn-primary" @click="updateUser">
          Enregistrer
        </button>

        <button class="btn btn-secondary" @click="cancelEdit">
          Annuler
        </button>
      </div>
    </div>

    <!-- Liste des utilisateurs -->
    <div class="card">
      <h2>Liste des utilisateurs</h2>

      <p v-if="loading">Chargement des utilisateurs...</p>

      <table v-else-if="users.length" class="user-table">
        <thead>
        <tr>
          <th>ID</th>
          <th>Email</th>
          <th>Prénom</th>
          <th>Nom</th>
          <th>RGPD</th>
          <th>Actions</th>
        </tr>
        </thead>

        <tbody>
        <tr v-for="user in users" :key="user.id">
          <td>{{ user.id }}</td>
          <td>{{ user.email }}</td>
          <td>{{ user.firstName }}</td>
          <td>{{ user.lastName }}</td>
          <td>
              <span :class="user.consentRgpd ? 'badge badge-success' : 'badge badge-danger'">
                {{ user.consentRgpd ? 'Oui' : 'Non' }}
              </span>
          </td>
          <td class="table-actions">
            <button class="btn btn-small btn-secondary" @click="startEdit(user)">
              Modifier
            </button>

            <button class="btn btn-small btn-danger" @click="deleteUser(user.id)">
              Supprimer
            </button>
          </td>
        </tr>
        </tbody>
      </table>

      <p v-else>Aucun utilisateur trouvé.</p>
    </div>
  </div>
</template>

<script>
import {
  getUsers,
  createUser as apiCreateUser,
  updateUser as apiUpdateUser,
  deleteUser as apiDeleteUser
} from '../services/UserService';

export default {
  name: 'UserList',

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
        consentRgpd: false
      },

      editForm: {
        email: '',
        firstName: '',
        lastName: '',
        password: ''
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
            this.errorMessage = "Impossible de charger les utilisateurs.";
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
        this.errorMessage = "Veuillez remplir tous les champs.";
        return;
      }

      if (!this.newUser.consentRgpd) {
        this.errorMessage = "Vous devez accepter le traitement des données personnelles.";
        return;
      }

      apiCreateUser(this.newUser)
          .then(() => {
            this.successMessage = "Utilisateur créé avec succès.";
            this.cancelCreate();
            this.loadUsers();
          })
          .catch(err => {
            console.error(err);
            this.errorMessage = "Erreur lors de la création de l’utilisateur.";
          });
    },

    cancelCreate() {
      this.newUser = {
        email: '',
        firstName: '',
        lastName: '',
        password: '',
        consentRgpd: false
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
        password: ''
      };
    },

    updateUser() {
      this.clearMessages();

      if (!this.editForm.email || !this.editForm.firstName || !this.editForm.lastName) {
        this.errorMessage = "Veuillez remplir l’email, le prénom et le nom.";
        return;
      }

      const payload = {
        email: this.editForm.email,
        firstName: this.editForm.firstName,
        lastName: this.editForm.lastName
      };

      if (this.editForm.password && this.editForm.password.trim() !== '') {
        payload.password = this.editForm.password;
      }

      apiUpdateUser(this.editingUser.id, payload)
          .then(() => {
            this.successMessage = "Utilisateur modifié avec succès.";
            this.cancelEdit();
            this.loadUsers();
          })
          .catch(err => {
            console.error(err);
            this.errorMessage = "Erreur lors de la modification de l’utilisateur.";
          });
    },

    cancelEdit() {
      this.editingUser = null;

      this.editForm = {
        email: '',
        firstName: '',
        lastName: '',
        password: ''
      };
    },

    deleteUser(id) {
      this.clearMessages();

      if (confirm("Voulez-vous vraiment supprimer cet utilisateur ?")) {
        apiDeleteUser(id)
            .then(() => {
              this.successMessage = "Utilisateur supprimé avec succès.";
              this.loadUsers();
            })
            .catch(err => {
              console.error(err);
              this.errorMessage = "Erreur lors de la suppression de l’utilisateur.";
            });
      }
    },

    clearMessages() {
      this.errorMessage = '';
      this.successMessage = '';
    }
  }
};
</script>

<style scoped src="./UserList.css"></style>