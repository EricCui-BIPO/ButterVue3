package com.i0.agents.gateway.persistence.dataobjects;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.i0.agents.domain.entities.ChatMessage;
import com.i0.agents.domain.enums.MessageRole;
import com.i0.agents.domain.valueobjects.FunctionCall;
import com.i0.agents.domain.valueobjects.MessageContent;
import com.i0.agents.domain.valueobjects.UIComponentReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 聊天消息数据对象
 */
@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("chat_messages")
public class ChatMessageDO {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String sessionId;

    private String role;

    private String content;

    private String functionName;

    private String functionArguments;

    private String functionDescription;

    private String uiComponents; // JSON格式存储UI组件引用列表

    private String parentMessageId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime timestamp;

    @TableLogic(value = "false", delval = "true")
    @TableField("is_deleted")
    private Boolean isDeleted;

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    /**
     * 从Domain实体转换为DO
     */
    public static ChatMessageDO from(ChatMessage chatMessage) {
        if (chatMessage == null) {
            return null;
        }

        return ChatMessageDO.builder()
                .id(chatMessage.getId())
                .sessionId(chatMessage.getSessionId())
                .role(chatMessage.getRole().name())
                .content(chatMessage.getContent() != null ? chatMessage.getContent().getContent() : null)
                .functionName(chatMessage.getFunctionCall() != null ? chatMessage.getFunctionCall().getFunctionName() : null)
                .functionArguments(chatMessage.getFunctionCall() != null ? serializeArguments(chatMessage.getFunctionCall().getArguments()) : null)
                .functionDescription(chatMessage.getFunctionCall() != null ? chatMessage.getFunctionCall().getDescription() : null)
                .uiComponents(chatMessage.hasUIComponents() ? serializeUIComponents(chatMessage.getUiComponents()) : null)
                .parentMessageId(chatMessage.getParentMessageId())
                .timestamp(chatMessage.getTimestamp())
                .isDeleted(false)
                .build();
    }

    /**
     * 从DO转换为Domain实体
     */
    public ChatMessage toDomain() {
        MessageRole role = MessageRole.valueOf(this.role);
        FunctionCall functionCall = null;

        if (this.functionName != null) {
            Map<String, Object> arguments = deserializeArguments(this.functionArguments);
            functionCall = FunctionCall.of(this.functionName, arguments, this.functionDescription);
        }

        MessageContent messageContent = this.content != null ? MessageContent.of(this.content) : null;
        List<UIComponentReference> uiComponents = deserializeUIComponents(this.uiComponents);

        return ChatMessage.reconstruct(
                this.id,
                this.sessionId,
                role,
                messageContent,
                functionCall,
                uiComponents,
                this.timestamp,
                this.parentMessageId
        );
    }

    /**
     * 序列化函数参数
     */
    private static String serializeArguments(Map<String, Object> arguments) {
        try {
            if (arguments == null) {
                return null;
            }
            return objectMapper.writeValueAsString(arguments);
        } catch (Exception e) {
            log.error("Failed to serialize function arguments", e);
            return "{}";
        }
    }

    /**
     * 反序列化函数参数
     */
    private static Map<String, Object> deserializeArguments(String argumentsJson) {
        try {
            if (argumentsJson == null || argumentsJson.trim().isEmpty()) {
                return Map.of();
            }
            return objectMapper.readValue(argumentsJson, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.error("Failed to deserialize function arguments: {}", argumentsJson, e);
            return Map.of();
        }
    }

    /**
     * 序列化UI组件列表
     */
    private static String serializeUIComponents(List<UIComponentReference> uiComponents) {
        try {
            if (uiComponents == null || uiComponents.isEmpty()) {
                return null;
            }
            return objectMapper.writeValueAsString(uiComponents);
        } catch (Exception e) {
            log.error("Failed to serialize UI components", e);
            return "[]";
        }
    }

    /**
     * 反序列化UI组件列表
     */
    private static List<UIComponentReference> deserializeUIComponents(String uiComponentsJson) {
        try {
            if (uiComponentsJson == null || uiComponentsJson.trim().isEmpty()) {
                return List.of();
            }
            return objectMapper.readValue(uiComponentsJson, new TypeReference<List<UIComponentReference>>() {});
        } catch (Exception e) {
            log.error("Failed to deserialize UI components: {}", uiComponentsJson, e);
            return List.of();
        }
    }
}