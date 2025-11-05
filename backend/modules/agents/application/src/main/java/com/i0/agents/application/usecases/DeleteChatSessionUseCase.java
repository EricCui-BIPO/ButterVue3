package com.i0.agents.application.usecases;

import com.i0.agents.domain.repositories.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 删除聊天会话用例
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class DeleteChatSessionUseCase {

    private final ChatSessionRepository chatSessionRepository;

    /**
     * 执行删除聊天会话
     *
     * @param sessionId 会话ID
     */
    public void execute(String sessionId) {
        log.info("Deleting chat session: {}", sessionId);

        if (sessionId == null || sessionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Session ID cannot be null or empty");
        }

        // 检查会话是否存在
        if (!chatSessionRepository.existsById(sessionId)) {
            throw new IllegalArgumentException("Chat session not found: " + sessionId);
        }

        // 删除会话（逻辑删除）
        chatSessionRepository.deleteById(sessionId);

        log.info("Successfully deleted chat session: {}", sessionId);
    }
}