<template>
  <div class="page-compliance">
    <div class="page-compliance__header">
      <div class="page-compliance__title">
        <h1>Compliance</h1>
        <p>合规管理，合规检查、合规报告、合规监控</p>
      </div>
    </div>

    <div class="page-compliance__placeholder">
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
.page-compliance {
  min-height: 100vh;
  padding: var(--spacing-lg);
}

.page-compliance__header {
  margin-bottom: var(--spacing-xl);
}

.page-compliance__title h1 {
  font-size: var(--font-size-2xl);
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 var(--spacing-sm) 0;
}

.page-compliance__title p {
  font-size: var(--font-size-base);
  color: var(--text-secondary);
  margin: 0;
}

.page-compliance__placeholder {
  text-align: center;
  padding: var(--spacing-2xl) 0;
}
</style>

<route lang="yaml">
name: Compliance
meta:
  title: 'Compliance'
  layout: 'main'
</route>