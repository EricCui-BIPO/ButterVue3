<template>
  <div class="page-expenses">
    <div class="page-expenses__header">
      <div class="page-expenses__title">
        <h1>Expenses</h1>
        <p>费用管理，费用报销、费用审批、费用统计</p>
      </div>
    </div>

    <div class="page-expenses__placeholder">
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
.page-expenses {
  min-height: 100vh;
  padding: var(--spacing-lg);
}

.page-expenses__header {
  margin-bottom: var(--spacing-xl);
}

.page-expenses__title h1 {
  font-size: var(--font-size-2xl);
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 var(--spacing-sm) 0;
}

.page-expenses__title p {
  font-size: var(--font-size-base);
  color: var(--text-secondary);
  margin: 0;
}

.page-expenses__placeholder {
  text-align: center;
  padding: var(--spacing-2xl) 0;
}
</style>

<route lang="yaml">
name: Expenses
meta:
  title: 'Expenses'
  layout: 'main'
</route>