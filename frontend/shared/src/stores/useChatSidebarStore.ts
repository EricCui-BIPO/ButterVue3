import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useChatSidebarStore = defineStore('chatSidebar', () => {
  // 状态：侧边栏是否折叠（默认关闭）
  const isCollapsed = ref(true);

  // 操作：切换折叠状态
  const toggleCollapse = () => {
    isCollapsed.value = !isCollapsed.value;
  };

  // 操作：设置折叠状态
  const setCollapsed = (collapsed: boolean) => {
    isCollapsed.value = collapsed;
  };

  const ensureCollapsed = () => {
    if (!isCollapsed.value) {
      isCollapsed.value = true;
    }
  };

  return {
    // 状态
    isCollapsed,
    // 操作
    toggleCollapse,
    setCollapsed,
    ensureCollapsed
  };
});
