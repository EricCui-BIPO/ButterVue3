package com.i0.agents.application.usecases;

import com.i0.agents.application.dto.input.StreamChatInput;
import com.i0.agents.application.dto.output.ChatMessageOutput;
import com.i0.agents.application.dto.output.ConversationResult;
import com.i0.agents.application.dto.output.StreamChatEvent;
import com.i0.agents.application.services.AIConversationCoordinator;
import com.i0.agents.domain.entities.ChatMessage;
import com.i0.agents.domain.entities.ChatSession;
import com.i0.agents.domain.exceptions.AgentsException;
import com.i0.agents.domain.repositories.ChatMessageRepository;
import com.i0.agents.domain.repositories.ChatSessionRepository;
import com.i0.agents.domain.valueobjects.UIComponentReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 流式聊天UseCase
 * 处理SSE流式聊天，接入真实的AI对话服务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StreamChatUseCase {

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final AIConversationCoordinator aiConversationCoordinator;
    private final SendMessageUseCase sendMessageUseCase;

    /**
     * 处理流式聊天请求
     *
     * @param input 流式聊天输入
     * @return SSE发射器用于实时推送消息
     */
    public SseEmitter executeStreamChat(StreamChatInput input) {
        log.info("Starting stream chat for session: {}, user: {}", input.getSessionId(), input.getUserId());

        // 创建SSE发射器
        SseEmitter emitter = new SseEmitter(60000L); // 60秒超时

        try {
            // 异步处理流式聊天
            CompletableFuture.runAsync(() -> processStreamChat(input, emitter))
                    .orTimeout(60, TimeUnit.SECONDS)
                    .exceptionally(throwable -> {
                        log.error("Stream chat processing failed for session: {}", input.getSessionId(), throwable);
                        handleError(emitter, "处理失败: " + throwable.getMessage(), input.getSessionId());
                        return null;
                    });

        } catch (Exception e) {
            log.error("Failed to start stream chat for session: {}", input.getSessionId(), e);
            emitter.completeWithError(e);
        }

        return emitter;
    }

    /**
     * 处理流式聊天的核心逻辑
     */
    private void processStreamChat(StreamChatInput input, SseEmitter emitter) {
        try {
            // 1. 验证会话存在且可交互
            ChatSession session = validateAndGetSession(input.getSessionId());

            // 2. 发送处理开始状态
            sendEvent(emitter, StreamChatEvent.status("processing", input.getSessionId()));

            // 3. 发送用户消息确认
            sendEvent(emitter, StreamChatEvent.message("user", input.getContent(), input.getSessionId()));

            // 4. 保存用户消息
            ChatMessage userMessage = saveUserMessage(input);

            // 5. 发送AI思考状态（如果启用）
            if (input.isShowThinking()) {
                sendEvent(emitter, StreamChatEvent.status("thinking", input.getSessionId()));
                // 模拟思考过程，给用户更好的体验
                Thread.sleep(500);
            }

            // 6. 调用AI服务获取响应
            List<ChatMessage> sessionMessages = chatMessageRepository.findBySessionId(input.getSessionId());
            ConversationResult conversationResult = aiConversationCoordinator.chat(input.getSessionId(), sessionMessages, emitter)
                    .get(30, TimeUnit.SECONDS);

            // 7. 验证和处理AI回复
            String processedResponse = processAIResponse(conversationResult.getResponse());

            // 8. 流式发送AI响应
            streamAssistantResponse(emitter, processedResponse, input);

            // 9. 保存AI回复消息（包含UI组件信息）
            saveAssistantMessage(input.getSessionId(), processedResponse, userMessage.getId(), conversationResult.getUiComponents());

            // 10. 更新会话状态
            session.addMessage(userMessage);
            chatSessionRepository.save(session);

            // 11. 发送完成状态（如果启用）
            if (input.isShowCompleted()) {
                sendEvent(emitter, StreamChatEvent.completed(input.getSessionId()));
            }

            // 12. 完成流式响应
            emitter.complete();

            log.info("Stream chat completed successfully for session: {}", input.getSessionId());

        } catch (java.util.concurrent.TimeoutException e) {
            log.error("AI service timeout for session: {}", input.getSessionId());
            handleError(emitter, "AI服务响应超时，请稍后再试", input.getSessionId());
        } catch (java.util.concurrent.ExecutionException e) {
            log.error("AI service execution failed for session: {}", input.getSessionId(), e);
            handleError(emitter, "AI服务暂时不可用，请稍后再试", input.getSessionId());
        } catch (InterruptedException e) {
            log.error("AI service interrupted for session: {}", input.getSessionId());
            Thread.currentThread().interrupt();
            handleError(emitter, "服务请求被中断，请重试", input.getSessionId());
        } catch (Exception e) {
            log.error("Stream chat processing failed for session: {}", input.getSessionId(), e);
            handleError(emitter, "处理失败: " + e.getMessage(), input.getSessionId());
        }
    }

    /**
     * 流式发送AI助手回复
     */
    private void streamAssistantResponse(SseEmitter emitter, String response, StreamChatInput input) {
        try {
            // 按字符分割模拟打字效果
            String[] words = response.split(" ");
            for (int i = 0; i < words.length; i++) {
                String word = words[i];

                // 如果不是最后一个词，添加空格
                if (i < words.length - 1) {
                    word += " ";
                }

                sendEvent(emitter, StreamChatEvent.message("assistant", word, input.getSessionId()));

                // 打字延迟（模拟AI思考输出）
                Thread.sleep(input.getTypingSpeed());
            }
        } catch (InterruptedException e) {
            log.error("Streaming response interrupted for session: {}", input.getSessionId());
            Thread.currentThread().interrupt();
            // 发送剩余内容
            sendEvent(emitter, StreamChatEvent.message("assistant", response, input.getSessionId()));
        } catch (Exception e) {
            log.error("Error streaming response for session: {}", input.getSessionId(), e);
            // 发送完整内容作为后备
            sendEvent(emitter, StreamChatEvent.message("assistant", response, input.getSessionId()));
        }
    }

    /**
     * 发送SSE事件
     */
    private void sendEvent(SseEmitter emitter, StreamChatEvent event) {
        try {
            emitter.send(SseEmitter.event()
                    .name(event.getEventType())
                    .data(event.toJson()));
        } catch (IOException e) {
            log.error("Failed to send SSE event: {}", event, e);
        }
    }

    /**
     * 处理错误情况
     */
    private void handleError(SseEmitter emitter, String errorMessage, String sessionId) {
        try {
            sendEvent(emitter, StreamChatEvent.error(errorMessage, sessionId));
            emitter.complete();
        } catch (Exception e) {
            log.error("Failed to send error event", e);
            emitter.completeWithError(e);
        }
    }

    /**
     * 保存用户消息
     */
    private ChatMessage saveUserMessage(StreamChatInput input) {
        ChatMessage userMessage = ChatMessage.createUserMessage(input.getSessionId(), input.getContent());
        return chatMessageRepository.save(userMessage);
    }

    /**
     * 保存AI回复消息（包含UI组件信息）
     */
    private void saveAssistantMessage(String sessionId, String content, String parentMessageId, List<UIComponentReference> uiComponents) {
        ChatMessage assistantMessage = ChatMessage.createAssistantMessage(sessionId, content, parentMessageId, uiComponents);
        chatMessageRepository.save(assistantMessage);

        log.debug("Saved assistant message with {} UI components for session: {}",
                uiComponents != null ? uiComponents.size() : 0, sessionId);
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

    /**
     * 获取会话消息历史（代理方法）
     */
    public List<ChatMessageOutput> getSessionMessages(String sessionId) {
        return sendMessageUseCase.getSessionMessages(sessionId);
    }

    /**
     * 获取会话详情（代理方法）
     */
    public ChatMessageOutput sendMessage(com.i0.agents.application.dto.input.SendMessageInput input) {
        return sendMessageUseCase.execute(input);
    }
}