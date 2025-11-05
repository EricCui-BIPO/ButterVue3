package com.i0.agents.gateway.persistence.dataobjects;

import com.baomidou.mybatisplus.annotation.*;
import com.i0.agents.domain.entities.ChatSession;
import com.i0.agents.domain.enums.SessionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 聊天会话数据对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("chat_sessions")
public class ChatSessionDO {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String title;

    private String status;

    private String userId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic(value = "false", delval = "true")
    @TableField("is_deleted")
    private Boolean isDeleted;

    /**
     * 从Domain实体转换为DO
     */
    public static ChatSessionDO from(ChatSession chatSession) {
        if (chatSession == null) {
            return null;
        }

        return ChatSessionDO.builder()
                .id(chatSession.getId())
                .title(chatSession.getTitle())
                .status(chatSession.getStatus().name())
                .userId(chatSession.getUserId())
                .createdAt(chatSession.getCreatedAt())
                .updatedAt(chatSession.getUpdatedAt())
                .isDeleted(false)
                .build();
    }

    /**
     * 从DO转换为Domain实体
     */
    public ChatSession toDomain() {
        SessionStatus status = SessionStatus.valueOf(this.status);

        return ChatSession.reconstruct(
                this.id,
                this.title,
                status,
                this.createdAt,
                this.updatedAt,
                this.userId
        );
    }
}