<template>
  <div>
    <h1>Liste des Users</h1>
    <ul v-if="users.length">
      <li v-for="user in users" :key="user.id">
        {{ user.id }} - {{ user.username }} - {{ user.email }}
      </li>
    </ul>
    <p v-else>Chargement...</p>
  </div>
</template>

<script>
import { getUsers } from '../services/UserService';

export default {
  name: 'UserList',
  data() {
    return {
      users: []
    }
  },
  mounted() {
    getUsers()
        .then(response => {
          this.users = response.data;
        })
        .catch(err => console.error('Erreur API:', err));
  }
}
</script>
