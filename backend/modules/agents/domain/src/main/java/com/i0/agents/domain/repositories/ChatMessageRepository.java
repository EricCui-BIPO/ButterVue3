package com.i0.agents.domain.repositories;

import com.i0.agents.domain.entities.ChatMessage;
import com.i0.agents.domain.enums.MessageRole;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 聊天消息Repository接口
 * 定义聊天消息的数据访问操作
 */
public interface ChatMessageRepository {
    /**
     * 保存聊天消息
     *
     * @param chatMessage 聊天消息
     * @return 保存后的聊天消息
     */
    ChatMessage save(ChatMessage chatMessage);

    /**
     * 批量保存聊天消息
     *
     * @param messages 聊天消息列表
     * @return 保存后的聊天消息列表
     */
    List<ChatMessage> saveAll(List<ChatMessage> messages);

    /**
     * 根据ID查找聊天消息
     *
     * @param id 消息ID
     * @return 聊天消息，如果不存在则返回空
     */
    Optional<ChatMessage> findById(String id);

    /**
     * 根据会话ID查找所有消息
     *
     * @param sessionId 会话ID
     * @return 会话的所有消息，按时间顺序排列
     */
    List<ChatMessage> findBySessionId(String sessionId);

    /**
     * 根据会话ID和角色查找消息
     *
     * @param sessionId 会话ID
     * @param role 消息角色
     * @return 指定角色的所有消息
     */
    List<ChatMessage> findBySessionIdAndRole(String sessionId, MessageRole role);

    /**
     * 根据会话ID和时间范围查找消息
     *
     * @param sessionId 会话ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 时间范围内的所有消息
     */
    List<ChatMessage> findBySessionIdAndTimestampBetween(String sessionId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据会话ID查找最近N条消息
     *
     * @param sessionId 会话ID
     * @param limit 限制数量
     * @return 最近的N条消息
     */
    List<ChatMessage> findRecentBySessionId(String sessionId, int limit);

    /**
     * 根据父消息ID查找子消息
     *
     * @param parentMessageId 父消息ID
     * @return 所有子消息
     */
    List<ChatMessage> findByParentMessageId(String parentMessageId);

    /**
     * 根据内容关键词搜索消息
     *
     * @param sessionId 会话ID
     * @param keyword 关键词
     * @return 包含关键词的所有消息
     */
    List<ChatMessage> findByContentContaining(String sessionId, String keyword);

    /**
     * 检查消息是否存在
     *
     * @param id 消息ID
     * @return 如果存在返回true，否则返回false
     */
    boolean existsById(String id);

    /**
     * 统计会话的消息总数
     *
     * @param sessionId 会话ID
     * @return 消息总数
     */
    long countBySessionId(String sessionId);

    /**
     * 统计会话中指定角色的消息数
     *
     * @param sessionId 会话ID
     * @param role 消息角色
     * @return 指定角色的消息数
     */
    long countBySessionIdAndRole(String sessionId, MessageRole role);

    /**
     * 删除会话的所有消息
     *
     * @param sessionId 会话ID
     */
    void deleteBySessionId(String sessionId);

    /**
     * 删除消息
     *
     * @param id 消息ID
     */
    void deleteById(String id);

    /**
     * 批量删除消息
     *
     * @param ids 消息ID列表
     */
    void deleteByIds(List<String> ids);

    /**
     * 查找会话的最后一条消息
     *
     * @param sessionId 会话ID
     * @return 最后一条消息，如果不存在则返回空
     */
    Optional<ChatMessage> findLastBySessionId(String sessionId);

    /**
     * 查找会话的最后一条用户消息
     *
     * @param sessionId 会话ID
     * @return 最后一条用户消息，如果不存在则返回空
     */
    Optional<ChatMessage> findLastUserMessageBySessionId(String sessionId);

    /**
     * 查找会话的最后一条助手消息
     *
     * @param sessionId 会话ID
     * @return 最后一条助手消息，如果不存在则返回空
     */
    Optional<ChatMessage> findLastAssistantMessageBySessionId(String sessionId);
}