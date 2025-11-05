package com.i0.agents.application.dto.output;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.i0.agents.domain.enums.MessageRole;
import com.i0.agents.domain.valueobjects.UIComponentReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 聊天消息输出DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageOutput {
    private String id;
    private String sessionId;
    private MessageRole role;
    private String content;
    private Map<String, Object> functionCall;
    private List<UIComponentReference> uiComponents;
    private String parentMessageId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    /**
     * 从Domain实体转换为输出DTO
     */
    public static ChatMessageOutput from(com.i0.agents.domain.entities.ChatMessage chatMessage) {
        if (chatMessage == null) {
            return null;
        }

        return ChatMessageOutput.builder()
                .id(chatMessage.getId())
                .sessionId(chatMessage.getSessionId())
                .role(chatMessage.getRole())
                .content(chatMessage.getContent() != null ? chatMessage.getContent().getContent() : null)
                .functionCall(chatMessage.getFunctionCall() != null ?
                        createFunctionCallMap(chatMessage.getFunctionCall()) : null)
                .uiComponents(chatMessage.hasUIComponents() ? chatMessage.getUiComponents() : null)
                .parentMessageId(chatMessage.getParentMessageId())
                .timestamp(chatMessage.getTimestamp())
                .build();
    }

    /**
     * 创建函数调用Map，支持null值
     */
    private static Map<String, Object> createFunctionCallMap(com.i0.agents.domain.valueobjects.FunctionCall functionCall) {
        Map<String, Object> functionCallMap = new java.util.HashMap<>();
        functionCallMap.put("functionName", functionCall.getFunctionName());
        functionCallMap.put("arguments", functionCall.getArguments());
        functionCallMap.put("description", functionCall.getDescription());
        return functionCallMap;
    }
}