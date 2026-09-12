<template>
  <nav class="navbar">
    <div class="navbar-brand">
      <router-link to="/" class="brand-link">
        {{ $t("navbar.brand") }}
      </router-link>
    </div>

    <NavbarSearch />

    <div class="navbar-right">
      <div class="navbar-links">
        <router-link to="/" class="nav-link">
          {{ $t("navbar.home") }}
        </router-link>

        <router-link to="/activities" class="nav-link">
          {{ $t("navbar.activities") }}
        </router-link>

        <router-link to="/shop" class="nav-link">
          {{ $t("navbar.shop") }}
        </router-link>

        <router-link
            v-if="isLoggedIn"
            to="/cart"
            class="nav-link"
        >
          {{ $t("navbar.cart") }}
        </router-link>

        <router-link
            v-if="isLoggedIn"
            to="/reservations"
            class="nav-link"
        >
          {{ $t("navbar.reservations") }}
        </router-link>

        <router-link
            v-if="isPartner"
            to="/partner"
            class="nav-link"
        >
          {{ $t("navbar.partnerSpace") }}
        </router-link>

        <router-link
            v-if="isPartner"
            to="/partner/activities"
            class="nav-link"
        >
          {{ $t("navbar.myActivities") }}
        </router-link>

        <router-link
            v-if="isPartner"
            to="/partner/locations"
            class="nav-link"
        >
          {{ $t("navbar.myLocations") }}
        </router-link>

        <router-link
            v-if="isAdmin"
            to="/admin"
            class="nav-link"
        >
          {{ $t("navbar.admin") }}
        </router-link>

        <router-link
            v-if="isAdmin"
            to="/admin/activities"
            class="nav-link"
        >
          {{ $t("navbar.adminActivities") }}
        </router-link>
      </div>

      <div class="menu-wrapper" @click.stop>
        <button
            class="menu-button"
            type="button"
            aria-label="Menu"
            aria-controls="account-menu"
            :aria-expanded="isMenuOpen"
            @click="toggleMenu"
        >
          ☰
        </button>

        <div
            v-if="isMenuOpen"
            id="account-menu"
            class="account-menu"
        >
          <div class="account-menu-links">
            <template v-if="!isLoggedIn">
              <router-link
                  to="/login"
                  class="account-menu-link"
              >
                {{ $t("navbar.login") }}
              </router-link>

              <router-link
                  to="/register"
                  class="account-menu-link"
              >
                {{ $t("navbar.register") }}
              </router-link>
            </template>

            <template v-else>
              <router-link
                  to="/profile"
                  class="account-menu-link"
              >
                {{ $t("navbar.profile") }}
              </router-link>

              <button
                  type="button"
                  class="account-menu-link logout-menu-button"
                  @click="logout"
              >
                {{ $t("navbar.logout") }}
              </button>
            </template>
          </div>

          <div class="menu-divider"></div>

          <div class="language-section">
            <span class="language-title">
              {{ $t("navbar.language") }}
            </span>

            <div
                class="language-switch"
                :class="`language-${currentLocale}`"
                role="group"
                :aria-label="$t('navbar.language')"
            >
              <span class="language-switch-thumb"></span>

              <button
                  type="button"
                  class="language-option"
                  :class="{ active: currentLocale === 'fr' }"
                  :aria-pressed="currentLocale === 'fr'"
                  @click="setLanguage('fr')"
              >
                FR
              </button>

              <button
                  type="button"
                  class="language-option"
                  :class="{ active: currentLocale === 'nl' }"
                  :aria-pressed="currentLocale === 'nl'"
                  @click="setLanguage('nl')"
              >
                NL
              </button>

              <button
                  type="button"
                  class="language-option"
                  :class="{ active: currentLocale === 'en' }"
                  :aria-pressed="currentLocale === 'en'"
                  @click="setLanguage('en')"
              >
                EN
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </nav>
</template>

<script>
import AuthService from "../services/AuthService";
import i18n from "../i18n";
import NavbarSearch from "./NavbarSearch.vue";

export default {
  name: "AppNavbar",

  components: {
    NavbarSearch
  },

  data() {
    return {
      isLoggedIn: false,
      isAdmin: false,
      isPartner: false,
      isMenuOpen: false,
      currentLocale: i18n.global.locale.value
    };
  },

  mounted() {
    this.refreshAuthState();

    document.addEventListener("click", this.closeMenu);
  },

  beforeUnmount() {
    document.removeEventListener("click", this.closeMenu);
  },

  watch: {
    $route() {
      this.refreshAuthState();
      this.closeMenu();
    }
  },

  methods: {
    refreshAuthState() {
      this.isLoggedIn = AuthService.isLoggedIn();
      this.isAdmin = AuthService.hasRole("ADMIN");
      this.isPartner = AuthService.hasRole("PARTNER");
    },

    setLanguage(language) {
      this.currentLocale = language;
      i18n.global.locale.value = language;

      localStorage.setItem("language", language);

      document.documentElement.lang = language;
      document.title = i18n.global.t("navbar.documentTitle");
    },

    toggleMenu() {
      this.isMenuOpen = !this.isMenuOpen;
    },

    closeMenu() {
      this.isMenuOpen = false;
    },

    logout() {
      AuthService.logout();
      this.refreshAuthState();
      this.closeMenu();
      this.$router.push("/login");
    }
  }
};
</script>

<style scoped src="./AppNavbar.css"></style>