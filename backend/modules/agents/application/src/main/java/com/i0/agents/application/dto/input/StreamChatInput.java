package com.i0.agents.application.dto.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 流式聊天输入DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StreamChatInput {

    @NotBlank(message = "会话ID不能为空")
    private String sessionId;

    @NotBlank(message = "消息内容不能为空")
    private String content;

    private String userId;

    /**
     * 是否启用思考状态显示
     */
    @Builder.Default
    private boolean showThinking = true;

    /**
     * 流式发送速度（毫秒/字符）
     */
    @Builder.Default
    private int typingSpeed = 100;

    /**
     * 是否启用完成状态
     */
    @Builder.Default
    private boolean showCompleted = true;
}