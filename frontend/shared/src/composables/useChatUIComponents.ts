import { ref } from 'vue';
import type { UIComponentData } from '@I0/shared/types';
import { createSharedComposable } from '@vueuse/core';

/**
 * 聊天UI组件管理 Hook
 * 负责管理多个UI组件的状态和操作
 */
export const useChatUIComponents = createSharedComposable(() => {
  // UI组件数据列表
  const uiComponents = ref<UIComponentData[]>([]);

  const addUIComponent = (uiComponentData: UIComponentData) => {
    uiComponents.value.unshift(uiComponentData);
  };

  /**
   * 移除指定索引的UI组件
   * @param index 组件索引
   */
  const removeUIComponent = (index: number) => {
    if (index >= 0 && index < uiComponents.value.length) {
      uiComponents.value.splice(index, 1);
    }
  };

  /**
   * 清空所有UI组件
   */
  const clearAllUIComponents = () => {
    uiComponents.value = [];
  };

  /**
   * 获取UI组件数量
   */
  const uiComponentsCount = () => uiComponents.value.length;

  /**
   * 检查是否有UI组件显示
   */
  const hasUIComponents = () => uiComponents.value.length > 0;

  return {
    // 状态
    uiComponents,

    // 方法
    addUIComponent,
    removeUIComponent,
    clearAllUIComponents,
    uiComponentsCount,
    hasUIComponents
  };
});
