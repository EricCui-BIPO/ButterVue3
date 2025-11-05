<template>
  <div class="page-message">
    <div class="page-message__header">
      <div class="page-message__title">
        <h1>Message</h1>
        <p>消息中心，系统通知、用户消息、消息管理</p>
      </div>
    </div>

    <div class="page-message__placeholder">
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
.page-message {
  min-height: 100vh;
  padding: var(--spacing-lg);
}

.page-message__header {
  margin-bottom: var(--spacing-xl);
}

.page-message__title h1 {
  font-size: var(--font-size-2xl);
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 var(--spacing-sm) 0;
}

.page-message__title p {
  font-size: var(--font-size-base);
  color: var(--text-secondary);
  margin: 0;
}

.page-message__placeholder {
  text-align: center;
  padding: var(--spacing-2xl) 0;
}
</style>

<route lang="yaml">
name: Message
meta:
  title: 'Message'
  layout: 'main'
</route>