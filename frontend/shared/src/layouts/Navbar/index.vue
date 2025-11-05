<template>
  <div class="navbar">
    <!-- 左侧面包屑导航 -->
    <div class="navbar-left">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item v-for="(item, index) in breadcrumbItems" :key="index" :to="item.path">
          {{ item.title }}
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 右侧功能区域 -->
    <div class="navbar-right">
      <div class="navbar-actions">
        <!-- 用户头像和信息 -->
        <UserAvatar />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { GlobalMenuConfig } from '@I0/shared/config/menu'
import type { MenuItem } from '@I0/shared/types'
import UserAvatar from './components/UserAvatar.vue'

const route = useRoute()

// 面包屑项目
const breadcrumbItems = ref<Array<{ title: string; path: string }>>([])

// 获取全局菜单配置
const globalMenuConfig = GlobalMenuConfig.getInstance().getConfig()

/**
 * 根据路由名称在菜单中查找对应的菜单项
 */
function findMenuItemByName(menus: MenuItem[], routeName: string): MenuItem | null {
  for (const menu of menus) {
    // 检查当前菜单项
    if (menu.name === routeName) {
      return menu
    }

    // 递归检查子菜单
    if (menu.children && menu.children.length > 0) {
      const found = findMenuItemByName(menu.children, routeName)
      if (found) {
        return found
      }
    }
  }
  return null
}

/**
 * 获取菜单项的完整路径（从根到当前项）
 */
function getMenuItemPath(menus: MenuItem[], targetName: string, currentPath: MenuItem[] = []): MenuItem[] | null {
  for (const menu of menus) {
    const newPath = [...currentPath, menu]

    // 找到目标菜单项
    if (menu.name === targetName) {
      return newPath
    }

    // 递归搜索子菜单
    if (menu.children && menu.children.length > 0) {
      const found = getMenuItemPath(menu.children, targetName, newPath)
      if (found) {
        return found
      }
    }
  }
  return null
}

/**
 * 生成面包屑导航
 */
function generateBreadcrumb() {
  if (!route.name || !globalMenuConfig) {
    breadcrumbItems.value = []
    return
  }

  // 在菜单配置中查找当前路由对应的菜单项路径
  const menuPath = getMenuItemPath(globalMenuConfig.menus, route.name as string)

  if (menuPath) {
    // 过滤掉没有路径的菜单组，只保留有实际路径的菜单项
    breadcrumbItems.value = menuPath
      .filter(item => item.path && item.path !== '#')
      .map(item => ({
        title: item.title || item.name || '',
        path: item.path || ''
      }))
  } else {
    // 如果在菜单中找不到，使用路由的meta信息作为后备
    breadcrumbItems.value = route.meta?.title
      ? [{ title: route.meta.title as string, path: route.path }]
      : []
  }
}

// 监听路由变化，更新面包屑
watch(
  () => route.name,
  () => {
    generateBreadcrumb()
  },
  { immediate: true }
)
</script>

<style scoped lang="scss">
.navbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;

  .navbar-left {
    display: flex;
    align-items: center;
  }

  .navbar-right {
    display: flex;
    align-items: center;
  }

  .navbar-actions {
    display: flex;
    align-items: center;
    gap: 8px;
  }
}
</style>
