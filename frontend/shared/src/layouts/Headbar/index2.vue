<template>
  <div class="headbar-container">
    <div class="inner">
      <div class="logo" @click="$router.push('/')">butter.ai</div>
      <el-menu
        :default-active="selectedTopMenu"
        class="top-menu"
        popper-class="headbar-menu-popper"
        mode="horizontal"
        :collapse="false"
        :unique-opened="true"
        @select="handleTopMenuSelect"
      >
        <template v-for="group in filteredMenuGroups" :key="group.title">
          <!-- 非选中分组：有子菜单时显示下拉 -->
          <el-sub-menu
            v-if="
              Array.isArray(group.children) &&
              group.children.length > 0 &&
              !group.meta?.hidden &&
              selectedTopMenu !== group.title
            "
            :index="group.title"
            :disabled="group.meta?.disabled"
          >
            <template #title>
              <span class="menu-title">{{ group.title }}</span>
            </template>

            <el-menu-item
              v-for="child in group.children"
              :key="getMenuItemKey(child)"
              :index="getMenuItemIndex(child)"
              :disabled="child.meta?.disabled"
            >
              <el-icon v-if="child.icon" class="menu-icon">
                <component :is="child.icon" />
              </el-icon>
              {{ child.name || child.title }}
            </el-menu-item>
          </el-sub-menu>

          <!-- 选中分组或无子菜单：仅显示一级菜单项，不弹出下拉 -->
          <el-menu-item
            v-else-if="!group.meta?.hidden"
            :index="group.title"
            :disabled="group.meta?.disabled"
            class="is-selected"
          >
            <span class="menu-title">{{ group.title }}</span>
          </el-menu-item>
        </template>
      </el-menu>
    </div>
    <!-- 二级菜单栏 - 仅在有选中一级菜单时显示 -->
    <div class="submenu-container">
      <el-menu
        :default-active="activeIndex"
        class="sub-menu"
        mode="horizontal"
        @select="handleMenuSelect"
        :collapse="false"
        :unique-opened="true"
      >
        <el-menu-item
          v-for="item in subMenuItems"
          :key="getMenuItemKey(item)"
          class="submenu-item"
          :index="getMenuItemIndex(item)"
          :disabled="item.meta?.disabled"
        >
          <el-icon v-if="item.icon" class="menu-icon">
            <component :is="item.icon" />
          </el-icon>
          <span>{{ item.name || item.title }}</span>
        </el-menu-item>
      </el-menu>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch, nextTick } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import type { MenuConfig, MenuItem } from '@I0/shared/types';
import {
  filterMenuGroups,
  getActiveMenuIndex,
  findMenuItemByIndex,
  getMenuItemIndex
} from '@I0/shared/utils/menu';
import { GlobalMenuConfig } from '@I0/shared/config/menu';

// 定义组件props

// 从单例获取菜单配置（如果没有通过props传入）
const globalMenuConfig = GlobalMenuConfig.getInstance().getConfig();

const router = useRouter();
const route = useRoute();

// 当前选中的一级菜单
const selectedTopMenu = ref<string>('');

// 默认菜单配置
const defaultMenuConfig: MenuConfig = {
  menus: []
};

// 获取菜单配置
const menuConfig = computed(() => {
  return globalMenuConfig || defaultMenuConfig;
});

// 过滤菜单组
const filteredMenuGroups = computed(() => {
  return filterMenuGroups(menuConfig.value.menus, { showHidden: false });
});

// 计算当前激活的菜单项
const activeIndex = computed(() => {
  return getActiveMenuIndex(filteredMenuGroups.value, route);
});

// 获取当前选中的一级菜单项
const selectedTopMenuItem = computed(() => {
  if (!selectedTopMenu.value) return null;
  return filteredMenuGroups.value.find(group => group.title === selectedTopMenu.value);
});

// 获取当前选中一级菜单的二级菜单项
const subMenuItems = computed(() => {
  return selectedTopMenuItem.value?.children || [];
});

// 获取菜单项的唯一键值
const getMenuItemKey = (item: MenuItem) => {
  return item.meta?.index || item.name || item.path || item.title || '';
};

// 根据路由自动设置选中的一级菜单
const setSelectedTopMenuByRoute = () => {
  const activeMenuItem = findMenuItemByIndex(filteredMenuGroups.value, activeIndex.value);

  if (activeMenuItem) {
    for (const group of filteredMenuGroups.value) {
      if (group.children) {
        const foundInGroup = group.children.some(
          child => getMenuItemIndex(child) === activeIndex.value
        );
        if (foundInGroup) {
          if (!selectedTopMenu.value || selectedTopMenu.value !== group.title) {
            selectedTopMenu.value = group.title || '';
          }
          return;
        }
      }
    }
  }
};

// 处理一级菜单选择（接收子项 index，映射到所属的一级菜单）
const handleTopMenuSelect = (index: string) => {
  console.log('handleTopMenuSelect', index);
  const menuItem = findMenuItemByIndex(filteredMenuGroups.value, index);
  const parentGroup = filteredMenuGroups.value.find(
    group =>
      Array.isArray(group.children) &&
      group.children.some(child => getMenuItemIndex(child) === index)
  );
  // 若找到所属分组，选中该一级菜单；否则直接使用 index（兼容直接选择一级）
  selectedTopMenu.value = parentGroup?.title || index;

  // 当 index 属于下拉子菜单时，执行路由跳转（与二级菜单一致）
  if (menuItem && parentGroup) {
    if (menuItem.path) {
      router.push(menuItem.path);
      return;
    }
    if (menuItem.name) {
      router.push({ name: menuItem.name });
      return;
    }
    if (menuItem.meta?.onClick) {
      menuItem.meta.onClick(menuItem);
    }
  }
};

// 处理菜单选择
const handleMenuSelect = (index: string) => {
  const menuItem = findMenuItemByIndex(filteredMenuGroups.value, index);

  // 立即设置对应的一级菜单状态 - 直接遍历查找
  for (const group of filteredMenuGroups.value) {
    if (group.children) {
      const foundInGroup = group.children.some(child => getMenuItemIndex(child) === index);
      if (foundInGroup) {
        selectedTopMenu.value = group.title || '';
        break;
      }
    }
  }

  if (menuItem?.path) {
    router.push(menuItem.path);
  }
  if (menuItem?.name) {
    router.push({ name: menuItem.name });
  }

  // 触发自定义事件
  if (menuItem?.meta?.onClick) {
    menuItem.meta.onClick(menuItem);
  }
};

// 监听路由变化，自动更新选中状态
watch(
  () => route.path,
  () => {
    // 延迟执行，让菜单选择先完成
    nextTick(() => {
      // 只有当没有选中菜单时才自动设置
      if (!selectedTopMenu.value) {
        setSelectedTopMenuByRoute();
      }
    });
  },
  { immediate: true }
);

// 监听菜单配置变化，自动设置选中状态
watch(
  filteredMenuGroups,
  () => {
    // 延迟执行，确保菜单配置已加载完成
    nextTick(() => {
      setSelectedTopMenuByRoute();
    });
  },
  { immediate: true }
);
</script>

<style scoped lang="scss">
.headbar-container {
  width: var(--container-width-max);
  background-color: #ffc72c;

  .inner {
    max-width: var(--container-width-large);
    margin: 0 auto;
    display: flex;
    align-items: center;
    flex-wrap: wrap;
  }

  .logo {
    height: 50px;
    line-height: 50px;
    text-align: center;
    font-size: 24px;
    font-weight: 600;
    margin-right: 12px;
    white-space: nowrap;
    margin: 0 120px 0 10px;
    font-style: italic;

    cursor: pointer;
    &:hover {
      color: #636363;
    }
  }

  .el-menu {
    background-color: transparent;
    &.top-menu {
      padding: 10px 0 0;
    }

    &.sub-menu {
      padding: 10px 0 0;
      max-width: 1800px;
      margin: 0 auto;
    }

    > li {
      margin: 0 10px;
    }
  }

  .top-menu {
    flex: 1;
    min-width: 0;
  }

  .el-menu--horizontal.el-menu {
    border: none;
  }

  .el-menu-item {
    height: 40px;
    border-radius: 20px;
    border-bottom: none;
    border: 2px solid transparent;
    &.is-active {
      background-color: #fff;
      font-weight: 600;
    }

    &:not(.is-disabled):hover {
      background-color: rgba(255, 255, 255, 0.3);
    }
  }

  .el-sub-menu {
    height: 40px;
    border-radius: 20px;
    overflow: hidden;
    &.is-opened {
      background-color: transparent;
    }
    .el-sub-menu__title {
      border-radius: 20px;
    }
  }

  .submenu-container {
    width: var(--container-width-max);
    height:40px;
    background-color: #ffe292;
    .el-menu.sub-menu{
      padding: 0;
    }
     .el-menu-item{
        height: 30px;
        line-height: 30px;
        margin-top:5px;
     }
  }
}
</style>
