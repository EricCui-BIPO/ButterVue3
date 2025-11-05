<template>
  <el-container
    class="app-wrapper"
    :class="{
      fullscreen: $route.meta?.fullscreen
    }"
  >
    <el-header>
      <Headbar />
    </el-header>

    <el-main>
      <div class="mains">
        <router-view />
      </div>
    </el-main>
  </el-container>
</template>

<script setup lang="ts">
import Headbar from './Headbar/index2.vue';
</script>

<style scoped lang="scss">
.app-wrapper {
  width: 100%;
  height: 100vh;
  position: relative;

  --navbar-height: 120px;

  .el-header {
    padding:0;
    height: auto;
  }

  .mains {
    min-height: calc(100vh - var(--navbar-height) - 40px);
    overflow: auto;
  }

  &.fullscreen {
    > :deep(.el-container > .el-main) {
      padding: 0;
    }
    .mains {
      min-height: calc(100vh - var(--navbar-height));
    }
  }
}
</style>
