package com.i0.agents.application.usecases;

import com.i0.agents.application.dto.input.SendMessageInput;
import com.i0.agents.application.dto.output.ChatMessageOutput;
import com.i0.agents.application.dto.output.ChatSessionOutput;
import com.i0.agents.application.services.AIConversationCoordinator;
import com.i0.agents.domain.entities.ChatMessage;
import com.i0.agents.domain.entities.ChatSession;
import com.i0.agents.domain.exceptions.AgentsException;
import com.i0.agents.domain.repositories.ChatMessageRepository;
import com.i0.agents.domain.repositories.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 发送消息UseCase
 * 处理用户消息发送和AI回复（简化版本）
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SendMessageUseCase {

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final AIConversationCoordinator aiConversationService;

    /**
     * 发送消息并获取AI回复
     *
     * @param input 发送消息输入
     * @return AI回复消息输出
     */
    @Transactional
    public ChatMessageOutput execute(SendMessageInput input) {
        log.info("Processing message for session: {}, user: {}", input.getSessionId(), input.getUserId());

        try {
            // 1. 验证会话存在且可交互
            ChatSession session = validateAndGetSession(input.getSessionId());

            // 2. 保存用户消息
            ChatMessage userMessage = ChatMessage.createUserMessage(input.getSessionId(), input.getContent());
            userMessage = chatMessageRepository.save(userMessage);

            // 3. 获取会话历史并调用AI服务
            List<ChatMessage> sessionMessages = chatMessageRepository.findBySessionId(input.getSessionId());
            com.i0.agents.application.dto.output.ConversationResult conversationResult = aiConversationService.chat(input.getSessionId(), sessionMessages)
                .get(30, java.util.concurrent.TimeUnit.SECONDS);

            // 4. 验证和处理AI回复
            String processedResponse = processAIResponse(conversationResult.getResponse());

            // 5. 保存AI回复消息（包含UI组件信息）
            ChatMessage assistantMessage = ChatMessage.createAssistantMessage(
                input.getSessionId(),
                processedResponse,
                userMessage.getId(),
                conversationResult.getUiComponents()
            );
            assistantMessage = chatMessageRepository.save(assistantMessage);

            // 6. 更新会话状态
            session.addMessage(userMessage);
            session.addMessage(assistantMessage);
            chatSessionRepository.save(session);

            log.info("Message processed successfully for session: {}", input.getSessionId());
            return ChatMessageOutput.from(assistantMessage);

        } catch (java.util.concurrent.TimeoutException e) {
            log.error("AI service timeout for session: {}", input.getSessionId());
            throw new AgentsException("AI服务响应超时，请稍后再试");
        } catch (java.util.concurrent.ExecutionException e) {
            log.error("AI service execution failed for session: {}", input.getSessionId(), e);
            throw new AgentsException("AI服务暂时不可用，请稍后再试");
        } catch (InterruptedException e) {
            log.error("AI service interrupted for session: {}", input.getSessionId());
            Thread.currentThread().interrupt();
            throw new AgentsException("服务请求被中断，请重试");
        } catch (AgentsException e) {
            log.warn("Failed to send message: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to send message to session: {}", input.getSessionId(), e);
            throw new AgentsException("发送消息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 处理AI回复内容
     */
    private String processAIResponse(String aiResponse) {
        if (aiResponse == null || aiResponse.trim().isEmpty()) {
            log.warn("AI returned empty response, using fallback");
            return "抱歉，我暂时无法回复您的消息，请稍后再试。";
        }

        // 检查是否包含错误信息
        if (aiResponse.contains("API错误") || aiResponse.contains("认证失败") ||
            aiResponse.contains("余额不足") || aiResponse.contains("请求失败")) {
            log.warn("AI returned error message: {}", aiResponse);
            return "服务暂时不可用，请稍后再试。如果问题持续存在，请联系管理员。";
        }

        return aiResponse;
    }

    /**
     * 获取会话消息历史
     *
     * @param sessionId 会话ID
     * @return 消息列表
     */
    public List<ChatMessageOutput> getSessionMessages(String sessionId) {
        log.debug("Getting messages for session: {}", sessionId);

        try {
            validateAndGetSession(sessionId);

            List<ChatMessage> messages = chatMessageRepository.findBySessionId(sessionId);
            return messages.stream()
                .map(ChatMessageOutput::from)
                .collect(Collectors.toList());


        } catch (AgentsException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to get messages for session: {}", sessionId, e);
            throw new AgentsException("获取消息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取会话详情（包含消息）
     *
     * @param sessionId 会话ID
     * @return 会话详情
     */
    public ChatSessionOutput getSessionWithMessages(String sessionId) {
        log.debug("Getting session with messages: {}", sessionId);

        try {
            ChatSession session = validateAndGetSession(sessionId);
            List<ChatMessageOutput> messages = getSessionMessages(sessionId);
            return ChatSessionOutput.from(session, messages);

        } catch (AgentsException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to get session with messages: {}", sessionId, e);
            throw new AgentsException("获取会话详情失败: " + e.getMessage(), e);
        }
    }

    /**
     * 验证会话并返回
     */
    private ChatSession validateAndGetSession(String sessionId) {
        return chatSessionRepository.findById(sessionId)
            .filter(session -> session.isInteractive())
            .orElseThrow(() -> {
                log.warn("Session not found or not interactive: {}", sessionId);
                return new AgentsException("聊天会话不存在或不可交互: " + sessionId);
            });
    }
}