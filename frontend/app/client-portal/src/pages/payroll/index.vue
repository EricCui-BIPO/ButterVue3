<template>
  <div class="page-payroll">
    <div class="page-payroll__header">
      <div class="page-payroll__title">
        <h1>Payroll</h1>
        <p>薪资管理，薪资计算、薪资发放、薪资统计</p>
      </div>
    </div>

    <div class="page-payroll__placeholder">
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
.page-payroll {
  min-height: 100vh;
  padding: var(--spacing-lg);
}

.page-payroll__header {
  margin-bottom: var(--spacing-xl);
}

.page-payroll__title h1 {
  font-size: var(--font-size-2xl);
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 var(--spacing-sm) 0;
}

.page-payroll__title p {
  font-size: var(--font-size-base);
  color: var(--text-secondary);
  margin: 0;
}

.page-payroll__placeholder {
  text-align: center;
  padding: var(--spacing-2xl) 0;
}
</style>

<route lang="yaml">
name: Payroll
meta:
  title: 'Payroll'
  layout: 'main'
</route>