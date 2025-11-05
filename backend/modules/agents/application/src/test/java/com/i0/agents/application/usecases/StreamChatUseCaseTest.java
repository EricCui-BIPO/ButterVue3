package com.i0.agents.application.usecases;

import com.i0.agents.application.dto.input.StreamChatInput;
import com.i0.agents.application.dto.output.ConversationResult;
import com.i0.agents.application.services.AIConversationCoordinator;
import com.i0.agents.domain.entities.ChatMessage;
import com.i0.agents.domain.entities.ChatSession;
import com.i0.agents.domain.enums.MessageRole;
import com.i0.agents.domain.repositories.ChatMessageRepository;
import com.i0.agents.domain.repositories.ChatSessionRepository;
import com.i0.agents.domain.valueobjects.MessageContent;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * StreamChatUseCase 测试
 */
@Slf4j
@ExtendWith(MockitoExtension.class)
@DisplayName("StreamChatUseCase 测试")
class StreamChatUseCaseTest {

    @Mock
    private ChatSessionRepository chatSessionRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private AIConversationCoordinator aiConversationService;

    @Mock
    private SendMessageUseCase sendMessageUseCase;

    @Mock
    private ChatSession mockChatSession;

    private StreamChatUseCase streamChatUseCase;

    @BeforeEach
    void setUp() {
        streamChatUseCase = new StreamChatUseCase(
                chatSessionRepository,
                chatMessageRepository,
                aiConversationService,
                sendMessageUseCase);
    }

    @Test
    @DisplayName("成功的流式聊天 - 包含思考状态")
    void testSuccessfulStreamChatWithThinking() {
        // Given
        String sessionId = "test-session-1";
        String userId = "test-user-1";
        String userMessage = "你好，帮我创建一个实体";
        String aiResponse = "我来帮您创建实体。请告诉我您想创建什么类型的实体？";

        StreamChatInput input = StreamChatInput.builder()
                .sessionId(sessionId)
                .content(userMessage)
                .userId(userId)
                .showThinking(true)
                .typingSpeed(50)
                .showCompleted(true)
                .build();

        // Mock session
        when(chatSessionRepository.findById(sessionId))
                .thenReturn(java.util.Optional.of(mockChatSession));
        when(mockChatSession.isInteractive()).thenReturn(true);
        // Mock addMessage method - do nothing for the test
        doNothing().when(mockChatSession).addMessage(any(ChatMessage.class));

        // Mock user message save
        ChatMessage savedUserMessage = createMockUserMessage(sessionId, userMessage);
        when(chatMessageRepository.save(any(ChatMessage.class)))
                .thenReturn(savedUserMessage);

        // Mock session messages
        List<ChatMessage> sessionMessages = Collections.singletonList(savedUserMessage);
        when(chatMessageRepository.findBySessionId(sessionId))
                .thenReturn(sessionMessages);

        // Mock AI response
        ConversationResult mockResult = ConversationResult.fromText(aiResponse);
        when(aiConversationService.chat(eq(sessionId), any(List.class), any(SseEmitter.class)))
                .thenReturn(CompletableFuture.completedFuture(mockResult));

        // Mock session save
        when(chatSessionRepository.save(any(ChatSession.class)))
                .thenReturn(mockChatSession);

        // When
        SseEmitter emitter = streamChatUseCase.executeStreamChat(input);

        // Then
        assertNotNull(emitter);

        // 验证AI服务被调用
        verify(aiConversationService, timeout(5000))
                .chat(eq(sessionId), any(List.class), any(SseEmitter.class));

        // 验证用户消息被保存
        verify(chatMessageRepository, timeout(5000))
                .save(any(ChatMessage.class));

        // 验证会话被更新
        verify(chatSessionRepository, timeout(5000))
                .save(any(ChatSession.class));
    }

    @Test
    @DisplayName("会话不存在的情况")
    void testSessionNotFound() {
        // Given
        String sessionId = "non-existent-session";
        String userId = "test-user";
        String userMessage = "测试消息";

        StreamChatInput input = StreamChatInput.builder()
                .sessionId(sessionId)
                .content(userMessage)
                .userId(userId)
                .build();

        // Mock session not found
        when(chatSessionRepository.findById(sessionId))
                .thenReturn(java.util.Optional.empty());

        // When
        SseEmitter emitter = streamChatUseCase.executeStreamChat(input);

        // Then
        assertNotNull(emitter);

        // 验证没有调用AI服务
        verify(aiConversationService, never())
                .chat(anyString(), any(List.class));
    }

    private ChatMessage createMockUserMessage(String sessionId, String content) {
        return ChatMessage.reconstruct(
                "user-message-id",
                sessionId,
                MessageRole.USER,
                MessageContent.of(content),
                null,
                null, // uiComponents
                LocalDateTime.now(),
                null
        );
    }
}