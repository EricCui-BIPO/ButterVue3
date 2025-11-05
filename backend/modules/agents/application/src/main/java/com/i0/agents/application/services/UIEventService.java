package com.i0.agents.application.services;

import com.i0.agents.application.dto.output.StreamChatEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

/**
 * UI事件发送服务
 * 职责：发送SSE事件，处理事件格式化
 */
@Slf4j
@Service
public class UIEventService {

    /**
     * 发送UI Component事件
     *
     * @param uiComponent UI组件信息
     * @param data 附加数据
     * @param sessionId 会话ID
     * @param emitter SSE发射器（可为null）
     */
    public void sendUIComponentEvent(Object uiComponent, Map<String, Object> data, String sessionId, SseEmitter emitter) {
        try {
            // 创建UI Component事件
            StreamChatEvent uiEvent = StreamChatEvent.uiComponent(uiComponent, data, sessionId);

            // 记录事件信息
            log.info("Sending UI Component event for session: {}, eventType: {}",
                    sessionId, uiEvent.getEventType());

            // 如果提供了SSE发射器，直接推送事件到前端
            if (emitter != null) {
                try {
                    emitter.send(SseEmitter.event()
                            .name(uiEvent.getEventType())
                            .data(uiEvent.toJson()));
                    log.debug("UI Component event sent via SSE for session: {}", sessionId);
                } catch (IOException e) {
                    log.warn("Failed to send UI Component event via SSE for session: {}, fallback to logging",
                            sessionId, e);
                }
            }

            // 记录事件到日志（无论是否通过SSE发送）
            log.debug("UI Component event created: {}", uiEvent.toJson());

        } catch (Exception e) {
            log.error("Failed to send UI Component event for session: {}", sessionId, e);
        }
    }

    /**
     * 发送状态事件
     *
     * @param content 事件内容
     * @param sessionId 会话ID
     * @param emitter SSE发射器（可为null）
     */
    public void sendStatusEvent(String content, String sessionId, SseEmitter emitter) {
        sendEvent(StreamChatEvent.status(content, sessionId), sessionId, emitter);
    }

    /**
     * 发送消息事件
     *
     * @param contentType 内容类型
     * @param content 事件内容
     * @param sessionId 会话ID
     * @param emitter SSE发射器（可为null）
     */
    public void sendMessageEvent(String contentType, String content, String sessionId, SseEmitter emitter) {
        sendEvent(StreamChatEvent.message(contentType, content, sessionId), sessionId, emitter);
    }

    /**
     * 发送完成事件
     *
     * @param sessionId 会话ID
     * @param emitter SSE发射器（可为null）
     */
    public void sendCompletedEvent(String sessionId, SseEmitter emitter) {
        sendEvent(StreamChatEvent.completed(sessionId), sessionId, emitter);
    }

    /**
     * 发送错误事件
     *
     * @param errorMessage 错误消息
     * @param sessionId 会话ID
     * @param emitter SSE发射器（可为null）
     */
    public void sendErrorEvent(String errorMessage, String sessionId, SseEmitter emitter) {
        sendEvent(StreamChatEvent.error(errorMessage, sessionId), sessionId, emitter);
    }

    /**
     * 通用事件发送方法
     *
     * @param event 事件对象
     * @param sessionId 会话ID
     * @param emitter SSE发射器（可为null）
     */
    private void sendEvent(StreamChatEvent event, String sessionId, SseEmitter emitter) {
        try {
            log.debug("Sending event for session: {}, eventType: {}", sessionId, event.getEventType());

            if (emitter != null) {
                try {
                    emitter.send(SseEmitter.event()
                            .name(event.getEventType())
                            .data(event.toJson()));
                    log.debug("Event sent via SSE for session: {}", sessionId);
                } catch (IOException e) {
                    log.warn("Failed to send event via SSE for session: {}, fallback to logging",
                            sessionId, e);
                }
            }

            log.debug("Event created: {}", event.toJson());

        } catch (Exception e) {
            log.error("Failed to send event for session: {}", sessionId, e);
        }
    }
}