<template>
  <div class="page-access">
    <div class="page-access__header">
      <div class="page-access__title">
        <h1>Access</h1>
        <p>权限管理，权限控制、访问控制、权限审计</p>
      </div>
    </div>

    <div class="page-access__placeholder">
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
.page-access {
  min-height: 100vh;
  padding: var(--spacing-lg);
}

.page-access__header {
  margin-bottom: var(--spacing-xl);
}

.page-access__title h1 {
  font-size: var(--font-size-2xl);
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 var(--spacing-sm) 0;
}

.page-access__title p {
  font-size: var(--font-size-base);
  color: var(--text-secondary);
  margin: 0;
}

.page-access__placeholder {
  text-align: center;
  padding: var(--spacing-2xl) 0;
}
</style>

<route lang="yaml">
name: Access
meta:
  title: 'Access'
  layout: 'main'
</route>