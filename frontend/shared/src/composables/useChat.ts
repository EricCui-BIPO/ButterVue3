import { chatAPI } from '@I0/shared/api';
import {
  MessageRole,
  type ChatMessageOutput,
  type ChatSessionOutput,
  type ChatStreamMessage,
  type UIComponentData,
  type StreamEvent
} from '@I0/shared/types';
import { createSharedComposable } from '@vueuse/core';
import { ElMessage } from 'element-plus';
import { computed, ref } from 'vue';
import { useAsyncAction } from './useAsyncAction';
import { useStreamReader } from './useStreamReader';
import { useChatUIComponents } from './useChatUIComponents';
import { useChatSidebarStore } from '@I0/shared/stores';

function createUserMessage(content: string, sessionId: string): ChatMessageOutput {
  return {
    id: Date.now().toString(),
    sessionId,
    role: MessageRole.USER,
    content,
    timestamp: new Date().toISOString()
  };
}

function createAssistantMessage(content: string, sessionId: string): ChatMessageOutput {
  return {
    id: Date.now().toString(),
    sessionId,
    role: MessageRole.ASSISTANT,
    content,
    timestamp: new Date().toISOString()
  };
}

export const useChat = createSharedComposable(() => {
  const userId = ref<string>('current-user');
  const inputMessage = ref<string>('');

  // 状态管理
  const sessions = ref<ChatSessionOutput[]>([]);
  const currentSession = ref<ChatSessionOutput | null>(null);

  const messages = ref<ChatMessageOutput[]>([]);

  const isSending = ref(false);
  const currentAssistantMessage = ref<ChatMessageOutput | null>(null);

  // 使用UI组件管理hook
  const uiComponentsManager = useChatUIComponents();

  // 侧边栏状态管理
  const chatSidebarStore = useChatSidebarStore();

  // 使用流读取器管理流
  const {
    isReading: isStreaming,
    read: readStream,
    cancel: stopStreaming
  } = useStreamReader<StreamEvent>();

  // 获取所有会话
  const [loadSessions, isLoadingSessions] = useAsyncAction(async () => {
    const { data } = await chatAPI.getSessions({ userId: userId.value });
    sessions.value = data || [];
  });

  // 删除会话
  const [deleteSession] = useAsyncAction(async (sessionId: string) => {
    await chatAPI.deleteSession(sessionId);

    // 从列表中移除
    const index = sessions.value.findIndex(s => s.id === sessionId);
    if (index > -1) {
      sessions.value.splice(index, 1);
    }

    // 如果删除的是当前会话，清空状态
    if (currentSession.value?.id === sessionId) {
      currentSession.value = null;
      messages.value = [];
    }

    ElMessage.success('删除成功');
  });

  // 选择会话
  const selectSession = async (session: ChatSessionOutput) => {
    currentSession.value = session;
    await loadSessionMessages(session.id);
  };

  // 清空当前会话（回到默认状态）
  const clearCurrentSession = () => {
    if (currentSession.value) {
      currentSession.value = null;
      messages.value = [];
      inputMessage.value = '';
    }
  };

  // 加载会话消息
  const [loadSessionMessages, isLoadingMessages] = useAsyncAction(async (sessionId: string) => {
    const res = await chatAPI.getSessionWithMessages(sessionId);
    messages.value = res.data.messages || [];
  });

  /**
   * 处理流式消息
   */
  const handleStreamEvents = async (sessionId: string, stream: ReadableStream<StreamEvent>) => {
    function cleanup() {
      currentAssistantMessage.value = null;
    }
    return readStream(stream, {
      onData: (event: StreamEvent) => {
        console.log('Stream event:', event.type, event.data);

        switch (event.type) {
          case 'message': {
            const messageData = event.data as ChatStreamMessage;
            if (messageData.contentType === 'assistant') {
              if (!currentAssistantMessage.value) {
                currentAssistantMessage.value = createAssistantMessage(
                  messageData.content,
                  sessionId
                );
                messages.value.push(currentAssistantMessage.value);
              } else {
                currentAssistantMessage.value.content += messageData.content;
              }
            } else if (messageData.contentType === 'user') {
              const userMessage = createUserMessage(messageData.content, sessionId);
              messages.value.push(userMessage);
              inputMessage.value = '';

              currentAssistantMessage.value = createAssistantMessage('', sessionId);
              messages.value.push(currentAssistantMessage.value);
            }
            break;
          }
          case 'status': {
            break;
          }
          case 'ui_component': {
            // 检查是否是第一个UI组件并管理侧边栏状态
            const isFirstComponent = uiComponentsManager.uiComponents.value.length === 0;
            if (isFirstComponent) {
              chatSidebarStore.ensureCollapsed();
            }

            // 优先往 currentAssistantMessage 里添加 uiComponents 信息
            if (currentAssistantMessage.value) {
              if (!currentAssistantMessage.value.uiComponents) {
                currentAssistantMessage.value.uiComponents = [];
              }
              // 将流消息转换为 UIComponentData 格式并添加到消息中
              const uiComponentData: UIComponentData = {
                componentType: event.data.uiComponent,
                componentData: {
                  toolData: event.data.data?.toolData || event.data.data,
                  toolName: event.data.data?.toolName || '',
                  serverName: event.data.data?.serverName || ''
                },
                timestamp: event.data.timestamp
              };
              currentAssistantMessage.value.uiComponents.unshift(uiComponentData);

              // 使用标准方法添加到管理器
              setTimeout(() => {
                uiComponentsManager.addUIComponent(uiComponentData);
              }, 500);
            }
            break;
          }
          case 'completed': {
            cleanup();
            break;
          }
          case 'error': {
            cleanup();
            break;
          }
        }
      },
      onComplete: () => {
        cleanup();
      },
      onError: error => {
        console.error('Stream error:', error);
        cleanup();
      },
      onAbort: () => {
        console.log('Stream aborted by user');
        cleanup();
      }
    });
  };

  // 处理发送消息 - 智能模式：无会话时自动创建，有会话时直接发送
  const handleSendMessage = async (message: string) => {
    if (isSending.value || isStreaming.value) return;
    if (!message) return;

    let currentSessionId = currentSession.value?.id || '';
    let stream: ReadableStream<StreamEvent> | null = null;

    try {
      isSending.value = true;
      if (!currentSession.value) {
        const title = message.substring(0, 50);
        currentSession.value = await chatAPI.createSession({ title, userId: userId.value });
        currentSessionId = currentSession.value.id;
        sessions.value.unshift(currentSession.value);
      }
      stream = await chatAPI.openStream({
        sessionId: currentSessionId!,
        message: message,
        userId: userId.value
      });
    } catch (error) {
      console.error('Failed to open stream:', error);
      return;
    } finally {
      isSending.value = false;
    }

    // 使用新的流处理逻辑
    await handleStreamEvents(currentSessionId!, stream);
  };

  // 计算属性
  const hasSessions = computed(() => sessions.value.length > 0);
  const currentSessionTitle = computed(() => currentSession.value?.title || '');
  const messageCount = computed(() => messages.value.length);

  const addUIComponent = (uiComponentData: UIComponentData) => {
    const isFirstComponent = uiComponentsManager.uiComponents.value.length === 0;
    if (isFirstComponent) {
      chatSidebarStore.ensureCollapsed();
    }
    uiComponentsManager.addUIComponent(uiComponentData);
  };

  return {
    // 状态
    inputMessage,
    sessions,
    currentSession,
    currentAssistantMessage,
    uiComponents: uiComponentsManager.uiComponents,
    messages,
    isLoadingSessions,
    isLoadingMessages,
    isSending,
    isStreaming,

    // 计算属性
    hasSessions,
    currentSessionTitle,
    messageCount,

    // 方法
    loadSessions,
    deleteSession,
    selectSession,
    loadSessionMessages,
    handleSendMessage,
    stopStreaming,
    clearCurrentSession,

    // UI组件管理方法
    addUIComponent: addUIComponent,
    removeUIComponent: uiComponentsManager.removeUIComponent
  };
});
