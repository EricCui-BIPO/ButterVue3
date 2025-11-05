package com.i0.agents.domain.entities;

import com.i0.agents.domain.enums.SessionStatus;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * 聊天会话实体
 * 表示一个完整的聊天会话
 */
public class ChatSession {
    private final String id;
    private String title;
    private SessionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private final List<ChatMessage> messages;
    private String userId;

    private ChatSession(String id, String title, SessionStatus status, String userId) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.messages = new ArrayList<>();
    }

    /**
     * 创建新的聊天会话
     */
    public static ChatSession create(String title, String userId) {
        if (StringUtils.isBlank(title)) {
            throw new IllegalArgumentException("会话标题不能为空");
        }

        if (title.length() > 100) {
            throw new IllegalArgumentException("会话标题长度不能超过100字符");
        }

        ChatSession session = new ChatSession(
                UUID.randomUUID().toString(),
                title.trim(),
                SessionStatus.ACTIVE,
                userId
        );

        return session;
    }

    /**
     * 从已有数据重建会话
     */
    public static ChatSession reconstruct(String id, String title, SessionStatus status,
                                         LocalDateTime createdAt, LocalDateTime updatedAt, String userId) {
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("会话ID不能为空");
        }

        ChatSession session = new ChatSession(id, title, status, userId);
        session.createdAt = createdAt;
        session.updatedAt = updatedAt;

        return session;
    }

    /**
     * 添加消息
     */
    public void addMessage(ChatMessage message) {
        if (message == null) {
            throw new IllegalArgumentException("消息不能为空");
        }

        if (!message.getSessionId().equals(this.id)) {
            throw new IllegalArgumentException("消息不属于当前会话");
        }

        if (!status.isInteractive()) {
            throw new IllegalStateException("当前会话状态不允许添加消息");
        }

        this.messages.add(message);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 更新会话状态
     */
    public void updateStatus(SessionStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("会话状态不能为空");
        }

        if (!status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                    String.format("会话状态不能从 %s 转换为 %s", status.getDescription(), newStatus.getDescription())
            );
        }

        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 更新会话标题
     */
    public void updateTitle(String newTitle) {
        if (StringUtils.isBlank(newTitle)) {
            throw new IllegalArgumentException("会话标题不能为空");
        }

        if (newTitle.length() > 100) {
            throw new IllegalArgumentException("会话标题长度不能超过100字符");
        }

        this.title = newTitle.trim();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 更新用户ID
     */
    public void updateUserId(String userId) {
        this.userId = userId;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 暂停会话
     */
    public void pause() {
        updateStatus(SessionStatus.PAUSED);
    }

    /**
     * 激活会话
     */
    public void activate() {
        updateStatus(SessionStatus.ACTIVE);
    }

    /**
     * 完成会话
     */
    public void complete() {
        updateStatus(SessionStatus.COMPLETED);
    }

    /**
     * 关闭会话
     */
    public void close() {
        updateStatus(SessionStatus.CLOSED);
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<ChatMessage> getMessages() {
        return new ArrayList<>(messages);
    }

    public String getUserId() {
        return userId;
    }

    /**
     * 获取消息数量
     */
    public int getMessageCount() {
        return messages.size();
    }

    /**
     * 获取用户消息数量
     */
    public long getUserMessageCount() {
        return messages.stream()
                .filter(ChatMessage::isUserMessage)
                .count();
    }

    /**
     * 获取助手消息数量
     */
    public long getAssistantMessageCount() {
        return messages.stream()
                .filter(ChatMessage::isAssistantMessage)
                .count();
    }

    /**
     * 获取函数调用消息数量
     */
    public long getFunctionCallCount() {
        return messages.stream()
                .filter(ChatMessage::isFunctionMessage)
                .count();
    }

    /**
     * 获取最后一条消息
     */
    public ChatMessage getLastMessage() {
        return messages.isEmpty() ? null : messages.get(messages.size() - 1);
    }

    /**
     * 判断是否有消息
     */
    public boolean hasMessages() {
        return !messages.isEmpty();
    }

    /**
     * 判断是否为活跃状态
     */
    public boolean isActive() {
        return status.isActive();
    }

    /**
     * 判断是否为终止状态
     */
    public boolean isTerminal() {
        return status.isTerminal();
    }

    /**
     * 判断是否为可交互状态
     */
    public boolean isInteractive() {
        return status.isInteractive();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatSession that = (ChatSession) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ChatSession{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", messageCount=" + messages.size() +
                ", createdAt=" + createdAt +
                '}';
    }
}