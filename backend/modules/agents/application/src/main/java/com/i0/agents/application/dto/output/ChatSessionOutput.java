package com.i0.agents.application.dto.output;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.i0.agents.domain.enums.SessionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 聊天会话输出DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatSessionOutput {
    private String id;
    private String title;
    private SessionStatus status;
    private String userId;
    private int messageCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private List<ChatMessageOutput> messages;

    /**
     * 从Domain实体转换为输出DTO
     */
    public static ChatSessionOutput from(com.i0.agents.domain.entities.ChatSession chatSession) {
        if (chatSession == null) {
            return null;
        }

        return ChatSessionOutput.builder()
                .id(chatSession.getId())
                .title(chatSession.getTitle())
                .status(chatSession.getStatus())
                .userId(chatSession.getUserId())
                .messageCount(chatSession.getMessageCount())
                .createdAt(chatSession.getCreatedAt())
                .updatedAt(chatSession.getUpdatedAt())
                .build();
    }

    /**
     * 从Domain实体和消息列表转换为输出DTO
     */
    public static ChatSessionOutput from(com.i0.agents.domain.entities.ChatSession chatSession,
                                       List<ChatMessageOutput> messages) {
        ChatSessionOutput output = from(chatSession);
        if (output != null) {
            output.setMessages(messages);
            // 当提供了消息列表时，使用实际的列表大小作为消息计数
            output.setMessageCount(messages != null ? messages.size() : 0);
        }
        return output;
    }
}