import type { ApiResponse } from '../request/types';

// 消息角色枚举（对应后端 MessageRole.java）
export enum MessageRole {
  USER = 'USER',
  ASSISTANT = 'ASSISTANT',
  SYSTEM = 'SYSTEM',
  FUNCTION = 'FUNCTION'
}

// 会话状态枚举（对应后端 SessionStatus.java）
export enum SessionStatus {
  ACTIVE = 'ACTIVE',
  PAUSED = 'PAUSED',
  COMPLETED = 'COMPLETED',
  CLOSED = 'CLOSED'
}

// UI 组件类型枚举
export enum UIComponentType {
  EMPLOYEE_INFO = 'employee-info',
  ENTITY_INFO = 'entity-info',
  CLIENT_INFO = 'client-info',
  VENDOR_INFO = 'vendor-info'
}

// 工具数据接口
export interface ToolData {
  name?: string;
  email?: string;
  status?: string;
  position?: string;
  department?: string;
  employeeId?: string;
  employeeNumber?: string;
  nationalityName?: string;
  workLocationName?: string;
  [key: string]: any; // 允许其他自定义字段
}

// UI 组件数据接口
export interface UIComponentData {
  id?: string;
  componentType: UIComponentType | string;
  componentData: {
    toolData: ToolData;
    toolName: string;
    serverName: string;
  };
  timestamp: string;
}

// ChatMessageOutput（对应后端 ChatMessageOutput.java）
export interface ChatMessageOutput {
  id: string;
  sessionId: string;
  role: MessageRole;
  content: string;
  functionCall?: Record<string, any> | null;
  parentMessageId?: string | null;
  timestamp: string;
  uiComponents?: UIComponentData[];
}

// ChatSessionOutput（对应后端 ChatSessionOutput.java）
export interface ChatSessionOutput {
  id: string;
  title: string;
  status: SessionStatus;
  userId: string;
  messageCount: number;
  createdAt: string;
  updatedAt: string;
  messages?: ChatMessageOutput[];
}

// 创建会话输入（对应后端 CreateChatSessionInput）
export interface CreateChatSessionInput {
  title: string;
  userId: string;
}

// 发送消息输入（对应后端 SendMessageInput）
export interface SendMessageInput {
  sessionId: string;
  content: string;
  userId?: string;
}

// 列表查询（可选，后端支持 userId 过滤）
export interface ChatSessionListParams {
  userId?: string;
}

// API 响应类型别名
export type ChatSessionResponse = ApiResponse<ChatSessionOutput>;
export type ChatSessionListResponse = ApiResponse<ChatSessionOutput[]>;
export type ChatMessageListResponse = ApiResponse<ChatMessageOutput[]>;

// SSE 流式聊天相关类型

// 流事件类型 - 代表每个流的消息
export type StreamEventType = 'status' | 'message' | 'error' | 'completed' | 'ui_component';

// 聊天流状态
export type ChatStreamStatus = 'processing' | 'thinking' | 'completed' | 'error';

// 流事件包装类型 - data 里是具体的消息
export interface StreamEvent {
  type: StreamEventType;
  data?: StreamMessage;
}

// 具体的消息类型 - 聊天消息
export interface ChatStreamMessage {
  type: 'message';
  contentType?: 'user' | 'assistant';
  content: string;
  sessionId: string;
  timestamp: string;
}

// 具体的消息类型 - UI组件消息
export interface UIComponentStreamMessage {
  type: 'ui_component';
  content: string;
  uiComponent: string;
  data: any;
  sessionId: string;
  timestamp: string;
}

// 抽象的消息基类
export type StreamMessage = ChatStreamMessage | UIComponentStreamMessage | any;

// 快速提示词接口
export interface QuickPrompt {
  id: string;
  content: string;
  order: number;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}
