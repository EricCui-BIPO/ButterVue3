package com.i0.agents.domain.repositories;

import com.i0.agents.domain.entities.ChatSession;
import com.i0.agents.domain.enums.SessionStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 聊天会话Repository接口
 * 定义聊天会话的数据访问操作
 */
public interface ChatSessionRepository {
    /**
     * 保存聊天会话
     *
     * @param chatSession 聊天会话
     * @return 保存后的聊天会话
     */
    ChatSession save(ChatSession chatSession);

    /**
     * 根据ID查找聊天会话
     *
     * @param id 会话ID
     * @return 聊天会话，如果不存在则返回空
     */
    Optional<ChatSession> findById(String id);

    /**
     * 根据用户ID查找所有活跃会话
     *
     * @param userId 用户ID
     * @return 用户的所有活跃会话
     */
    List<ChatSession> findActiveByUserId(String userId);

    /**
     * 根据状态查找会话
     *
     * @param status 会话状态
     * @return 指定状态的所有会话
     */
    List<ChatSession> findByStatus(SessionStatus status);

    /**
     * 根据用户ID和状态查找会话
     *
     * @param userId 用户ID
     * @param status 会话状态
     * @return 指定用户和状态的所有会话
     */
    List<ChatSession> findByUserIdAndStatus(String userId, SessionStatus status);

    /**
     * 查找指定时间之后创建的会话
     *
     * @param after 创建时间
     * @return 指定时间之后创建的所有会话
     */
    List<ChatSession> findByCreatedAtAfter(LocalDateTime after);

    /**
     * 查找指定时间之后更新的会话
     *
     * @param after 更新时间
     * @return 指定时间之后更新的所有会话
     */
    List<ChatSession> findByUpdatedAtAfter(LocalDateTime after);

    /**
     * 根据标题关键词搜索会话
     *
     * @param userId 用户ID
     * @param keyword 关键词
     * @return 包含关键词的所有会话
     */
    List<ChatSession> findByTitleContaining(String userId, String keyword);

    /**
     * 检查会话是否存在
     *
     * @param id 会话ID
     * @return 如果存在返回true，否则返回false
     */
    boolean existsById(String id);

    /**
     * 删除会话
     *
     * @param id 会话ID
     */
    void deleteById(String id);

    /**
     * 批量删除会话
     *
     * @param ids 会话ID列表
     */
    void deleteByIds(List<String> ids);

    /**
     * 统计用户会话总数
     *
     * @param userId 用户ID
     * @return 会话总数
     */
    long countByUserId(String userId);

    /**
     * 统计指定状态的会话数
     *
     * @param status 会话状态
     * @return 会话数
     */
    long countByStatus(SessionStatus status);

    /**
     * 查找最近的会话
     *
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 最近的会话列表
     */
    List<ChatSession> findRecentByUserId(String userId, int limit);
}