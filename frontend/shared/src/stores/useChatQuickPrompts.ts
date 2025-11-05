import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { chatAPI } from '@I0/shared/api';
import { useAsyncAction } from '@I0/shared/composables';
import type { QuickPrompt } from '@I0/shared/types';

export const useChatQuickPrompts = defineStore('chatQuickPrompts', () => {
  // 状态定义
  const quickPrompts = ref<QuickPrompt[]>([]);
  const quickPromptsLastFetched = ref<number | null>(null);

  // 计算属性
  const activeQuickPrompts = computed(() =>
    quickPrompts.value.filter(prompt => prompt.active).sort((a, b) => a.order - b.order)
  );

  // 加载快速提示词
  const [loadQuickPrompts, quickPromptsLoading] = useAsyncAction(async () => {
    const list = await chatAPI.getQuickPrompts();
    quickPrompts.value = list || [];
    quickPromptsLastFetched.value = Date.now();
  });

  // 检查是否需要刷新数据（5分钟缓存）
  const shouldRefreshPrompts = computed(() => {
    if (!quickPromptsLastFetched.value) return true;
    const fiveMinutes = 5 * 60 * 1000;
    return Date.now() - quickPromptsLastFetched.value > fiveMinutes;
  });

  // 智能加载提示词（带缓存）
  const ensureQuickPromptsLoaded = async () => {
    if (shouldRefreshPrompts.value || quickPrompts.value.length === 0) {
      await loadQuickPrompts();
    }
  };

  // 清除所有快速提示词
  const clearQuickPrompts = () => {
    quickPrompts.value = [];
    quickPromptsLastFetched.value = null;
  };

  return {
    // 状态
    quickPrompts,
    quickPromptsLoading,
    quickPromptsLastFetched,

    // 计算属性
    activeQuickPrompts,
    shouldRefreshPrompts,

    // 方法
    loadQuickPrompts,
    clearQuickPrompts,
    ensureQuickPromptsLoaded
  };
});
