package com.i0.agents.application.usecases;

import com.i0.agents.application.dto.input.CreateChatSessionInput;
import com.i0.agents.application.dto.output.ChatSessionOutput;
import com.i0.agents.domain.entities.ChatSession;
import com.i0.agents.domain.exceptions.AgentsException;
import com.i0.agents.domain.repositories.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 创建聊天会话UseCase
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CreateChatSessionUseCase {

    private final ChatSessionRepository chatSessionRepository;

    /**
     * 执行创建聊天会话
     *
     * @param input 创建会话输入
     * @return 创建的会话输出
     */
    @Transactional
    public ChatSessionOutput execute(CreateChatSessionInput input) {
        log.info("Creating chat session with title: {} for user: {}",
                input.getTitle(), input.getUserId());

        try {
            // 创建新的聊天会话
            ChatSession chatSession = ChatSession.create(input.getTitle(), input.getUserId());

            // 保存会话
            ChatSession savedSession = chatSessionRepository.save(chatSession);

            log.info("Successfully created chat session with id: {}", savedSession.getId());

            return ChatSessionOutput.from(savedSession);

        } catch (IllegalArgumentException e) {
            log.error("Invalid input for creating chat session: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to create chat session", e);
            throw new RuntimeException("创建聊天会话失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据ID获取聊天会话
     *
     * @param sessionId 会话ID
     * @return 会话输出
     */
    public ChatSessionOutput getChatSession(String sessionId) {
        log.debug("Getting chat session by id: {}", sessionId);

        Optional<ChatSession> sessionOpt = chatSessionRepository.findById(sessionId);

        if (sessionOpt.isEmpty()) {
            log.warn("Chat session not found: {}", sessionId);
            throw new AgentsException("聊天会话不存在: " + sessionId);
        }

        return ChatSessionOutput.from(sessionOpt.get());
    }
}