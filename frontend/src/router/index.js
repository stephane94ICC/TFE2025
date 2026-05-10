import { createRouter, createWebHistory } from 'vue-router';

import HomePage from '../pages/HomePage.vue';
import UserList from '../components/UserList.vue';
import ActivityListPage from '../pages/ActivityListPage.vue';
import ActivityDetailPage from '../pages/ActivityDetailPage.vue';
import ShopPage from '../pages/ShopPage.vue';
import ProductDetailPage from '../pages/ProductDetailPage.vue';
import CartPage from '../pages/CartPage.vue';
import RegisterPage from "../pages/RegisterPage.vue";
import LoginPage from "../pages/LoginPage.vue";
import PartnerPage from "../pages/PertnerPage.vue";
import AuthService from "../services/AuthService";
import AdminPage from "../pages/AdminPage.vue";
import ProfilePage from "../pages/ProfilePage.vue";


const routes = [
    {
        path: '/',
        name: 'home',
        component: HomePage
    },
    {
        path: '/users',
        name: 'users',
        component: UserList,
        meta: {
            requiresAuth: true,
            roles: ["ADMIN"]
        }
    },
    {
        path: '/activities',
        name: 'activities',
        component: ActivityListPage
    },
    {
        path: '/activities/:id',
        name: 'activity-detail',
        component: ActivityDetailPage
    },
    {
        path: '/shop',
        name: 'shop',
        component: ShopPage
    },
    {
        path: '/products/:id',
        name: 'product-detail',
        component: ProductDetailPage
    },
    {
        path: '/cart',
        name : 'CartPage',
        component: CartPage,
        meta: {
            requiresAuth: true,
            roles: ["MEMBER", "ADMIN", "PARTNER"]
        }
    },
    {
      path: "/register",
      name: "Register",
      component: RegisterPage
    },
    {
      path: "/login",
      name: "Login",
      component: LoginPage
    },
    {
        path: "/partner",
        name: "Partner",
        component: PartnerPage,
        meta:{
            requiresAuth: true,
            roles : ["PARTNER"]
        }
    },
    {
        path : "/admin",
        name : "Admin",
        component: AdminPage,
        meta: {
            requiresAuth: true,
            roles: ["ADMIN"]
        }
    },
    {
        path: "/profile",
        name: "Profile",
        component: ProfilePage,
        meta: {
            requiresAuth: true,
            roles: ["MEMBER", "ADMIN", "PARTNER"]
        }
    }


];

const router = createRouter({
    history: createWebHistory(),
    routes
});

router.beforeEach((to, from, next) => {
    const requiresAuth = to.meta.requiresAuth;
    const allowedRoles = to.meta.roles;

    if (!requiresAuth) {
        next();
        return;
    }

    if (!AuthService.isLoggedIn()) {
        next("/login");
        return;
    }

    if (allowedRoles && allowedRoles.length > 0) {
        const hasAllowedRole = allowedRoles.some(role => AuthService.hasRole(role));

        if (!hasAllowedRole) {
            next("/");
            return;
        }
    }

    next();
});

export default router;