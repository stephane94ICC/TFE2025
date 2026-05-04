import { createRouter, createWebHistory } from 'vue-router';

import HomePage from '../pages/HomePage.vue';
import UserList from '../components/UserList.vue';
import ActivityListPage from '../pages/ActivityListPage.vue';
import ActivityDetailPage from '../pages/ActivityDetailPage.vue';
import ShopPage from '../pages/ShopPage.vue';
import ProductDetailPage from '../pages/ProductDetailPage.vue';

const routes = [
    {
        path: '/',
        name: 'home',
        component: HomePage
    },
    {
        path: '/users',
        name: 'users',
        component: UserList
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
    }
];

const router = createRouter({
    history: createWebHistory(),
    routes
});

export default router;