<template>
  <div class="page-email-log">
    <div class="page-email-log__header">
      <div class="page-email-log__title">
        <h1>EmailLog</h1>
        <p>邮件日志，邮件发送、邮件记录、邮件统计</p>
      </div>
    </div>

    <div class="page-email-log__placeholder">
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
.page-email-log {
  min-height: 100vh;
  padding: var(--spacing-lg);
}

.page-email-log__header {
  margin-bottom: var(--spacing-xl);
}

.page-email-log__title h1 {
  font-size: var(--font-size-2xl);
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 var(--spacing-sm) 0;
}

.page-email-log__title p {
  font-size: var(--font-size-base);
  color: var(--text-secondary);
  margin: 0;
}

.page-email-log__placeholder {
  text-align: center;
  padding: var(--spacing-2xl) 0;
}
</style>

<route lang="yaml">
name: EmailLog
meta:
  title: 'Email Log'
  layout: 'main'
</route>