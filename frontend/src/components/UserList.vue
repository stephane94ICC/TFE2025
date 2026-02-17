<template>
  <div>
    <h1>Liste des Users</h1>

    <button @click="showCreateForm = !showCreateForm">
      {{ showCreateForm ? 'Annuler' : 'Ajouter un utilisateur' }}
    </button>

    <!-- Formulaire de création -->
    <div v-if="showCreateForm">
      <input v-model="newUser.email" placeholder="Email">
      <input v-model="newUser.firstName" placeholder="Prénom">
      <input v-model="newUser.lastName" placeholder="Nom">
      <input v-model="newUser.password" placeholder="Mot de passe" type="password">
      <label>
        <input type="checkbox" v-model="newUser.consentRgpd">
        J'accepte la RGPD
      </label>
      <button @click="createUser()">Créer</button>
    </div>

    <!-- Liste des users -->
    <ul v-if="users.length">
      <li v-for="user in users" :key="user.id">
        {{ user.id }} - {{ user.email }} - {{ user.firstName }} {{ user.lastName }}
        <button @click="editUser(user)">Modifier</button>
        <button @click="deleteUser(user.id)">Supprimer</button>
      </li>
    </ul>
    <p v-else>Chargement...</p>
  </div>
</template>

<script>
import { getUsers, createUser as apiCreateUser, updateUser as apiUpdateUser, deleteUser as apiDeleteUser } from '../services/UserService';

export default {
  name: 'UserList',
  data() {
    return {
      users: [],
      showCreateForm: false,
      newUser: { email: '', firstName: '', lastName: '', password: '', consentRgpd: false },
    }
  },
  mounted() {
    this.loadUsers();
  },
  methods: {
    loadUsers() {
      getUsers()
        .then(res => { this.users = res.data; })
        .catch(err => console.error(err));
    },
    createUser() {
      if (!this.newUser.consentRgpd) {
        alert("Vous devez accepter la RGPD pour créer un compte.");
        return;
      }
      apiCreateUser(this.newUser)
        .then(() => {
          this.newUser = { email: '', firstName: '', lastName: '', password: '', consentRgpd: false };
          this.showCreateForm = false;
          this.loadUsers();
        })
        .catch(err => console.error(err));
    },
    editUser(user) {
      const updatedEmail = prompt("Email", user.email);
      const updatedFirstName = prompt("Prénom", user.firstName);
      const updatedLastName = prompt("Nom", user.lastName);
      const updatedPassword = prompt("Nouveau mot de passe (laisser vide pour ne pas changer)");

      const payload = {
        email: updatedEmail,
        firstName: updatedFirstName,
        lastName: updatedLastName
      };

      // On ne met le password que s'il est fourni
      if (updatedPassword && updatedPassword.trim() !== '') {
        payload.password = updatedPassword;
      }

      apiUpdateUser(user.id, payload)
        .then(() => this.loadUsers())
        .catch(err => console.error(err));
    },
    deleteUser(id) {
      if (confirm("Voulez-vous vraiment supprimer cet utilisateur ?")) {
        apiDeleteUser(id)
          .then(() => this.loadUsers())
          .catch(err => console.error(err));
      }
    }
  }
}
</script>