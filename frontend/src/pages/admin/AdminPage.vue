<template>
  <div class="admin-page">
    <section class="admin-header">
      <h1>{{ $t("admin.dashboard.title") }}</h1>
      <p>
        {{ $t("admin.dashboard.introduction") }}
      </p>
    </section>

    <section class="admin-dashboard">
      <router-link to="/admin/users" class="admin-block admin-block-active">
        <h2>{{ $t("admin.dashboard.usersTitle") }}</h2>
        <p>
          {{ $t("admin.dashboard.usersDescription") }}
        </p>
        <span>{{ $t("admin.dashboard.access") }}</span>
      </router-link>

      <router-link to="/admin/activities" class="admin-block admin-block-active">
        <h2>{{ $t("admin.dashboard.activitiesTitle") }}</h2>
        <p>
          {{ $t("admin.dashboard.activitiesDescription") }}
        </p>
        <span>{{ $t("admin.dashboard.access") }}</span>
      </router-link>

      <router-link to="/admin/products" class="admin-block admin-block-active">
        <h2>{{ $t("admin.dashboard.productsTitle") }}</h2>
        <p>
          {{ $t("admin.dashboard.productsDescription") }}
        </p>
        <span>{{ $t("admin.dashboard.access") }}</span>
      </router-link>

      <router-link to="/admin/activity-logs" class="admin-block admin-block-active">
        <h2>{{ $t("admin.dashboard.activityLogTitle") }}</h2>
        <p>
          {{ $t("admin.dashboard.activityLogDescription") }}
        </p>
        <span>{{ $t("admin.dashboard.access") }}</span>
      </router-link>

      <div class="admin-block admin-block-disabled">
        <h2>{{ $t("admin.dashboard.partnersTitle") }}</h2>
        <p>
          {{ $t("admin.dashboard.partnersDescription") }}
        </p>
        <span>{{ $t("admin.dashboard.comingSoon") }}</span>
      </div>
    </section>
  </div>
</template>

<script>
import "./AdminPage.css";

export default {
  name: "AdminPage"
};
</script>