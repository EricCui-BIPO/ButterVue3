<template>
  <div class="page-users">
    <div class="page-users__header">
      <div class="page-users__title">
        <h1>Users</h1>
        <p>用户管理，用户创建、用户权限、用户管理</p>
      </div>
    </div>

    <div class="page-users__placeholder">
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
.page-users {
  min-height: 100vh;
  padding: var(--spacing-lg);
}

.page-users__header {
  margin-bottom: var(--spacing-xl);
}

.page-users__title h1 {
  font-size: var(--font-size-2xl);
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 var(--spacing-sm) 0;
}

.page-users__title p {
  font-size: var(--font-size-base);
  color: var(--text-secondary);
  margin: 0;
}

.page-users__placeholder {
  text-align: center;
  padding: var(--spacing-2xl) 0;
}
</style>

<route lang="yaml">
name: Users
meta:
  title: 'Users'
  layout: 'main'
</route>