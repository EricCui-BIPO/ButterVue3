import request from '../../core/request';
import type { ApiResponse } from '@I0/shared/types';
import type {
  ChatSessionOutput,
  ChatMessageOutput,
  CreateChatSessionInput,
  ChatSessionListParams,
  QuickPrompt,
  StreamEvent
} from '@I0/shared/types';
import { fetchAdapter } from '../../core/fetchAdapter';

/**
 * Chat API - 对应后端 ChatController
 */
class ChatAPI {
  private readonly basePath = '/v1/chat';

  /** 创建会话 */
  async createSession(data: CreateChatSessionInput) {
    return request.post<ChatSessionOutput>(`${this.basePath}/sessions`, data).then(res => res.data);
  }

  /** 获取会话详情 */
  async getSession(sessionId: string): Promise<ApiResponse<ChatSessionOutput>> {
    return request.get<ChatSessionOutput>(`${this.basePath}/sessions/${sessionId}`);
  }

  /** 获取会话消息列表 */
  async getSessionMessages(sessionId: string): Promise<ApiResponse<ChatMessageOutput[]>> {
    return request.get<ChatMessageOutput[]>(`${this.basePath}/sessions/${sessionId}/messages`);
  }

  /** 获取会话（包含消息） */
  async getSessionWithMessages(id: string): Promise<ApiResponse<ChatSessionOutput>> {
    return request.get<ChatSessionOutput>(`${this.basePath}/sessions/${id}/with-messages`);
  }

  /** 查询会话列表（支持 userId 过滤） */
  async getSessions(params: ChatSessionListParams = {}): Promise<ApiResponse<ChatSessionOutput[]>> {
    return request.get<ChatSessionOutput[]>(`${this.basePath}/sessions`, params);
  }

  /** 删除会话 */
  async deleteSession(id: string): Promise<ApiResponse<void>> {
    return request.delete<void>(`${this.basePath}/sessions/${id}`);
  }

  /**
   * 打开流式连接用于实时回复
   * 优化内存使用，减少字符串操作，正确解析 SSE 格式
   */
  async openStream(params: {
    sessionId: string;
    message: string;
    userId?: string;
    showThinking?: boolean;
    typingSpeed?: number;
  }): Promise<ReadableStream<StreamEvent>> {
    const response: any = await request.get(`${this.basePath}/stream`, params, {
      headers: {
        Accept: 'text/event-stream',
        'Cache-Control': 'no-cache'
      },
      responseType: 'stream',
      adapter: fetchAdapter as any
    });

    const stream = response as ReadableStream;
    const reader = stream.getReader();

    return new ReadableStream<StreamEvent>({
      start(controller) {
        let pendingEventType = '';
        let pendingData = '';

        const createStreamEvent = (eventType: string, data: string): StreamEvent => {
          const mappedType: any = eventType || 'message';
          const eventData = JSON.parse(data);
          return { type: mappedType, data: eventData };
        };

        const flushPending = () => {
          if (pendingEventType && pendingData) {
            const event = createStreamEvent(pendingEventType, pendingData);
            controller.enqueue(event);
            pendingEventType = '';
            pendingData = '';
          }
        };

        const processLine = (line: string) => {
          const trimmed = line.trim();
          if (!trimmed) return;

          if (trimmed.startsWith('event:')) {
            flushPending();
            pendingEventType = trimmed.substring(6).trim();
          } else if (trimmed.startsWith('data:')) {
            pendingData = trimmed.substring(5).trim();
            flushPending();
          }
        };

        const read = async () => {
          const decoder = new TextDecoder();
          let buffer = '';

          try {
            // eslint-disable-next-line no-constant-condition
            while (true) {
              const { done, value } = await reader.read();

              if (done) {
                // Process any remaining data in buffer
                if (buffer.trim()) {
                  processLine(buffer);
                }
                flushPending();
                controller.close();
                break;
              }

              const chunk = decoder.decode(value, { stream: true });
              buffer += chunk;

              // Process complete lines
              let newlineIndex;
              while ((newlineIndex = buffer.indexOf('\n')) !== -1) {
                const line = buffer.substring(0, newlineIndex);
                buffer = buffer.substring(newlineIndex + 1);
                processLine(line);
              }
            }
          } catch (error) {
            controller.error(error);
          } finally {
            reader.releaseLock();
          }
        };

        read();
      },

      cancel() {
        reader.releaseLock();
      }
    });
  }

  /**
   * 获取用户的所有活跃会话
   */
  async getActiveSessions(userId?: string): Promise<ApiResponse<ChatSessionOutput[]>> {
    return this.getSessions({ userId });
  }

  /**
   * 获取快速提示词列表
   */
  async getQuickPrompts(): Promise<QuickPrompt[]> {
    return request.get<QuickPrompt[]>(`${this.basePath}/quick-prompts`).then(res => res.data || []);
  }
}

export const chatAPI = new ChatAPI();
