<template>
  <div class="page-payslips">
    <div class="page-payslips__header">
      <div class="page-payslips__title">
        <h1>Payslips</h1>
        <p>工资单管理，工资单查询、工资单下载、工资单统计</p>
      </div>
    </div>

    <div class="page-payslips__placeholder">
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
.page-payslips {
  min-height: 100vh;
  padding: var(--spacing-lg);
}

.page-payslips__header {
  margin-bottom: var(--spacing-xl);
}

.page-payslips__title h1 {
  font-size: var(--font-size-2xl);
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 var(--spacing-sm) 0;
}

.page-payslips__title p {
  font-size: var(--font-size-base);
  color: var(--text-secondary);
  margin: 0;
}

.page-payslips__placeholder {
  text-align: center;
  padding: var(--spacing-2xl) 0;
}
</style>

<route lang="yaml">
name: Payslips
meta:
  title: 'Payslips'
  layout: 'main'
</route>