package com.i0.agents.gateway.persistence.repositories;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.i0.agents.domain.entities.ChatMessage;
import com.i0.agents.domain.enums.MessageRole;
import com.i0.agents.domain.repositories.ChatMessageRepository;
import com.i0.agents.gateway.persistence.dataobjects.ChatMessageDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 聊天消息Repository实现
 */
@Slf4j
@Repository
public class ChatMessageRepositoryImpl extends com.baomidou.mybatisplus.extension.service.impl.ServiceImpl<com.i0.agents.gateway.persistence.mappers.ChatMessageMapper, ChatMessageDO>
        implements ChatMessageRepository {

    private final JdbcTemplate jdbcTemplate;

    public ChatMessageRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ChatMessage save(ChatMessage chatMessage) {
        log.debug("Saving chat message: {} for session: {}", chatMessage.getId(), chatMessage.getSessionId());

        // 应用层数据一致性检查：确保会话存在
        if (!isSessionExists(chatMessage.getSessionId())) {
            throw new RuntimeException("Chat session not found: " + chatMessage.getSessionId());
        }

        // 应用层数据一致性检查：如果存在父消息ID，确保父消息存在
        if (chatMessage.getParentMessageId() != null && !chatMessage.getParentMessageId().isEmpty()) {
            if (!existsById(chatMessage.getParentMessageId())) {
                log.warn("Parent message not found: {} for message: {}", chatMessage.getParentMessageId(), chatMessage.getId());
                // 可以选择抛出异常或者继续处理，这里仅记录警告
            }
        }

        ChatMessageDO chatMessageDO = ChatMessageDO.from(chatMessage);

        // 使用会话ID级别的同步锁，避免同一会话的并发插入冲突
        synchronized (getSessionLock(chatMessage.getSessionId())) {
            save(chatMessageDO);
        }

        return chatMessageDO.toDomain();
    }

    /**
     * 检查会话是否存在（应用层外键约束检查）
     */
    private boolean isSessionExists(String sessionId) {
        try {
            // 这里需要注入ChatSessionRepository或者通过JdbcTemplate查询
            // 为了避免循环依赖，使用JdbcTemplate直接查询
            String sql = "SELECT COUNT(*) FROM chat_sessions WHERE id = ? AND is_deleted = false";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, sessionId);
            return count != null && count > 0;
        } catch (Exception e) {
            log.error("Failed to check session existence: {}", sessionId, e);
            return false;
        }
    }

    /**
     * 获取会话级别的锁对象
     */
    private Object getSessionLock(String sessionId) {
        // 简单的会话锁机制，避免同一会话的并发冲突
        // 注意：这是一个简单的内存锁，在分布式环境下需要使用分布式锁
        return sessionLocks.computeIfAbsent(sessionId, id -> new Object());
    }

    // 会话锁映射表（内存级别）
    private static final java.util.concurrent.ConcurrentHashMap<String, Object> sessionLocks =
            new java.util.concurrent.ConcurrentHashMap<>();

    @Override
    public List<ChatMessage> saveAll(List<ChatMessage> messages) {
        log.debug("Saving {} chat messages", messages.size());

        List<ChatMessageDO> chatMessageDOs = messages.stream()
                .map(ChatMessageDO::from)
                .collect(Collectors.toList());

        saveBatch(chatMessageDOs);

        return chatMessageDOs.stream()
                .map(ChatMessageDO::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ChatMessage> findById(String id) {
        log.debug("Finding chat message by id: {}", id);

        ChatMessageDO chatMessageDO = getById(id);
        return Optional.ofNullable(chatMessageDO)
                .map(ChatMessageDO::toDomain);
    }

    @Override
    public List<ChatMessage> findBySessionId(String sessionId) {
        log.debug("Finding chat messages by sessionId: {}", sessionId);

        LambdaQueryWrapper<ChatMessageDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatMessageDO::getSessionId, sessionId)
                .eq(ChatMessageDO::getIsDeleted, false)
                .orderByAsc(ChatMessageDO::getTimestamp);

        List<ChatMessageDO> chatMessageDOs = list(queryWrapper);
        return chatMessageDOs.stream()
                .map(ChatMessageDO::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChatMessage> findBySessionIdAndRole(String sessionId, MessageRole role) {
        log.debug("Finding chat messages by sessionId: {} and role: {}", sessionId, role);

        LambdaQueryWrapper<ChatMessageDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatMessageDO::getSessionId, sessionId)
                .eq(ChatMessageDO::getRole, role.name())
                .eq(ChatMessageDO::getIsDeleted, false)
                .orderByAsc(ChatMessageDO::getTimestamp);

        List<ChatMessageDO> chatMessageDOs = list(queryWrapper);
        return chatMessageDOs.stream()
                .map(ChatMessageDO::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChatMessage> findBySessionIdAndTimestampBetween(String sessionId, LocalDateTime startTime, LocalDateTime endTime) {
        log.debug("Finding chat messages by sessionId: {} between {} and {}", sessionId, startTime, endTime);

        LambdaQueryWrapper<ChatMessageDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatMessageDO::getSessionId, sessionId)
                .ge(ChatMessageDO::getTimestamp, startTime)
                .le(ChatMessageDO::getTimestamp, endTime)
                .eq(ChatMessageDO::getIsDeleted, false)
                .orderByAsc(ChatMessageDO::getTimestamp);

        List<ChatMessageDO> chatMessageDOs = list(queryWrapper);
        return chatMessageDOs.stream()
                .map(ChatMessageDO::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChatMessage> findRecentBySessionId(String sessionId, int limit) {
        log.debug("Finding recent chat messages by sessionId: {} limit: {}", sessionId, limit);

        LambdaQueryWrapper<ChatMessageDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatMessageDO::getSessionId, sessionId)
                .eq(ChatMessageDO::getIsDeleted, false)
                .orderByDesc(ChatMessageDO::getTimestamp)
                .last("LIMIT " + limit);

        List<ChatMessageDO> chatMessageDOs = list(queryWrapper);
        return chatMessageDOs.stream()
                .map(ChatMessageDO::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChatMessage> findByParentMessageId(String parentMessageId) {
        log.debug("Finding chat messages by parentMessageId: {}", parentMessageId);

        LambdaQueryWrapper<ChatMessageDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatMessageDO::getParentMessageId, parentMessageId)
                .eq(ChatMessageDO::getIsDeleted, false)
                .orderByAsc(ChatMessageDO::getTimestamp);

        List<ChatMessageDO> chatMessageDOs = list(queryWrapper);
        return chatMessageDOs.stream()
                .map(ChatMessageDO::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChatMessage> findByContentContaining(String sessionId, String keyword) {
        log.debug("Finding chat messages by sessionId: {} with keyword: {}", sessionId, keyword);

        LambdaQueryWrapper<ChatMessageDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatMessageDO::getSessionId, sessionId)
                .like(ChatMessageDO::getContent, keyword)
                .eq(ChatMessageDO::getIsDeleted, false)
                .orderByAsc(ChatMessageDO::getTimestamp);

        List<ChatMessageDO> chatMessageDOs = list(queryWrapper);
        return chatMessageDOs.stream()
                .map(ChatMessageDO::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(String id) {
        log.debug("Checking if chat message exists by id: {}", id);
        return lambdaQuery()
                .eq(ChatMessageDO::getId, id)
                .eq(ChatMessageDO::getIsDeleted, false)
                .exists();
    }

    @Override
    public long countBySessionId(String sessionId) {
        log.debug("Counting chat messages by sessionId: {}", sessionId);

        return lambdaQuery()
                .eq(ChatMessageDO::getSessionId, sessionId)
                .eq(ChatMessageDO::getIsDeleted, false)
                .count();
    }

    @Override
    public long countBySessionIdAndRole(String sessionId, MessageRole role) {
        log.debug("Counting chat messages by sessionId: {} and role: {}", sessionId, role);

        return lambdaQuery()
                .eq(ChatMessageDO::getSessionId, sessionId)
                .eq(ChatMessageDO::getRole, role.name())
                .eq(ChatMessageDO::getIsDeleted, false)
                .count();
    }

    @Override
    public void deleteBySessionId(String sessionId) {
        log.debug("Deleting chat messages by sessionId: {}", sessionId);

        LambdaQueryWrapper<ChatMessageDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatMessageDO::getSessionId, sessionId);
        remove(queryWrapper);
    }

    @Override
    public void deleteById(String id) {
        log.debug("Deleting chat message by id: {}", id);

        // 使用MyBatis-Plus的逻辑删除
        int result = baseMapper.deleteById(id);
        if (result == 0) {
            log.warn("No chat message deleted for id: {}", id);
        }
    }

    @Override
    public void deleteByIds(List<String> ids) {
        log.debug("Deleting chat messages by ids: {}", ids);

        if (ids != null && !ids.isEmpty()) {
            int result = baseMapper.deleteBatchIds(ids);
            log.info("Deleted {} chat messages", result);
        }
    }

    @Override
    public Optional<ChatMessage> findLastBySessionId(String sessionId) {
        log.debug("Finding last chat message by sessionId: {}", sessionId);

        LambdaQueryWrapper<ChatMessageDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatMessageDO::getSessionId, sessionId)
                .eq(ChatMessageDO::getIsDeleted, false)
                .orderByDesc(ChatMessageDO::getTimestamp)
                .last("LIMIT 1");

        ChatMessageDO chatMessageDO = getOne(queryWrapper);
        return Optional.ofNullable(chatMessageDO)
                .map(ChatMessageDO::toDomain);
    }

    @Override
    public Optional<ChatMessage> findLastUserMessageBySessionId(String sessionId) {
        log.debug("Finding last user message by sessionId: {}", sessionId);

        LambdaQueryWrapper<ChatMessageDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatMessageDO::getSessionId, sessionId)
                .eq(ChatMessageDO::getRole, MessageRole.USER.name())
                .eq(ChatMessageDO::getIsDeleted, false)
                .orderByDesc(ChatMessageDO::getTimestamp)
                .last("LIMIT 1");

        ChatMessageDO chatMessageDO = getOne(queryWrapper);
        return Optional.ofNullable(chatMessageDO)
                .map(ChatMessageDO::toDomain);
    }

    @Override
    public Optional<ChatMessage> findLastAssistantMessageBySessionId(String sessionId) {
        log.debug("Finding last assistant message by sessionId: {}", sessionId);

        LambdaQueryWrapper<ChatMessageDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatMessageDO::getSessionId, sessionId)
                .eq(ChatMessageDO::getRole, MessageRole.ASSISTANT.name())
                .eq(ChatMessageDO::getIsDeleted, false)
                .orderByDesc(ChatMessageDO::getTimestamp)
                .last("LIMIT 1");

        ChatMessageDO chatMessageDO = getOne(queryWrapper);
        return Optional.ofNullable(chatMessageDO)
                .map(ChatMessageDO::toDomain);
    }
}