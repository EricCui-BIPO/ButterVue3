<template>
  <div class="chat-container">
    <!-- 左侧会话列表 -->
    <ChatSidebar
      :sessions="sessions"
      :active-id="currentSession?.id"
      :loading="isLoadingSessions"
      @create="clearCurrentSession"
      @select="selectSession"
      @command="handleSessionCommand"
    />

    <!-- 中间聊天区域 -->
    <div class="chat-main">
      <div v-if="!currentSession" class="welcome-screen">
        <div class="sender-container">
          <ChatSender
            v-model="inputMessage"
            :is-loading="isSending || isStreaming"
            @submit="handleSubmitMessage"
            @cancel="stopStreaming"
          />
        </div>
      </div>

      <div v-else class="chat-screen">
        <div class="messages-container" ref="messagesContainer">
          <ChatMessages
            :key="currentSession?.id"
            :messages="messages"
            :loading="isLoadingMessages"
            :current-assistant-message-id="currentAssistantMessage?.id"
            @thumbnail-click="handleThumbnailClick"
          />
        </div>

        <div class="sender-container">
          <ChatSender
            v-model="inputMessage"
            size="small"
            :is-loading="isSending || isStreaming"
            @submit="handleSubmitMessage"
            @cancel="stopStreaming"
          />
        </div>
      </div>
    </div>

    <!-- 右侧UI组件区域 - 支持多个组件 -->
    <ChatUIComponentsPanel :list="uiComponents" @close="removeUIComponent" />
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, onMounted } from 'vue';
import { useChat } from '@I0/shared/composables';
import { useChatCacheStore } from '@I0/shared/stores';
import type { UIComponentData } from '@I0/shared/types';
import { ChatSender, ChatSidebar, ChatUIComponentsPanel, ChatMessages } from './components';

const chat = useChat();
const chatCacheStore = useChatCacheStore();
const {
  sessions,
  currentSession,
  currentAssistantMessage,
  messages,
  isLoadingSessions,
  isLoadingMessages,
  isSending,
  isStreaming,
  uiComponents,
  loadSessions,
  deleteSession,
  selectSession,
  handleSendMessage,
  inputMessage,
  stopStreaming,
  removeUIComponent,
  addUIComponent,
  clearCurrentSession
} = chat;

// 消息容器引用
const messagesContainer = ref<HTMLElement>();

// 处理会话命令（包括删除）
const handleSessionCommand = async (command: any, item: any) => {
  if (command === 'delete') {
    await deleteSession(item.id);
  }
};

// 处理发送消息包装函数
const handleSubmitMessage = () => {
  handleSendMessage(inputMessage.value.trim());
};

// 处理 UI 组件点击
const handleThumbnailClick = (component: UIComponentData) => {
  addUIComponent(component);
};

// 初始化
onMounted(() => {
  loadSessions();

  // 检查是否有从 home 页面传递过来的初始数据
  const chatData = chatCacheStore.getChatData();
  const message = chatData?.message;
  if (message) {
    chatCacheStore.clearChatData();
    inputMessage.value = message;

    nextTick(() => {
      handleSubmitMessage();
    });
  }
});
</script>

<style scoped lang="scss">
.chat-container {
  display: flex;
  height: calc(100vh - var(--navbar-height) - 40px);
  gap: 24px;
}

.chat-main {
  flex: 1.6;
  display: flex;
  flex-direction: column;
  background: white;
  border-radius: 15px;
  height: 100%;
  transition: flex 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);

  // Welcome Screen - 上下居中
  .welcome-screen {
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 20px;
    gap: 20px;

    .welcome-actions {
      display: flex;
      gap: 12px;
    }
  }

  // Chat Screen - 聊天界面
  .chat-screen {
    height: 100%;
    display: flex;
    flex-direction: column;

    // 消息容器 - 占满剩余空间，内部滚动
    .messages-container {
      flex: 1;
      overflow: hidden;
      padding: 20px;
    }

    // 发送器容器 - 沉底固定
    .sender-container {
      padding: 20px;
      background: #fff;
      flex-shrink: 0;
    }
  }
}
</style>
