package com.i0.agents.domain.entities;

import com.i0.agents.domain.enums.MessageRole;
import com.i0.agents.domain.valueobjects.FunctionCall;
import com.i0.agents.domain.valueobjects.MessageContent;
import com.i0.agents.domain.valueobjects.UIComponentReference;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.UUID;

/**
 * 聊天消息实体
 * 表示聊天会话中的单条消息
 */
public class ChatMessage {
    private final String id;
    private final String sessionId;
    private final MessageRole role;
    private final MessageContent content;
    private final FunctionCall functionCall;
    private final List<UIComponentReference> uiComponents;
    private final LocalDateTime timestamp;
    private final String parentMessageId;

    private ChatMessage(String id, String sessionId, MessageRole role, MessageContent content,
                       FunctionCall functionCall, List<UIComponentReference> uiComponents,
                       LocalDateTime timestamp, String parentMessageId) {
        this.id = id;
        this.sessionId = sessionId;
        this.role = role;
        this.content = content;
        this.functionCall = functionCall;
        this.uiComponents = uiComponents != null ? new ArrayList<>(uiComponents) : new ArrayList<>();
        this.timestamp = timestamp;
        this.parentMessageId = parentMessageId;
    }

    /**
     * 创建用户消息
     */
    public static ChatMessage createUserMessage(String sessionId, String content) {
        return create(sessionId, MessageRole.USER, content, null, null, null);
    }

    /**
     * 创建助手消息
     */
    public static ChatMessage createAssistantMessage(String sessionId, String content, String parentMessageId) {
        return create(sessionId, MessageRole.ASSISTANT, content, null, null, parentMessageId);
    }

    /**
     * 创建带UI组件的助手消息
     */
    public static ChatMessage createAssistantMessage(String sessionId, String content, String parentMessageId,
                                                   List<UIComponentReference> uiComponents) {
        return create(sessionId, MessageRole.ASSISTANT, content, null, uiComponents, parentMessageId);
    }

    /**
     * 创建函数调用消息
     */
    public static ChatMessage createFunctionMessage(String sessionId, FunctionCall functionCall, String parentMessageId) {
        return create(sessionId, MessageRole.FUNCTION, null, functionCall, null, parentMessageId);
    }

    /**
     * 创建系统消息
     */
    public static ChatMessage createSystemMessage(String sessionId, String content) {
        return create(sessionId, MessageRole.SYSTEM, content, null, null, null);
    }

    /**
     * 从已有数据重建消息
     */
    public static ChatMessage reconstruct(String id, String sessionId, MessageRole role,
                                         MessageContent content, FunctionCall functionCall,
                                         List<UIComponentReference> uiComponents,
                                         LocalDateTime timestamp, String parentMessageId) {
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("消息ID不能为空");
        }

        if (StringUtils.isBlank(sessionId)) {
            throw new IllegalArgumentException("会话ID不能为空");
        }

        if (role == null) {
            throw new IllegalArgumentException("消息角色不能为空");
        }

        return new ChatMessage(id, sessionId, role, content, functionCall, uiComponents, timestamp, parentMessageId);
    }

    /**
     * 通用创建方法
     */
    private static ChatMessage create(String sessionId, MessageRole role, String content,
                                    FunctionCall functionCall, List<UIComponentReference> uiComponents,
                                    String parentMessageId) {
        if (StringUtils.isBlank(sessionId)) {
            throw new IllegalArgumentException("会话ID不能为空");
        }

        if (role == null) {
            throw new IllegalArgumentException("消息角色不能为空");
        }

        // 验证角色和内容的匹配
        if (role != MessageRole.FUNCTION && functionCall != null) {
            throw new IllegalArgumentException("非函数调用消息不能包含函数调用信息");
        }

        if (role == MessageRole.FUNCTION && functionCall == null) {
            throw new IllegalArgumentException("函数调用消息必须包含函数调用信息");
        }

        if (role != MessageRole.FUNCTION && StringUtils.isBlank(content)) {
            throw new IllegalArgumentException("消息内容不能为空");
        }

        MessageContent messageContent = StringUtils.isNotBlank(content) ? MessageContent.of(content) : null;

        return new ChatMessage(
                UUID.randomUUID().toString(),
                sessionId,
                role,
                messageContent,
                functionCall,
                uiComponents,
                LocalDateTime.now(),
                parentMessageId
        );
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public MessageRole getRole() {
        return role;
    }

    public MessageContent getContent() {
        return content;
    }

    public FunctionCall getFunctionCall() {
        return functionCall;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getParentMessageId() {
        return parentMessageId;
    }

    public List<UIComponentReference> getUiComponents() {
        return new ArrayList<>(uiComponents);
    }

    /**
     * 判断是否为用户消息
     */
    public boolean isUserMessage() {
        return role.isUser();
    }

    /**
     * 判断是否为助手消息
     */
    public boolean isAssistantMessage() {
        return role.isAssistant();
    }

    /**
     * 判断是否为函数调用消息
     */
    public boolean isFunctionMessage() {
        return role.isFunction();
    }

    /**
     * 判断是否为系统消息
     */
    public boolean isSystemMessage() {
        return role.isSystem();
    }

    /**
     * 判断是否有父消息
     */
    public boolean hasParentMessage() {
        return StringUtils.isNotBlank(parentMessageId);
    }

    /**
     * 判断是否包含UI组件
     */
    public boolean hasUIComponents() {
        return uiComponents != null && !uiComponents.isEmpty();
    }

    /**
     * 获取UI组件数量
     */
    public int getUIComponentCount() {
        return uiComponents != null ? uiComponents.size() : 0;
    }

    /**
     * 根据组件类型获取UI组件
     */
    public List<UIComponentReference> getUIComponentsByType(String componentType) {
        if (uiComponents == null || StringUtils.isBlank(componentType)) {
            return new ArrayList<>();
        }

        return uiComponents.stream()
                .filter(component -> componentType.equals(component.getComponentType()))
                .collect(ArrayList::new, (list, component) -> list.add(component), ArrayList::addAll);
    }

    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMessage that = (ChatMessage) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "id='" + id + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", role=" + role +
                ", content=" + (content != null ? content.getContent().substring(0, Math.min(20, content.getLength())) + "..." : "null") +
                ", functionCall=" + (functionCall != null ? functionCall.getFunctionName() : "null") +
                ", timestamp=" + timestamp +
                '}';
    }
}