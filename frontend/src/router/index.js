import { createRouter, createWebHistory } from 'vue-router';

import HomePage from '../pages/public/HomePage.vue';
import UserList from '../components/UserList.vue';
import ActivityListPage from '../pages/public/ActivityListPage.vue';
import ActivityDetailPage from '../pages/public/ActivityDetailPage.vue';
import ShopPage from '../pages/public/ShopPage.vue';
import ProductDetailPage from '../pages/public/ProductDetailPage.vue';
import CartPage from '../pages/member/CartPage.vue';
import RegisterPage from "../pages/auth/RegisterPage.vue";
import LoginPage from "../pages/auth/LoginPage.vue";
import PartnerPage from "../pages/partner/PartnerPage.vue";
import AuthService from "../services/AuthService";

import ProfilePage from "../pages/member/ProfilePage.vue";

import AdminPage from "../pages/admin/AdminPage.vue";
import AdminProductsPage from "../pages/admin/AdminProductsPage.vue";
import AdminProductFormPage from "../pages/admin/AdminProductFormPage.vue";

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
        path: "/admin/products",
        name: "AdminProducts",
        component: AdminProductsPage,
        meta:{
            requiresAuth: true,
            roles: ["ADMIN"]
        }
    },
    {
        path: "/admin/products/new",
        name: "AdminProductNew",
        component: AdminProductFormPage,
        meta:{
            requiresAuth: true,
            roles: ["ADMIN"]
        }
    },
    {
        path: "/admin/products/edit/:id",
        name: "AdminProductEdit",
        component: AdminProductFormPage,
        meta:{
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