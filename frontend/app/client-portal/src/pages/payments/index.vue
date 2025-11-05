<template>
  <div class="page-payments">
    <div class="page-payments__header">
      <div class="page-payments__title">
        <h1>Payments</h1>
        <p>支付管理，支付处理、支付记录、支付统计</p>
      </div>
    </div>

    <div class="page-payments__placeholder">
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
.page-payments {
  min-height: 100vh;
  padding: var(--spacing-lg);
}

.page-payments__header {
  margin-bottom: var(--spacing-xl);
}

.page-payments__title h1 {
  font-size: var(--font-size-2xl);
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 var(--spacing-sm) 0;
}

.page-payments__title p {
  font-size: var(--font-size-base);
  color: var(--text-secondary);
  margin: 0;
}

.page-payments__placeholder {
  text-align: center;
  padding: var(--spacing-2xl) 0;
}
</style>

<route lang="yaml">
name: Payments
meta:
  title: 'Payments'
  layout: 'main'
</route>