<template>
  <el-menu
    :default-active="activeIndex"
    class="sidebar-menu"
    @select="handleMenuSelect"
    :collapse="collapsed"
    :unique-opened="uniqueOpened"
    :mode="mode"
  >
    <template v-for="group in filteredMenuGroups" :key="group.title">
      <el-menu-item-group v-if="!group.meta?.hidden" :title="group.title">
        <template v-for="item in group.children" :key="getMenuItemKey(item)">
          <el-sub-menu
            v-if="item.children && item.children.length > 0 && !item.meta?.hidden"
            :index="getMenuItemIndex(item)"
            :disabled="item.meta?.disabled"
          >
            <template #title>
              <el-icon v-if="item.icon" class="menu-icon">
                <component :is="item.icon" />
              </el-icon>
              <span>{{ item.title }}</span>
            </template>
            <el-menu-item
              v-for="child in item.children"
              :key="getMenuItemKey(child)"
              :index="getMenuItemIndex(child)"
              :disabled="child.meta?.disabled"
            >
              <el-icon v-if="child.icon" class="menu-icon">
                <component :is="child.icon" />
              </el-icon>
              {{ child.title }}
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item
            v-else-if="!item.meta?.hidden"
            :index="getMenuItemIndex(item)"
            :disabled="item.meta?.disabled"
          >
            <el-icon v-if="item.icon" class="menu-icon">
              <component :is="item.icon" />
            </el-icon>
            {{ item.title }}
          </el-menu-item>
        </template>
      </el-menu-item-group>
    </template>
  </el-menu>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import type {
  MenuConfig,
  MenuItem,
  MenuFilterOptions,
  PermissionChecker
} from '@I0/shared/types'
import {
  filterMenuGroups,
  getActiveMenuIndex,
  findMenuItemByIndex,
  getMenuItemIndex
} from '@I0/shared/utils/menu'
import { GlobalMenuConfig } from '@I0/shared/config/menu'

// 定义组件props
interface SidebarProps {
  menuConfig?: MenuConfig
  collapsed?: boolean
  uniqueOpened?: boolean
  mode?: 'vertical' | 'horizontal'
  permissionChecker?: PermissionChecker
  showHidden?: boolean
}

const props = withDefaults(defineProps<SidebarProps>(), {
  collapsed: false,
  uniqueOpened: true,
  mode: 'vertical',
  showHidden: false
})

// 从单例获取菜单配置（如果没有通过props传入）
const globalMenuConfig = GlobalMenuConfig.getInstance().getConfig()

const router = useRouter()
const route = useRoute()

// 默认菜单配置
const defaultMenuConfig: MenuConfig = {
  menus: []
}

// 获取菜单配置
const menuConfig = computed(() => {
  return props.menuConfig || globalMenuConfig || defaultMenuConfig
})

// 过滤菜单组
const filteredMenuGroups = computed(() => {
  const filterOptions: MenuFilterOptions = {
    permissionChecker: props.permissionChecker,
    showHidden: props.showHidden
  }

  return filterMenuGroups(menuConfig.value.menus, filterOptions)
})

// 计算当前激活的菜单项
const activeIndex = computed(() => {
  return getActiveMenuIndex(filteredMenuGroups.value, route)
})

// 获取菜单项的唯一键值
const getMenuItemKey = (item: MenuItem) => {
  return item.meta?.index || item.name || item.path || item.title || ''
}

// 处理菜单选择
const handleMenuSelect = (index: string) => {
  const menuItem = findMenuItemByIndex(filteredMenuGroups.value, index)

  if (menuItem?.path) {
    router.push(menuItem.path)
  }
  if (menuItem?.name) {
    router.push({ name: menuItem.name })
  }

  // 触发自定义事件
  if (menuItem?.meta?.onClick) {
    menuItem.meta.onClick(menuItem)
  }
}

// 暴露组件方法
defineExpose({
  getActiveIndex: () => activeIndex.value,
  getMenuConfig: () => menuConfig.value,
  refreshMenu: () => {
    // 强制重新计算菜单
    return filteredMenuGroups.value
  }
})
</script>

<style scoped>
.sidebar-menu {
  border-right: none;
}

.menu-icon {
  margin-right: 8px;
}
</style>
