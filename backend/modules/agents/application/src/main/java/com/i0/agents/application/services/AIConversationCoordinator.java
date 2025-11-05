package com.i0.agents.application.services;

import com.i0.agents.application.dto.output.ConversationResult;
import com.i0.agents.application.usecases.ProcessAIConversationUseCase;
import com.i0.agents.domain.entities.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * AI对话服务协调器
 * 职责：协调各个AI对话相关的服务，提供统一的外部接口
 */
@Slf4j
@Service
public class AIConversationCoordinator {

    private final ProcessAIConversationUseCase processAIConversationUseCase;

    public AIConversationCoordinator(ProcessAIConversationUseCase processAIConversationUseCase) {
        this.processAIConversationUseCase = processAIConversationUseCase;
    }

    /**
     * 进行AI对话（简化版本）
     * 直接使用配置中指定的活跃提供商
     *
     * @param sessionId 会话ID
     * @param messages  消息历史
     * @return AI对话结果
     */
    public CompletableFuture<ConversationResult> chat(String sessionId, List<ChatMessage> messages) {
        return chat(sessionId, messages, null);
    }

    /**
     * 进行AI对话（支持SSE事件推送）
     * 直接使用配置中指定的活跃提供商
     *
     * @param sessionId 会话ID
     * @param messages  消息历史
     * @param emitter   SSE发射器，用于推送UI组件事件（可为null）
     * @return AI对话结果
     */
    public CompletableFuture<ConversationResult> chat(String sessionId, List<ChatMessage> messages, SseEmitter emitter) {
        try {
            log.debug("Processing AI conversation for session: {}", sessionId);

            ProcessAIConversationUseCase.ProcessAIConversationInput input = ProcessAIConversationUseCase.ProcessAIConversationInput.builder()
                .sessionId(sessionId)
                .messages(messages)
                .build();

            return processAIConversationUseCase.executeAsync(input, emitter);

        } catch (Exception e) {
            log.error("Error in AI conversation coordinator for session: {}", sessionId, e);
            return CompletableFuture.completedFuture(ConversationResult.error("对话处理失败，请稍后再试。"));
        }
    }
}