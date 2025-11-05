<template>
  <div class="page-roles">
    <div class="page-roles__header">
      <div class="page-roles__title">
        <h1>Roles</h1>
        <p>角色管理，角色创建、权限分配、角色管理</p>
      </div>
    </div>

    <div class="page-roles__placeholder">
      <el-empty description="页面功能待实现">
        <el-button type="primary">敬请期待</el-button>
      </el-empty>
    </div>
  </div>
</template>

<script setup lang="ts">
// No props needed - domain pages use static content
// No computed properties needed for simplified structure
</script>

<style scoped>
.page-roles {
  min-height: 100vh;
  padding: var(--spacing-lg);
}

.page-roles__header {
  margin-bottom: var(--spacing-xl);
}

.page-roles__title h1 {
  font-size: var(--font-size-2xl);
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 var(--spacing-sm) 0;
}

.page-roles__title p {
  font-size: var(--font-size-base);
  color: var(--text-secondary);
  margin: 0;
}

.page-roles__placeholder {
  text-align: center;
  padding: var(--spacing-2xl) 0;
}
</style>

<route lang="yaml">
name: Roles
meta:
  title: 'Roles'
  layout: 'main'
</route>