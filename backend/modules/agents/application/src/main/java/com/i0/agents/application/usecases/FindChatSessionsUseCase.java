package com.i0.agents.application.usecases;

import com.i0.agents.application.dto.output.ChatSessionOutput;
import com.i0.agents.domain.repositories.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 查询聊天会话列表用例
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FindChatSessionsUseCase {

    private final ChatSessionRepository chatSessionRepository;

    /**
     * 执行查询聊天会话
     *
     * @param userId 用户ID（可选）
     * @param titleKeyword 标题关键字（可选）
     * @return 会话列表
     */
    public List<ChatSessionOutput> execute(String userId, String titleKeyword) {
        log.debug("Finding chat sessions with userId: {}, keyword: {}", userId, titleKeyword);

        // 通过用户ID查找活跃会话（如果提供了用户ID）
        List<com.i0.agents.domain.entities.ChatSession> sessions;
        if (userId != null && !userId.trim().isEmpty()) {
            sessions = chatSessionRepository.findActiveByUserId(userId);
        } else {
            // 如果没有提供用户ID，返回空列表
            sessions = Collections.emptyList();
        }

        // 过滤标题关键字（如果提供了）
        List<ChatSessionOutput> filteredSessions = sessions.stream()
                .filter(session -> titleKeyword == null || titleKeyword.trim().isEmpty() ||
                        session.getTitle().contains(titleKeyword))
                .map(ChatSessionOutput::from)
                .collect(Collectors.toList());

        log.info("Found {} chat sessions", filteredSessions.size());
        return filteredSessions;
    }
}