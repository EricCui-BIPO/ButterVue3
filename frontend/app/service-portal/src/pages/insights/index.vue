<template>
  <div class="page-insights">
    <div class="page-insights__header">
      <div class="page-insights__title">
        <h1>Insights</h1>
        <p>洞察分析，数据洞察、趋势分析、智能预测</p>
      </div>
    </div>

    <div class="page-insights__placeholder">
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
.page-insights {
  min-height: 100vh;
  padding: var(--spacing-lg);
}

.page-insights__header {
  margin-bottom: var(--spacing-xl);
}

.page-insights__title h1 {
  font-size: var(--font-size-2xl);
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 var(--spacing-sm) 0;
}

.page-insights__title p {
  font-size: var(--font-size-base);
  color: var(--text-secondary);
  margin: 0;
}

.page-insights__placeholder {
  text-align: center;
  padding: var(--spacing-2xl) 0;
}
</style>

<route lang="yaml">
name: Insights
meta:
  title: 'Insights'
  layout: 'main'
</route>