<template>
  <div class="page-contract">
    <div class="page-contract__header">
      <div class="page-contract__title">
        <h1>Contract</h1>
        <p>合同管理，合同创建、合同签署、合同管理</p>
      </div>
    </div>

    <div class="page-contract__placeholder">
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
.page-contract {
  min-height: 100vh;
  padding: var(--spacing-lg);
}

.page-contract__header {
  margin-bottom: var(--spacing-xl);
}

.page-contract__title h1 {
  font-size: var(--font-size-2xl);
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 var(--spacing-sm) 0;
}

.page-contract__title p {
  font-size: var(--font-size-base);
  color: var(--text-secondary);
  margin: 0;
}

.page-contract__placeholder {
  text-align: center;
  padding: var(--spacing-2xl) 0;
}
</style>

<route lang="yaml">
name: Contract
meta:
  title: 'Contract'
  layout: 'main'
</route>