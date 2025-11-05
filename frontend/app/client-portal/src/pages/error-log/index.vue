<template>
  <div class="page-error-log">
    <div class="page-error-log__header">
      <div class="page-error-log__title">
        <h1>ErrorLog</h1>
        <p>错误日志，错误记录、错误分析、错误监控</p>
      </div>
    </div>

    <div class="page-error-log__placeholder">
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
.page-error-log {
  min-height: 100vh;
  padding: var(--spacing-lg);
}

.page-error-log__header {
  margin-bottom: var(--spacing-xl);
}

.page-error-log__title h1 {
  font-size: var(--font-size-2xl);
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 var(--spacing-sm) 0;
}

.page-error-log__title p {
  font-size: var(--font-size-base);
  color: var(--text-secondary);
  margin: 0;
}

.page-error-log__placeholder {
  text-align: center;
  padding: var(--spacing-2xl) 0;
}
</style>

<route lang="yaml">
name: ErrorLog
meta:
  title: 'Error Log'
  layout: 'main'
</route>