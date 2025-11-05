import { defineStore } from 'pinia';
import { ref } from 'vue';

export interface ChatCacheData {
  message: string;
  files: any[];
}

export const useChatCacheStore = defineStore('chatCache', () => {
  // 缓存的聊天数据
  const cachedChatData = ref<ChatCacheData | null>(null);

  // 设置缓存数据
  const setChatData = (data: ChatCacheData) => {
    cachedChatData.value = data;
  };

  // 清除缓存数据
  const clearChatData = () => {
    cachedChatData.value = null;
  };

  // 获取缓存数据
  const getChatData = (): ChatCacheData | null => {
    return cachedChatData.value;
  };

  return {
    cachedChatData,
    setChatData,
    clearChatData,
    getChatData,
  };
});