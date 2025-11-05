package com.i0.agents.application.dto.input;

import com.i0.agents.domain.enums.SessionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 聊天会话分页查询输入DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatSessionPageInput {

    private Integer page;

    private Integer size;

    private String userId;

    private SessionStatus status;

    private String titleKeyword;
}