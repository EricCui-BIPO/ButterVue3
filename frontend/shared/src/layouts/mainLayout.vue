<template>
  <el-container
    class="app-wrapper"
    :class="{
      fullscreen: $route.meta?.fullscreen
    }"
  >
    <el-aside width="240px">
      <div class="logo">butter.ai</div>
      <SideBar />
    </el-aside>

    <el-container>
      <el-header>
        <NavBar />
      </el-header>

      <el-main>
        <div class="mains">
          <router-view />
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import SideBar from './Sidebar/index.vue';
import NavBar from './Navbar/index.vue';
</script>

<style scoped lang="scss">
.app-wrapper {
  width: 100%;
  height: 100vh;
  position: relative;

  --navbar-height: 80px;

  .logo {
    height: 60px;
    line-height: 60px;
    text-align: center;
    font-size: 24px;
    font-weight: 600;
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
