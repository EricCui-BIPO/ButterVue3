package com.i0.agents.gateway.persistence.repositories;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.i0.agents.domain.entities.ChatSession;
import com.i0.agents.domain.enums.SessionStatus;
import com.i0.agents.domain.repositories.ChatSessionRepository;
import com.i0.agents.gateway.persistence.dataobjects.ChatSessionDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 聊天会话Repository实现
 */
@Slf4j
@Repository
public class ChatSessionRepositoryImpl extends com.baomidou.mybatisplus.extension.service.impl.ServiceImpl<com.i0.agents.gateway.persistence.mappers.ChatSessionMapper, ChatSessionDO>
        implements ChatSessionRepository {

    @Override
    public ChatSession save(ChatSession chatSession) {
        log.debug("Saving chat session: {}", chatSession.getId());

        ChatSessionDO chatSessionDO = ChatSessionDO.from(chatSession);

        if (chatSession.getId() == null || !existsById(chatSession.getId())) {
            // 新增
            save(chatSessionDO);
        } else {
            // 更新
            updateById(chatSessionDO);
        }

        return chatSessionDO.toDomain();
    }

    @Override
    public Optional<ChatSession> findById(String id) {
        log.debug("Finding chat session by id: {}", id);

        ChatSessionDO chatSessionDO = getById(id);
        return Optional.ofNullable(chatSessionDO)
                .map(ChatSessionDO::toDomain);
    }

    @Override
    public List<ChatSession> findActiveByUserId(String userId) {
        log.debug("Finding active chat sessions by userId: {}", userId);

        LambdaQueryWrapper<ChatSessionDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatSessionDO::getUserId, userId)
                .eq(ChatSessionDO::getStatus, SessionStatus.ACTIVE.name())
                .eq(ChatSessionDO::getIsDeleted, false)
                .orderByDesc(ChatSessionDO::getUpdatedAt);

        List<ChatSessionDO> chatSessionDOs = list(queryWrapper);
        return chatSessionDOs.stream()
                .map(ChatSessionDO::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChatSession> findByStatus(SessionStatus status) {
        log.debug("Finding chat sessions by status: {}", status);

        LambdaQueryWrapper<ChatSessionDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatSessionDO::getStatus, status.name())
                .eq(ChatSessionDO::getIsDeleted, false)
                .orderByDesc(ChatSessionDO::getUpdatedAt);

        List<ChatSessionDO> chatSessionDOs = list(queryWrapper);
        return chatSessionDOs.stream()
                .map(ChatSessionDO::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChatSession> findByUserIdAndStatus(String userId, SessionStatus status) {
        log.debug("Finding chat sessions by userId: {} and status: {}", userId, status);

        LambdaQueryWrapper<ChatSessionDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatSessionDO::getUserId, userId)
                .eq(ChatSessionDO::getStatus, status.name())
                .eq(ChatSessionDO::getIsDeleted, false)
                .orderByDesc(ChatSessionDO::getUpdatedAt);

        List<ChatSessionDO> chatSessionDOs = list(queryWrapper);
        return chatSessionDOs.stream()
                .map(ChatSessionDO::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChatSession> findByCreatedAtAfter(LocalDateTime after) {
        log.debug("Finding chat sessions created after: {}", after);

        LambdaQueryWrapper<ChatSessionDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ge(ChatSessionDO::getCreatedAt, after)
                .eq(ChatSessionDO::getIsDeleted, false)
                .orderByDesc(ChatSessionDO::getCreatedAt);

        List<ChatSessionDO> chatSessionDOs = list(queryWrapper);
        return chatSessionDOs.stream()
                .map(ChatSessionDO::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChatSession> findByUpdatedAtAfter(LocalDateTime after) {
        log.debug("Finding chat sessions updated after: {}", after);

        LambdaQueryWrapper<ChatSessionDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ge(ChatSessionDO::getUpdatedAt, after)
                .eq(ChatSessionDO::getIsDeleted, false)
                .orderByDesc(ChatSessionDO::getUpdatedAt);

        List<ChatSessionDO> chatSessionDOs = list(queryWrapper);
        return chatSessionDOs.stream()
                .map(ChatSessionDO::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChatSession> findByTitleContaining(String userId, String keyword) {
        log.debug("Finding chat sessions by userId: {} and title keyword: {}", userId, keyword);

        LambdaQueryWrapper<ChatSessionDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatSessionDO::getUserId, userId)
                .like(ChatSessionDO::getTitle, keyword)
                .eq(ChatSessionDO::getIsDeleted, false)
                .orderByDesc(ChatSessionDO::getUpdatedAt);

        List<ChatSessionDO> chatSessionDOs = list(queryWrapper);
        return chatSessionDOs.stream()
                .map(ChatSessionDO::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(String id) {
        log.debug("Checking if chat session exists by id: {}", id);
        return lambdaQuery()
                .eq(ChatSessionDO::getId, id)
                .eq(ChatSessionDO::getIsDeleted, false)
                .exists();
    }

    @Override
    public void deleteById(String id) {
        log.debug("Deleting chat session by id: {}", id);

        // 使用MyBatis-Plus的逻辑删除
        int result = baseMapper.deleteById(id);
        if (result == 0) {
            log.warn("No chat session deleted for id: {}", id);
        }
    }

    @Override
    public void deleteByIds(List<String> ids) {
        log.debug("Deleting chat sessions by ids: {}", ids);

        if (ids != null && !ids.isEmpty()) {
            int result = baseMapper.deleteBatchIds(ids);
            log.info("Deleted {} chat sessions", result);
        }
    }

    @Override
    public long countByUserId(String userId) {
        log.debug("Counting chat sessions by userId: {}", userId);

        return lambdaQuery()
                .eq(ChatSessionDO::getUserId, userId)
                .eq(ChatSessionDO::getIsDeleted, false)
                .count();
    }

    @Override
    public long countByStatus(SessionStatus status) {
        log.debug("Counting chat sessions by status: {}", status);

        return lambdaQuery()
                .eq(ChatSessionDO::getStatus, status.name())
                .eq(ChatSessionDO::getIsDeleted, false)
                .count();
    }

    @Override
    public List<ChatSession> findRecentByUserId(String userId, int limit) {
        log.debug("Finding recent chat sessions by userId: {} limit: {}", userId, limit);

        LambdaQueryWrapper<ChatSessionDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatSessionDO::getUserId, userId)
                .eq(ChatSessionDO::getIsDeleted, false)
                .orderByDesc(ChatSessionDO::getUpdatedAt)
                .last("LIMIT " + limit);

        List<ChatSessionDO> chatSessionDOs = list(queryWrapper);
        return chatSessionDOs.stream()
                .map(ChatSessionDO::toDomain)
                .collect(Collectors.toList());
    }
}