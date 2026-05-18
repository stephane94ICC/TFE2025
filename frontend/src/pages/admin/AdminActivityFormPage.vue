<template>
  <div class="admin-activity-form-page">
    <div class="form-card">
      <h1>{{ isEditMode ? "Modifier une activité" : "Ajouter une activité" }}</h1>

      <div v-if="errorMessage" class="error-message">
        {{ errorMessage }}
      </div>

      <form @submit.prevent="saveActivity">
        <div class="form-group">
          <label for="title">Titre</label>
          <input
            id="title"
            v-model="form.title"
            type="text"
            required
          />
        </div>

        <div class="form-group">
          <label for="description">Description</label>
          <textarea
            id="description"
            v-model="form.description"
            rows="4"
          ></textarea>
        </div>

        <div class="form-group">
          <label for="price">Prix</label>
          <input
            id="price"
            v-model.number="form.price"
            type="number"
            step="0.01"
            min="0"
          />
        </div>

        <div class="form-group">
          <label for="city">Ville</label>
          <input
            id="city"
            v-model="form.city"
            type="text"
          />
        </div>

        <div class="form-group">
          <label for="partnerId">ID partenaire</label>
          <input
            id="partnerId"
            v-model.number="form.partnerId"
            type="number"
            min="1"
            required
          />
        </div>

        <div class="form-actions">
          <router-link to="/admin/activities" class="btn-cancel">
            Annuler
          </router-link>

          <button type="submit" class="btn-save">
            Enregistrer
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import {
  getAdminActivityById,
  createActivity,
  updateActivity
} from "@/services/ActivityService";

export default {
  name: "AdminActivityFormPage",

  data() {
    return {
      form: {
        title: "",
        description: "",
        price: null,
        city: "",
        partnerId: null
      },
      errorMessage: ""
    };
  },

  computed: {
    isEditMode() {
      return !!this.$route.params.id;
    }
  },

  mounted() {
    if (this.isEditMode) {
      this.loadActivity();
    }
  },

  methods: {
    async loadActivity() {
      try {
        const response = await getAdminActivityById(this.$route.params.id);
        this.form = {
          title: response.data.title || "",
          description: response.data.description || "",
          price: response.data.price ?? null,
          city: response.data.city || "",
          partnerId: response.data.partnerId ?? null
        };
      } catch (error) {
        console.error(error);
        this.errorMessage = "Impossible de charger l’activité.";
      }
    },

    async saveActivity() {
      try {
        this.errorMessage = "";

        if (this.isEditMode) {
          await updateActivity(this.$route.params.id, this.form);
        } else {
          await createActivity(this.form);
        }

        this.$router.push("/admin/activities");
      } catch (error) {
        console.error(error);
        this.errorMessage = "Impossible d’enregistrer l’activité.";
      }
    }
  }
};
</script>

<style scoped src="./AdminActivityFormPage.css"></style>