package com.i0.agents.gateway.web.controllers;

import com.i0.agents.application.dto.input.CreateChatSessionInput;
import com.i0.agents.application.dto.input.SendMessageInput;
import com.i0.agents.application.dto.output.ChatMessageOutput;
import com.i0.agents.application.dto.output.ChatSessionOutput;
import com.i0.agents.application.dto.output.QuickPromptOutput;
import com.i0.agents.application.usecases.CreateChatSessionUseCase;
import com.i0.agents.application.usecases.FindChatSessionsUseCase;
import com.i0.agents.application.usecases.SendMessageUseCase;
import com.i0.agents.application.usecases.DeleteChatSessionUseCase;
import com.i0.agents.application.usecases.StreamChatUseCase;
import com.i0.agents.application.usecases.GetQuickPromptsUseCase;
import com.i0.agents.application.dto.input.StreamChatInput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.validation.Valid;
import java.util.List;

/**
 * 聊天会话REST控制器
 * 提供聊天相关的API接口，支持SSE流式通信
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@Validated
public class ChatController {

    private final CreateChatSessionUseCase createChatSessionUseCase;
    private final FindChatSessionsUseCase findChatSessionsUseCase;
    private final SendMessageUseCase sendMessageUseCase;
    private final DeleteChatSessionUseCase deleteChatSessionUseCase;
    private final StreamChatUseCase streamChatUseCase;
    private final GetQuickPromptsUseCase getQuickPromptsUseCase;

    /**
     * 创建新的聊天会话
     *
     * @param request 创建会话请求
     * @return 创建的会话响应
     */
    @PostMapping("/sessions")
    public ChatSessionOutput createSession(@Valid @RequestBody CreateChatSessionInput request) {
        log.info("Creating new chat session with title: {} for user: {}",
                request.getTitle(), request.getUserId());

        return createChatSessionUseCase.execute(request);
    }

    /**
     * 获取聊天会话详情
     *
     * @param sessionId 会话ID
     * @return 会话详情
     */
    @GetMapping("/sessions/{sessionId}")
    public ChatSessionOutput getSession(@PathVariable String sessionId) {
        log.debug("Getting chat session details: {}", sessionId);

        return createChatSessionUseCase.getChatSession(sessionId);
    }

    /**
     * 获取会话的消息列表
     *
     * @param sessionId 会话ID
     * @return 消息列表
     */
    @GetMapping("/sessions/{sessionId}/messages")
    public List<ChatMessageOutput> getSessionMessages(@PathVariable String sessionId) {
        log.debug("Getting messages for session: {}", sessionId);

        return sendMessageUseCase.getSessionMessages(sessionId);
    }

    /**
     * 获取会话详情（包含消息）
     *
     * @param sessionId 会话ID
     * @return 会话详情和消息列表
     */
    @GetMapping("/sessions/{id}/with-messages")
    public ChatSessionOutput getSessionWithMessages(@PathVariable String id) {
        log.debug("Getting session with messages: {}", id);

        return sendMessageUseCase.getSessionWithMessages(id);
    }

    /**
     * 查询聊天会话列表
     *
     * @param userId 用户ID（可选）
     * @return 会话列表
     */
    @GetMapping("/sessions")
    public List<ChatSessionOutput> getSessions(
            @RequestParam(required = false) String userId) {
        log.debug("Getting chat sessions - userId: {}", userId);

        return findChatSessionsUseCase.execute(userId, null);
    }

    /**
     * 删除聊天会话
     *
     * @param id 会话ID
     * @return 删除结果
     */
    @DeleteMapping("/sessions/{id}")
    public void deleteSession(@PathVariable String id) {
        log.info("Deleting chat session: {}", id);
        deleteChatSessionUseCase.execute(id);
    }

    /**
     * 发送消息到聊天会话
     *
     * @param request 发送消息请求
     * @return AI回复消息
     */
    @PostMapping("/sessions/{sessionId}/messages")
    public ChatMessageOutput sendMessage(
            @PathVariable String sessionId,
            @Valid @RequestBody SendMessageInput request) {

        log.info("Sending message to session: {} from user: {}",
                sessionId, request.getUserId());

        // 确保路径参数和请求体中的会话ID一致
        request.setSessionId(sessionId);
        return sendMessageUseCase.execute(request);
    }

    /**
     * 流式聊天接口（SSE）
     * 支持Server-Sent Events实时推送AI回复，接入真实的AI对话服务
     *
     * @param sessionId 会话ID
     * @param message 用户消息内容
     * @param userId 用户ID
     * @param showThinking 是否显示思考状态
     * @param typingSpeed 打字速度（毫秒/字符）
     * @return SSE流式响应
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChat(
            @RequestParam String sessionId,
            @RequestParam String message,
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "true") boolean showThinking,
            @RequestParam(defaultValue = "100") int typingSpeed) {

        log.info("Starting SSE chat stream for session: {} from user: {}", sessionId, userId);

        // 构建流式聊天输入
        StreamChatInput input = StreamChatInput.builder()
                .sessionId(sessionId)
                .content(message)
                .userId(userId)
                .showThinking(showThinking)
                .typingSpeed(typingSpeed)
                .showCompleted(true)
                .build();

        // 调用流式聊天UseCase
        return streamChatUseCase.executeStreamChat(input);
    }

  
    /**
     * 处理SSE超时
     */
    @GetMapping(value = "/stream/timeout", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamWithTimeout() {
        // 设置30秒超时的SSE
        SseEmitter emitter = new SseEmitter(30000L);

        emitter.onTimeout(() -> {
            log.warn("SSE connection timed out");
            emitter.complete();
        });

        emitter.onCompletion(() -> {
            log.info("SSE connection completed");
        });

        return emitter;
    }

    /**
     * 获取快速提示词列表
     * 基于已注册的业务函数提供用户友好的提示词选项
     *
     * @return 快速提示词列表
     */
    @GetMapping("/quick-prompts")
    public List<QuickPromptOutput> getQuickPrompts() {
        log.debug("Getting quick prompts for chat interface");

        return getQuickPromptsUseCase.execute();
    }
}