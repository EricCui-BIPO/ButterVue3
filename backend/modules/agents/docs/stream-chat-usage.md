# Agents模块流式聊天使用指南

## 概述

agents模块是系统中的**智能对话与业务函数调用核心模块**，基于Clean Architecture + DDD架构设计，提供完整的AI对话、业务函数管理、工具调用等功能。流式聊天功能通过 Server-Sent Events (SSE) 提供实时的AI对话体验，用户可以实时看到AI的思考过程和回复内容，并支持AI调用业务函数执行具体操作。

### 核心特性
- **实时流式对话**：基于SSE的实时AI对话，支持打字效果和状态显示
- **业务函数集成**：AI可调用注册的业务API执行具体操作
- **UI组件引用**：AI回复中可包含前端UI组件引用，增强交互体验
- **多AI服务支持**：支持BigModel等多个AI服务提供商
- **MCP协议支持**：集成Model Context Protocol，支持工具调用和扩展
- **会话管理**：完整的聊天会话生命周期管理

## 业务需求与架构设计

### 业务需求分析

#### 1. 核心业务场景
- **智能客服对话**：用户通过自然语言与AI助手进行对话，获得即时响应
- **业务操作自动化**：AI理解用户意图并调用相应的业务API执行操作
- **数据查询与分析**：AI帮助用户查询和分析系统数据
- **工作流程指导**：AI为用户提供操作指导和流程建议

#### 2. 用户角色与权限
- **普通用户**：可创建会话、发送消息、查看历史记录
- **管理员**：可查看所有会话、管理业务函数配置
- **系统开发者**：可注册新的业务函数、配置AI服务

#### 3. 业务规则
- 会话状态管理：ACTIVE、PAUSED、COMPLETED、CLOSED
- 消息类型支持：USER、ASSISTANT、SYSTEM、FUNCTION
- 函数调用权限：基于用户角色的函数访问控制
- 内容审核：支持敏感内容过滤和安全检查

### 技术架构设计

#### 1. 整体架构
```
┌─────────────────────────────────────────────────────────────┐
│                    Gateway Layer (外层)                      │
├─────────────────────────────────────────────────────────────┤
│  ChatController  │  RepositoryImpl  │  ACL Adapters         │
├─────────────────────────────────────────────────────────────┤
│                 Application Layer (中层)                     │
├─────────────────────────────────────────────────────────────┤
│  StreamChatUseCase  │  AIConversationCoordinator  │  Services│
├─────────────────────────────────────────────────────────────┤
│                  Domain Layer (内层)                         │
├─────────────────────────────────────────────────────────────┤
│  ChatSession  │  ChatMessage  │  BusinessFunction  │  Enums  │
└─────────────────────────────────────────────────────────────┘
```

#### 2. 核心组件设计

**Domain层（业务核心）**：
- `ChatSession` - 聊天会话实体，管理会话状态和消息列表
- `ChatMessage` - 聊天消息实体，支持多种消息类型和UI组件引用
- `BusinessFunction` - 业务函数定义，描述可被AI调用的API
- `MessageRole`、`SessionStatus` - 枚举，包含状态转换逻辑
- `ChatSessionRepository`、`ChatMessageRepository` - 数据访问抽象

**Application层（业务编排）**：
- `StreamChatUseCase` - 流式聊天核心逻辑
- `AIConversationCoordinator` - AI对话协调器
- `AIRequestService` - AI请求处理服务
- `ToolExecutionService` - 工具执行服务
- `BusinessFunctionRegistry` - 业务函数注册表

**Gateway层（外部集成）**：
- `ChatController` - REST控制器，提供API和SSE接口
- `ChatSessionRepositoryImpl` - 数据访问实现
- `EntityFunctionAdapter` - 实体函数适配器
- `LocationSearchAdapter` - 位置搜索适配器

#### 3. 数据流设计
```
用户请求 → ChatController → StreamChatUseCase → AIConversationCoordinator
    ↓
AI服务调用 ← AIRequestService ← ToolExecutionService ← BusinessFunctionRegistry
    ↓
函数执行结果 → UIComponentReference → 消息存储 → SSE流式响应
```

### 技术栈与依赖

#### 1. 核心技术栈
- **框架**：Spring Framework（严格遵循Clean Architecture）
- **持久化**：MyBatis-Plus + MySQL
- **网络通信**：OkHttp3 + Server-Sent Events
- **序列化**：Jackson JSON
- **AI集成**：BigModel API + 自定义适配器

#### 2. 关键依赖关系
```gradle
// Domain层 - 纯净的领域逻辑
implementation project(':frameworks:domain.core')

// Application层 - 业务编排
implementation project(':frameworks:domain.core')
implementation project(':frameworks:persistence.spring')

// Gateway层 - 外部集成
implementation project(':frameworks:gateway.context')
implementation project(':agents:domain')
implementation project(':agents:application')

// 跨领域依赖（通过ACL）
implementation project(':entity:application')
implementation project(':talent:application')
implementation project(':location:application')
```

## API 接口

### 流式聊天端点

**URL**: `GET /api/v1/chat/stream`

**参数**:
- `sessionId` (必需): 会话ID
- `message` (必需): 用户消息内容
- `userId` (可选): 用户ID
- `showThinking` (可选, 默认true): 是否显示AI思考状态
- `typingSpeed` (可选, 默认100): 打字速度（毫秒/字符）

**示例请求**:
```javascript
// 基本流式聊天
fetch('/api/v1/chat/stream?sessionId=123&message=你好&userId=user1', {
    headers: {
        'Accept': 'text/event-stream',
        'Cache-Control': 'no-cache'
    }
})

// 自定义参数的流式聊天
fetch('/api/v1/chat/stream?sessionId=123&message=创建一个实体&userId=user1&showThinking=false&typingSpeed=50', {
    headers: {
        'Accept': 'text/event-stream'
    }
})
```

### 完整API接口列表

#### 会话管理
```http
POST   /api/v1/chat/sessions                    # 创建会话
GET    /api/v1/chat/sessions/{id}                # 获取会话详情
GET    /api/v1/chat/sessions                    # 查询会话列表
DELETE /api/v1/chat/sessions/{id}               # 删除会话
```

#### 消息管理
```http
GET    /api/v1/chat/sessions/{sessionId}/messages     # 获取消息列表
POST   /api/v1/chat/sessions/{sessionId}/messages     # 发送消息
GET    /api/v1/chat/sessions/{id}/with-messages      # 获取会话详情（含消息）
```

#### 辅助功能
```http
GET    /api/v1/chat/quick-prompts                  # 获取快速提示词
GET    /api/v1/chat/stream/timeout                 # SSE超时测试
```

## 业务函数系统

### 函数注册机制

agents模块支持动态注册业务函数，AI可以根据用户意图调用相应的业务API执行具体操作。

#### 1. 函数定义结构
```java
BusinessFunction function = BusinessFunction.builder()
    .name("create_entity")
    .description("创建一个新的业务实体")
    .parameter("name", "string", "实体名称，长度2-50个字符")
    .parameter("type", "string", "实体类型")
    .parameter("description", "string", "实体描述", PropertyDefinition.builder()
        .type("string")
        .description("详细描述")
        .defaultValue("")
        .build())
    .required("name")
    .required("type")
    .handler(arguments -> {
        // 执行业务逻辑
        String name = (String) arguments.get("name");
        String type = (String) arguments.get("type");

        // 调用相应的业务服务
        EntityService.createEntity(name, type, arguments);

        return BusinessFunction.FunctionCallResult.success(
            "实体创建成功",
            Map.of("entityId", "123", "name", name),
            createEntitySuccessUI(name)
        );
    })
    .build();
```

#### 2. 已注册的业务函数

**实体管理函数**：
- `create_entity` - 创建业务实体
- `find_entity_by_code` - 根据代码查询实体
- `update_entity` - 更新实体信息
- `list_entities` - 获取实体列表

**员工管理函数**：
- `find_employee_by_id` - 根据ID查询员工
- `search_employees` - 搜索员工信息
- `update_employee_status` - 更新员工状态

**位置管理函数**：
- `search_locations` - 搜索位置信息
- `get_location_details` - 获取位置详情

### UI组件引用系统

AI回复中可包含前端UI组件引用，增强用户交互体验。

#### UI组件类型
```json
{
  "type": "button",
  "props": {
    "label": "查看详情",
    "action": "navigate",
    "target": "/entities/123"
  },
  "style": {
    "variant": "primary",
    "size": "medium"
  }
}
```

#### 支持的UI组件
- **按钮 (button)**: 触发导航或操作
- **卡片 (card)**: 展示结构化信息
- **列表 (list)**: 显示数据列表
- **表单 (form)**: 收集用户输入
- **模态框 (modal)**: 弹出式交互

### MCP协议支持

agents模块集成Model Context Protocol (MCP)，支持标准化的工具调用和扩展。

#### 1. MCP服务器管理
```java
@Component
public class MCPProtocolManager {

    public void registerServer(MCPServer server) {
        // 注册MCP服务器
        servers.put(server.getName(), server);
    }

    public List<MCPTool> getAvailableTools() {
        // 获取所有可用工具
        return servers.values().stream()
            .flatMap(server -> server.getTools().stream())
            .collect(Collectors.toList());
    }
}
```

#### 2. 工具调用流程
1. **工具发现**：AI获取当前可用的业务函数工具
2. **参数验证**：验证工具调用参数的完整性和有效性
3. **工具执行**：执行相应的业务逻辑
4. **结果处理**：将执行结果转换为AI可理解的格式

## SSE 事件格式

### 事件类型

#### 1. 状态事件 (status)
```json
{
  "type": "status",
  "content": "processing",
  "sessionId": "123",
  "timestamp": "2025-01-20T10:30:00Z"
}
```

**状态类型**:
- `processing`: 开始处理消息
- `thinking`: AI正在思考（如果启用）
- `completed`: 处理完成

#### 2. 消息事件 (message)
```json
{
  "type": "message",
  "contentType": "user",
  "content": "你好",
  "sessionId": "123",
  "timestamp": "2025-01-20T10:30:01Z",
  "uiComponents": [
    {
      "id": "welcome-card",
      "type": "card",
      "props": {
        "title": "欢迎使用AI助手",
        "description": "我可以帮助您管理实体、查询员工信息等"
      }
    }
  ]
}
```

**消息类型**:
- `user`: 用户消息
- `assistant`: AI助手回复
- `system`: 系统消息
- `function`: 函数调用结果

#### 3. 函数调用事件 (function_call)
```json
{
  "type": "function_call",
  "functionName": "create_entity",
  "arguments": {
    "name": "测试实体",
    "type": "USER"
  },
  "sessionId": "123",
  "timestamp": "2025-01-20T10:30:02Z"
}
```

#### 4. 函数结果事件 (function_result)
```json
{
  "type": "function_result",
  "functionName": "create_entity",
  "result": {
    "success": true,
    "data": {
      "entityId": "123",
      "name": "测试实体"
    },
    "uiComponent": {
      "type": "button",
      "props": {
        "label": "查看详情",
        "target": "/entities/123"
      }
    }
  },
  "sessionId": "123",
  "timestamp": "2025-01-20T10:30:03Z"
}
```

#### 5. 错误事件 (error)
```json
{
  "type": "error",
  "content": "处理失败: 会话不存在",
  "sessionId": "123",
  "timestamp": "2025-01-20T10:30:02Z"
}
```

#### 6. 完成事件 (completed)
```json
{
  "type": "completed",
  "content": "completed",
  "sessionId": "123",
  "timestamp": "2025-01-20T10:30:05Z"
}
```

## 前端集成示例

### JavaScript 客户端

```javascript
class StreamChatClient {
    constructor(options = {}) {
        this.defaultOptions = {
            showThinking: true,
            typingSpeed: 100,
            timeout: 60000,
            ...options
        };
    }

    // 创建流式聊天连接
    createStreamChat(sessionId, message, userId, options = {}) {
        const config = { ...this.defaultOptions, ...options };
        const url = this.buildUrl(sessionId, message, userId, config);

        const eventSource = new EventSource(url);

        // 设置超时处理
        const timeoutId = setTimeout(() => {
            eventSource.close();
            this.onError(new Error('连接超时'));
        }, config.timeout);

        eventSource.onmessage = (event) => {
            clearTimeout(timeoutId);
            this.onMessage(JSON.parse(event.data));
        };

        eventSource.onerror = (error) => {
            clearTimeout(timeoutId);
            this.onError(error);
            eventSource.close();
        };

        return eventSource;
    }

    buildUrl(sessionId, message, userId, config) {
        const params = new URLSearchParams({
            sessionId: sessionId,
            message: message,
            userId: userId || '',
            showThinking: config.showThinking,
            typingSpeed: config.typingSpeed
        });

        return `/api/v1/chat/stream?${params.toString()}`;
    }

    // 事件处理器
    onMessage(data) {
        switch(data.type) {
            case 'status':
                this.onStatusChange(data);
                break;
            case 'message':
                this.onMessageReceived(data);
                break;
            case 'function_call':
                this.onFunctionCall(data);
                break;
            case 'function_result':
                this.onFunctionResult(data);
                break;
            case 'error':
                this.onError(data);
                break;
            case 'completed':
                this.onCompleted(data);
                break;
        }
    }

    onStatusChange(event) {
        console.log('状态变更:', event.content);
        this.updateStatusIndicator(event.content);
    }

    onMessageReceived(event) {
        console.log('消息接收:', event.contentType, event.content);

        if (event.contentType === 'user') {
            this.appendUserMessage(event.content);
        } else if (event.contentType === 'assistant') {
            this.appendAssistantMessage(event.content, event.uiComponents);
        }
    }

    onFunctionCall(event) {
        console.log('函数调用:', event.functionName, event.arguments);
        this.showFunctionCalling(event.functionName);
    }

    onFunctionResult(event) {
        console.log('函数结果:', event.result);
        this.hideFunctionCalling();

        if (event.result.success) {
            this.appendFunctionResult(event.result);
        } else {
            this.showError(`函数执行失败: ${event.result.error}`);
        }
    }

    onError(error) {
        console.error('流式聊天错误:', error);
        this.showError(error.content || '连接错误，请重试');
    }

    onCompleted(event) {
        console.log('流式聊天完成');
        this.hideThinkingState();
        this.enableInput();
    }

    // UI更新方法
    updateStatusIndicator(status) {
        const indicator = document.getElementById('status-indicator');
        if (indicator) {
            indicator.textContent = this.getStatusText(status);
            indicator.className = `status ${status}`;
        }
    }

    appendUserMessage(content) {
        const messagesContainer = document.getElementById('messages');
        const messageElement = this.createMessageElement('user', content);
        messagesContainer.appendChild(messageElement);
        this.scrollToBottom(messagesContainer);
    }

    appendAssistantMessage(content, uiComponents = []) {
        const messagesContainer = document.getElementById('messages');
        const messageElement = this.createMessageElement('assistant', content);

        // 添加UI组件
        if (uiComponents && uiComponents.length > 0) {
            const componentsContainer = this.createUIComponentsContainer(uiComponents);
            messageElement.appendChild(componentsContainer);
        }

        messagesContainer.appendChild(messageElement);
        this.scrollToBottom(messagesContainer);
    }

    appendFunctionResult(result) {
        const messagesContainer = document.getElementById('messages');
        const resultElement = this.createFunctionResultElement(result);
        messagesContainer.appendChild(resultElement);
        this.scrollToBottom(messagesContainer);
    }

    createMessageElement(type, content) {
        const div = document.createElement('div');
        div.className = `message ${type}`;

        const contentDiv = document.createElement('div');
        contentDiv.className = 'content';
        contentDiv.textContent = content;

        div.appendChild(contentDiv);
        return div;
    }

    createUIComponentsContainer(uiComponents) {
        const container = document.createElement('div');
        container.className = 'ui-components';

        uiComponents.forEach(component => {
            const element = this.renderUIComponent(component);
            if (element) {
                container.appendChild(element);
            }
        });

        return container;
    }

    renderUIComponent(component) {
        switch(component.type) {
            case 'button':
                return this.createButton(component);
            case 'card':
                return this.createCard(component);
            case 'list':
                return this.createList(component);
            default:
                console.warn('未知的UI组件类型:', component.type);
                return null;
        }
    }

    createButton(component) {
        const button = document.createElement('button');
        button.className = `btn btn-${component.style?.variant || 'primary'}`;
        button.textContent = component.props.label;

        button.addEventListener('click', () => {
            if (component.props.action === 'navigate') {
                window.location.href = component.props.target;
            }
        });

        return button;
    }

    createCard(component) {
        const card = document.createElement('div');
        card.className = 'card';

        const title = document.createElement('h3');
        title.textContent = component.props.title;

        const description = document.createElement('p');
        description.textContent = component.props.description;

        card.appendChild(title);
        card.appendChild(description);

        return card;
    }

    showFunctionCalling(functionName) {
        const indicator = document.getElementById('function-calling');
        if (indicator) {
            indicator.textContent = `正在执行: ${functionName}`;
            indicator.style.display = 'block';
        }
    }

    hideFunctionCalling() {
        const indicator = document.getElementById('function-calling');
        if (indicator) {
            indicator.style.display = 'none';
        }
    }

    getStatusText(status) {
        const statusMap = {
            'processing': '正在处理...',
            'thinking': 'AI正在思考...',
            'completed': '处理完成'
        };
        return statusMap[status] || status;
    }

    scrollToBottom(container) {
        container.scrollTop = container.scrollHeight;
    }

    // 其他UI方法...
    showError(message) {
        // 显示错误信息
    }

    hideThinkingState() {
        // 隐藏思考状态
    }

    enableInput() {
        // 启用输入框
    }
}

// 使用示例
const chatClient = new StreamChatClient({
    showThinking: true,
    typingSpeed: 80,
    timeout: 30000
});

// 启动聊天
function startChat(sessionId, message, userId) {
    const eventSource = chatClient.createStreamChat(sessionId, message, userId);

    // 可以添加自定义事件处理
    chatClient.onError = (error) => {
        console.error('自定义错误处理:', error);
        // 重试逻辑
        setTimeout(() => {
            if (shouldRetry()) {
                startChat(sessionId, message, userId);
            }
        }, 3000);
    };

    return eventSource;
}
```

### React 组件示例

```jsx
import React, { useState, useEffect, useRef } from 'react';

const StreamChat = ({ sessionId, userId }) => {
    const [messages, setMessages] = useState([]);
    const [status, setStatus] = useState('');
    const [isTyping, setIsTyping] = useState(false);
    const [input, setInput] = useState('');
    const eventSourceRef = useRef(null);

    const connectStream = (sessionId, message) => {
        if (eventSourceRef.current) {
            eventSourceRef.current.close();
        }

        const url = `/api/v1/chat/stream?sessionId=${sessionId}&message=${encodeURIComponent(message)}&userId=${userId}`;
        eventSourceRef.current = new EventSource(url);

        eventSourceRef.current.onmessage = (event) => {
            const data = JSON.parse(event.data);

            switch (data.type) {
                case 'status':
                    setStatus(data.content);
                    if (data.content === 'thinking') {
                        setIsTyping(true);
                    }
                    break;
                case 'message':
                    if (data.contentType === 'assistant') {
                        setMessages(prev => [...prev, {
                            type: 'assistant',
                            content: data.content,
                            timestamp: data.timestamp
                        }]);
                    }
                    break;
                case 'error':
                    setStatus(`错误: ${data.content}`);
                    break;
                case 'completed':
                    setStatus('');
                    setIsTyping(false);
                    eventSourceRef.current.close();
                    eventSourceRef.current = null;
                    break;
            }
        };

        eventSourceRef.current.onerror = (error) => {
            console.error('Stream error:', error);
            setStatus('连接错误，请重试');
            if (eventSourceRef.current) {
                eventSourceRef.current.close();
                eventSourceRef.current = null;
            }
            setIsTyping(false);
        };
    };

    const sendMessage = () => {
        if (!input.trim()) return;

        const userMessage = {
            type: 'user',
            content: input.trim(),
            timestamp: new Date().toISOString()
        };

        setMessages(prev => [...prev, userMessage]);

        connectStream(sessionId, input.trim());
        setInput('');
    };

    return (
        <div className="stream-chat">
            <div className="messages">
                {messages.map((msg, index) => (
                    <div key={index} className={`message ${msg.type}`}>
                        <div className="content">{msg.content}</div>
                        <div className="timestamp">{msg.timestamp}</div>
                    </div>
                ))}
            </div>

            {status && (
                <div className="status">
                    {status}
                    {isTyping && <span className="typing-indicator">...</span>}
                </div>
            )}

            <div className="input-area">
                <input
                    type="text"
                    value={input}
                    onChange={(e) => setInput(e.target.value)}
                    onKeyPress={(e) => e.key === 'Enter' && sendMessage()}
                    placeholder="输入消息..."
                    disabled={isTyping}
                />
                <button onClick={sendMessage} disabled={isTyping}>
                    发送
                </button>
            </div>
        </div>
    );
};

export default StreamChat;
```

## 配置管理与最佳实践

### 系统配置

#### 1. AI服务配置
```yaml
# application.yml
agents:
  ai:
    provider: bigmodel
    api-key: ${BIGMODEL_API_KEY}
    base-url: https://open.bigmodel.cn/api/paas/v4
    model: glm-4
    max-tokens: 2000
    temperature: 0.7
    timeout: 30s
```

#### 2. 业务函数配置
```yaml
agents:
  functions:
    enabled: true
    auto-discovery: true
    registries:
      - name: entity-functions
        package: com.i0.entity.application.services
      - name: talent-functions
        package: com.i0.talent.application.services
      - name: location-functions
        package: com.i0.location.application.services
```

#### 3. 流式聊天配置
```yaml
agents:
  stream-chat:
    default-thinking-enabled: true
    default-typing-speed: 100
    max-session-duration: 3600s
    max-message-length: 4000
    rate-limit:
      requests-per-minute: 60
      burst-size: 10
```

### 业务函数配置最佳实践

#### 1. 函数注册规范
```java
@Configuration
public class BusinessFunctionConfiguration {

    @Bean
    @ConditionalOnProperty(name = "agents.functions.enabled", havingValue = "true")
    public BusinessFunctionRegistry businessFunctionRegistry() {
        BusinessFunctionRegistry registry = new BusinessFunctionRegistry();

        // 注册实体管理函数
        registry.register(createEntityFunction());
        registry.register(findEntityFunction());
        registry.register(updateEntityFunction());

        // 注册员工管理函数
        registry.register(findEmployeeFunction());
        registry.register(searchEmployeesFunction());

        return registry;
    }

    private BusinessFunction createEntityFunction() {
        return BusinessFunction.builder()
            .name("create_entity")
            .description("创建新的业务实体")
            .parameter("name", "string", "实体名称，2-50个字符")
            .parameter("type", "string", "实体类型")
            .parameter("description", "string", "实体描述")
            .required("name", "type")
            .handler(this::handleCreateEntity)
            .build();
    }

    private FunctionCallResult handleCreateEntity(Map<String, Object> arguments) {
        try {
            // 参数验证
            validateCreateEntityParams(arguments);

            // 调用业务服务
            CreateEntityInput input = CreateEntityInput.builder()
                .name((String) arguments.get("name"))
                .type((String) arguments.get("type"))
                .description((String) arguments.get("description"))
                .build();

            EntityOutput result = entityService.createEntity(input);

            // 返回成功结果，包含UI组件
            return FunctionCallResult.success(
                "实体创建成功",
                Map.of(
                    "entityId", result.getId(),
                    "entityName", result.getName(),
                    "entityType", result.getType()
                ),
                createEntitySuccessUI(result)
            );

        } catch (Exception e) {
            log.error("创建实体失败", e);
            return FunctionCallResult.failure("创建实体失败: " + e.getMessage());
        }
    }

    private void validateCreateEntityParams(Map<String, Object> arguments) {
        String name = (String) arguments.get("name");
        String type = (String) arguments.get("type");

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("实体名称不能为空");
        }

        if (name.length() < 2 || name.length() > 50) {
            throw new IllegalArgumentException("实体名称长度必须在2-50个字符之间");
        }

        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("实体类型不能为空");
        }
    }
}
```

#### 2. UI组件生成规范
```java
private Object createEntitySuccessUI(EntityOutput result) {
    return Map.of(
        "type", "card",
        "id", "entity-success-card",
        "props", Map.of(
            "title", "实体创建成功",
            "content", String.format("实体 %s 已成功创建", result.getName())
        ),
        "actions", List.of(
            Map.of(
                "type", "button",
                "props", Map.of(
                    "label", "查看详情",
                    "variant", "primary",
                    "action", "navigate",
                    "target", "/entities/" + result.getId()
                )
            ),
            Map.of(
                "type", "button",
                "props", Map.of(
                    "label", "创建另一个",
                    "variant", "outline",
                    "action", "navigate",
                    "target", "/entities/create"
                )
            )
        )
    );
}
```

### 性能优化配置

#### 1. 连接池配置
```yaml
agents:
  http-client:
    max-connections: 100
    max-connections-per-route: 20
    connection-timeout: 10s
    read-timeout: 30s
    write-timeout: 30s
```

#### 2. 缓存配置
```yaml
agents:
  cache:
    function-definitions:
      ttl: 300s
      max-size: 1000
    session-context:
      ttl: 1800s
      max-size: 500
```

#### 3. 监控配置
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,agents
  metrics:
    tags:
      application: agents-module
    export:
      prometheus:
        enabled: true
```

### 安全配置

#### 1. API访问控制
```yaml
security:
  agents:
    rate-limit:
      enabled: true
      requests-per-minute: 60
      burst-size: 10
    authentication:
      required: true
      jwt-secret: ${JWT_SECRET}
    authorization:
      roles:
        - AGENT_USER
        - AGENT_ADMIN
```

#### 2. 内容安全
```yaml
agents:
  content-safety:
    enabled: true
    moderation:
      enabled: true
      provider: internal
    data-privacy:
      mask-pii: true
      log-sanitization: true
```

### 开发环境配置

#### 1. 测试配置
```yaml
# application-test.yml
agents:
  ai:
    mock-enabled: true
    mock-responses:
      greeting: "你好！我是AI助手，很高兴为您服务。"
      function-success: "操作已成功完成。"
  stream-chat:
    default-typing-speed: 10  # 加快测试速度
```

#### 2. 调试配置
```yaml
logging:
  level:
    com.i0.agents: DEBUG
    org.springframework.web.servlet.mvc.method.annotation.SseEmitter: DEBUG
```

### 配置验证

#### 1. 启动时配置验证
```java
@ConfigurationProperties(prefix = "agents")
@Validated
public class AgentsProperties {

    @NotNull
    private AIProperties ai;

    @Valid
    private StreamChatProperties streamChat;

    @Valid
    private FunctionProperties functions;

    // getters and setters...

    @PostConstruct
    public void validateConfiguration() {
        if (ai.getApiKey() == null || ai.getApiKey().isEmpty()) {
            throw new IllegalStateException("AI API key must be configured");
        }

        if (streamChat.getMaxMessageLength() > 8000) {
            log.warn("Max message length is too large, may cause performance issues");
        }
    }
}
```

## 配置选项

### 1. 思考状态显示
- 启用时会显示 "AI正在思考..." 状态
- 提供更好的用户体验
- 稍微增加响应时间

### 2. 打字速度
- 控制AI回复的显示速度
- 模拟真实的打字效果
- 单位：毫秒/字符
- 推荐值：50-150ms

### 3. 完成状态
- 控制是否发送完成事件
- 影响前端的状态管理
- 建议启用

### 4. 会话超时
- 设置SSE连接的最大持续时间
- 防止长时间占用连接资源
- 默认值：60秒

### 5. 函数调用
- 控制是否允许AI调用业务函数
- 影响系统的自动化能力
- 生产环境建议启用

## 错误处理与监控

### 常见错误类型

1. **会话相关错误**
   - 错误信息：`聊天会话不存在或不可交互`
   - HTTP状态码：400 Bad Request
   - 解决方案：创建新会话或检查会话ID

2. **AI服务错误**
   - 错误信息：`AI服务响应超时`、`AI服务暂时不可用`
   - HTTP状态码：503 Service Unavailable
   - 解决方案：检查AI服务配置，稍后重试

3. **业务函数错误**
   - 错误信息：`函数调用失败: [具体错误]`
   - HTTP状态码：400 Bad Request
   - 解决方案：检查函数参数，确保业务服务正常

4. **网络连接错误**
   - 错误信息：`连接错误，请重试`
   - HTTP状态码：500 Internal Server Error
   - 解决方案：检查网络连接和服务器状态

5. **参数验证错误**
   - 错误信息：`消息内容过长`、`参数格式错误`
   - HTTP状态码：400 Bad Request
   - 解决方案：检查输入参数格式和长度限制

### 错误处理最佳实践

#### 1. 前端错误处理
```javascript
class StreamChatClient {
    constructor(options = {}) {
        this.retryCount = 0;
        this.maxRetries = options.maxRetries || 3;
        this.retryDelay = options.retryDelay || 1000;
    }

    onError(error) {
        console.error('流式聊天错误:', error);

        // 根据错误类型进行不同处理
        if (this.isRetryableError(error)) {
            this.handleRetryableError(error);
        } else {
            this.handleNonRetryableError(error);
        }
    }

    isRetryableError(error) {
        const retryableErrors = [
            'AI服务响应超时',
            '连接错误，请重试',
            '服务暂时不可用'
        ];

        return retryableErrors.some(err =>
            error.content && error.content.includes(err)
        );
    }

    handleRetryableError(error) {
        if (this.retryCount < this.maxRetries) {
            this.retryCount++;
            const delay = this.retryDelay * Math.pow(2, this.retryCount - 1);

            console.log(`将在 ${delay}ms 后进行第 ${this.retryCount} 次重试`);

            setTimeout(() => {
                this.showError(`连接失败，正在重试 (${this.retryCount}/${this.maxRetries})...`);
                // 重新连接逻辑
            }, delay);
        } else {
            this.showError('重试失败，请检查网络连接后重试');
            this.enableInput();
        }
    }

    handleNonRetryableError(error) {
        this.showError(error.content || '发生未知错误');
        this.enableInput();
    }
}
```

#### 2. 后端错误处理
```java
@Component
@Slf4j
public class StreamChatErrorHandler {

    public void handleStreamChatError(Exception e, SseEmitter emitter, String sessionId) {
        String errorMessage;

        if (e instanceof AgentsException) {
            errorMessage = e.getMessage();
            log.warn("业务异常 - Session: {}, Error: {}", sessionId, errorMessage);
        } else if (e instanceof TimeoutException) {
            errorMessage = "AI服务响应超时，请稍后再试";
            log.error("AI服务超时 - Session: {}", sessionId);
        } else if (e instanceof ConnectException) {
            errorMessage = "AI服务连接失败，请稍后再试";
            log.error("AI服务连接失败 - Session: {}", sessionId);
        } else {
            errorMessage = "系统内部错误，请联系管理员";
            log.error("未知错误 - Session: {}", sessionId, e);
        }

        // 发送错误事件
        sendErrorEvent(emitter, errorMessage, sessionId);

        // 完成SSE连接
        emitter.complete();
    }

    private void sendErrorEvent(SseEmitter emitter, String errorMessage, String sessionId) {
        try {
            StreamChatEvent errorEvent = StreamChatEvent.error(errorMessage, sessionId);
            emitter.send(SseEmitter.event()
                .name(errorEvent.getEventType())
                .data(errorEvent.toJson()));
        } catch (IOException e) {
            log.error("发送错误事件失败", e);
        }
    }
}
```

### 监控与日志

#### 1. 性能监控
```java
@Component
public class StreamChatMetrics {

    private final MeterRegistry meterRegistry;
    private final Counter chatRequestCounter;
    private final Timer chatResponseTimer;
    private final Counter errorCounter;

    public StreamChatMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.chatRequestCounter = Counter.builder("agents.chat.requests")
            .description("Total chat requests")
            .register(meterRegistry);
        this.chatResponseTimer = Timer.builder("agents.chat.response.time")
            .description("Chat response time")
            .register(meterRegistry);
        this.errorCounter = Counter.builder("agents.chat.errors")
            .description("Chat errors")
            .tag("error_type", "unknown")
            .register(meterRegistry);
    }

    public void recordChatRequest() {
        chatRequestCounter.increment();
    }

    public void recordChatResponse(Duration duration) {
        chatResponseTimer.record(duration);
    }

    public void recordError(String errorType) {
        errorCounter.increment(Tags.of("error_type", errorType));
    }
}
```

#### 2. 日志记录
```java
@Slf4j
@Component
public class StreamChatAuditor {

    public void logChatStart(String sessionId, String userId, String message) {
        log.info("Chat started - SessionId: {}, UserId: {}, MessageLength: {}",
            sessionId, userId, message.length());
    }

    public void logChatComplete(String sessionId, Duration duration, int messageCount) {
        log.info("Chat completed - SessionId: {}, Duration: {}ms, MessageCount: {}",
            sessionId, duration.toMillis(), messageCount);
    }

    public void logFunctionCall(String sessionId, String functionName, Map<String, Object> arguments) {
        log.info("Function called - SessionId: {}, Function: {}, Arguments: {}",
            sessionId, functionName, arguments.keySet());
    }

    public void logFunctionResult(String sessionId, String functionName, boolean success, long duration) {
        log.info("Function result - SessionId: {}, Function: {}, Success: {}, Duration: {}ms",
            sessionId, functionName, success, duration);
    }
}
```

## 性能优化

### 1. 连接管理
- **及时关闭SSE连接**：避免长时间占用连接资源
- **连接池管理**：合理配置HTTP连接池参数
- **超时设置**：设置合理的连接和读取超时时间

```yaml
agents:
  http-client:
    max-connections: 100
    max-connections-per-route: 20
    connection-timeout: 10s
    read-timeout: 30s
    write-timeout: 30s
  stream-chat:
    connection-timeout: 60s
    idle-timeout: 300s
```

### 2. 消息缓存
- **会话上下文缓存**：缓存会话相关的上下文信息
- **函数定义缓存**：缓存业务函数的定义信息
- **AI响应缓存**：对相同问题的响应进行适当缓存

```yaml
agents:
  cache:
    session-context:
      ttl: 1800s
      max-size: 500
    function-definitions:
      ttl: 300s
      max-size: 1000
    ai-responses:
      ttl: 600s
      max-size: 200
```

### 3. 异步处理
- **非阻塞IO**：使用CompletableFuture进行异步处理
- **消息队列**：对于耗时的操作使用消息队列
- **批处理**：对批量操作进行优化

```java
@Async("chatExecutor")
public CompletableFuture<ConversationResult> processChatAsync(
        String sessionId, List<ChatMessage> messages) {

    return CompletableFuture.supplyAsync(() -> {
        // 处理聊天逻辑
        return aiConversationCoordinator.chat(sessionId, messages, null);
    }, chatExecutor);
}
```

### 4. 数据库优化
- **索引优化**：为常用查询字段创建索引
- **分页查询**：大数据量查询必须分页
- **读写分离**：查询操作使用只读数据库

```sql
-- 为会话查询创建索引
CREATE INDEX idx_chat_sessions_user_id ON chat_sessions(user_id);
CREATE INDEX idx_chat_sessions_status ON chat_sessions(status);
CREATE INDEX idx_chat_messages_session_id ON chat_messages(session_id);
CREATE INDEX idx_chat_messages_created_at ON chat_messages(created_at);
```

## 安全考虑

### 1. 认证与授权
- **JWT Token验证**：所有API请求必须包含有效的JWT Token
- **角色权限控制**：基于角色的访问控制(RBAC)
- **会话访问权限**：用户只能访问自己的会话

```java
@PreAuthorize("hasRole('AGENT_USER') and @chatSecurityService.canAccessSession(#sessionId, authentication.name)")
@GetMapping("/stream")
public SseEmitter streamChat(
        @RequestParam String sessionId,
        @RequestParam String message) {
    // 流式聊天逻辑
}
```

### 2. 输入验证与内容安全
- **消息长度限制**：限制用户输入消息的最大长度
- **敏感词过滤**：过滤敏感内容和恶意输入
- **XSS防护**：对用户输入进行HTML转义
- **SQL注入防护**：使用参数化查询

```java
public class StreamChatInputValidator {

    private static final int MAX_MESSAGE_LENGTH = 4000;
    private static final Pattern XSS_PATTERN = Pattern.compile(
        "<script[^>]*>.*?</script>|<[^>]*script.*?>|javascript:",
        Pattern.CASE_INSENSITIVE
    );

    public void validateMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("消息内容不能为空");
        }

        if (message.length() > MAX_MESSAGE_LENGTH) {
            throw new IllegalArgumentException("消息长度不能超过" + MAX_MESSAGE_LENGTH + "个字符");
        }

        if (containsXSS(message)) {
            throw new IllegalArgumentException("消息内容包含不安全的内容");
        }
    }

    private boolean containsXSS(String message) {
        return XSS_PATTERN.matcher(message).find();
    }
}
```

### 3. 速率限制
- **请求频率限制**：限制用户请求频率
- **并发连接限制**：限制单个用户的并发连接数
- **IP白名单**：限制特定IP的访问

```yaml
agents:
  rate-limit:
    enabled: true
    requests-per-minute: 60
    burst-size: 10
    concurrent-connections-per-user: 5
    ip-whitelist:
      - 127.0.0.1
      - 192.168.1.0/24
```

### 4. 数据隐私保护
- **PII数据脱敏**：对个人身份信息进行脱敏处理
- **日志脱敏**：确保日志中不包含敏感信息
- **数据加密**：敏感数据传输和存储加密

```java
public class DataSanitizer {

    public String sanitizeForLogging(String content) {
        if (content == null) return null;

        // 脱敏手机号
        content = content.replaceAll("\\b(1[3-9]\\d)\\d{4}(\\d{4})\\b", "$1****$2");

        // 脱敏邮箱
        content = content.replaceAll("\\b([a-zA-Z0-9._%+-]+)@([a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})\\b", "$1***@$2");

        // 脱敏身份证号
        content = content.replaceAll("\\b(\\d{6})\\d{8}(\\d{4})\\b", "$1********$2");

        return content;
    }
}
```

## 故障排查指南

### 1. 常见问题诊断

#### SSE连接问题
```bash
# 检查SSE连接状态
curl -v -N "http://localhost:8080/api/v1/chat/stream?sessionId=test&message=hello"

# 检查网络连接
telnet localhost 8080

# 检查防火墙设置
iptables -L -n | grep 8080
```

#### AI服务问题
```bash
# 测试AI服务连接
curl -X POST "https://open.bigmodel.cn/api/paas/v4/chat/completions" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{"model": "glm-4", "messages": [{"role": "user", "content": "Hello"}]}'

# 检查API密钥配置
grep BIGMODEL_API_KEY application.yml
```

#### 数据库连接问题
```bash
# 检查数据库连接
mysql -h localhost -u username -p database_name

# 检查表结构
DESCRIBE chat_sessions;
DESCRIBE chat_messages;

# 检查索引
SHOW INDEX FROM chat_sessions;
SHOW INDEX FROM chat_messages;
```

### 2. 日志分析

#### 关键日志位置
```bash
# 应用日志
tail -f logs/application.log

# 错误日志
tail -f logs/error.log

# 访问日志
tail -f logs/access.log

# 性能日志
tail -f logs/metrics.log
```

#### 日志查询示例
```bash
# 查找特定会话的日志
grep "sessionId: abc123" logs/application.log

# 查找错误日志
grep "ERROR" logs/application.log

# 查找性能慢的请求
grep "took.*ms" logs/application.log | awk '$NF > 1000'

# 查找函数调用日志
grep "Function called" logs/application.log
```

### 3. 性能调优

#### JVM调优参数
```bash
-Xms2g -Xmx4g -XX:+UseG1GC -XX:MaxGCPauseMillis=200
-XX:+PrintGCDetails -XX:+PrintGCTimeStamps
-Xloggc:logs/gc.log
```

#### 数据库连接池配置
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      idle-timeout: 300000
      max-lifetime: 1200000
      connection-timeout: 20000
```

---

*更新时间：2025-01-22*
*版本：v2.0 - 包含完整的业务需求和技术设计*