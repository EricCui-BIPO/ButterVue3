<template>
  <div class="page-invoice">
    <div class="page-invoice__header">
      <div class="page-invoice__title">
        <h1>Invoice</h1>
        <p>发票管理，发票开具、发票查询、发票统计</p>
      </div>
    </div>

    <div class="page-invoice__placeholder">
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
.page-invoice {
  min-height: 100vh;
  padding: var(--spacing-lg);
}

.page-invoice__header {
  margin-bottom: var(--spacing-xl);
}

.page-invoice__title h1 {
  font-size: var(--font-size-2xl);
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 var(--spacing-sm) 0;
}

.page-invoice__title p {
  font-size: var(--font-size-base);
  color: var(--text-secondary);
  margin: 0;
}

.page-invoice__placeholder {
  text-align: center;
  padding: var(--spacing-2xl) 0;
}
</style>

<route lang="yaml">
name: Invoice
meta:
  title: 'Invoice'
  layout: 'main'
</route>