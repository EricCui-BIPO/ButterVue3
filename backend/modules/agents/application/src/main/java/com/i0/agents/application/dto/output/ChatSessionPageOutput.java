package com.i0.agents.application.dto.output;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.i0.agents.domain.enums.SessionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 聊天会话分页输出DTO
 * 注意：此DTO只包含实体字段，不包含分页元数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatSessionPageOutput {
    private String id;
    private String title;
    private SessionStatus status;
    private String userId;
    private int messageCount;
    private long userMessageCount;
    private long assistantMessageCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    /**
     * 从Domain实体转换为分页输出DTO
     */
    public static ChatSessionPageOutput from(com.i0.agents.domain.entities.ChatSession chatSession) {
        if (chatSession == null) {
            return null;
        }

        return ChatSessionPageOutput.builder()
                .id(chatSession.getId())
                .title(chatSession.getTitle())
                .status(chatSession.getStatus())
                .userId(chatSession.getUserId())
                .messageCount(chatSession.getMessageCount())
                .userMessageCount(chatSession.getUserMessageCount())
                .assistantMessageCount(chatSession.getAssistantMessageCount())
                .createdAt(chatSession.getCreatedAt())
                .updatedAt(chatSession.getUpdatedAt())
                .build();
    }
}