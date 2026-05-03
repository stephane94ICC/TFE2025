import { createRouter, createWebHistory } from 'vue-router';

import HomePage from '../pages/HomePage.vue';
import UserList from '../components/UserList.vue';
import ActivityListPage from '../pages/ActivityListPage.vue';
import ActivityDetailPage from '../pages/ActivityDetailPage.vue';

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
    }
];

const router = createRouter({
    history: createWebHistory(),
    routes
});

export default router;