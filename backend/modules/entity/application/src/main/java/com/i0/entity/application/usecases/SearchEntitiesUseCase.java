package com.i0.entity.application.usecases;

import com.i0.entity.application.dto.output.EntityPageOutput;
import com.i0.entity.application.dto.input.EntityPageInput;
import com.i0.entity.domain.entities.Entity;
import com.i0.entity.domain.repositories.EntityRepository;
import com.i0.domain.core.pagination.Pageable;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;



/**
 * 搜索实体用例
 * 负责分页查询实体的业务逻辑
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SearchEntitiesUseCase {

    private final EntityRepository entityRepository;

    /**
     * 执行搜索实体用例
     * @param request 分页查询请求
     * @return 分页响应
     */
    public Pageable<EntityPageOutput> execute(EntityPageInput request) {
        log.debug("Searching entities with page: {}, size: {}, entityType: {}, nameKeyword: {}, activeOnly: {}",
                request.getPage(), request.getSize(), request.getEntityType(),
                request.getNameKeyword(), request.getActiveOnly());

        // 业务逻辑：验证请求参数
        validateSearchRequest(request);
        
        // 业务逻辑：标准化搜索条件
        String normalizedKeyword = normalizeNameKeyword(request.getNameKeyword());
        
        // 委托给repository层进行数据查询
        Pageable<Entity> entityPage = entityRepository.searchEntities(
                normalizedKeyword,
                request.getEntityType(),
                request.getActiveOnly(),
                request.getPage(),
                request.getSize()
        );

        // 业务逻辑：转换为应用层DTO并返回分页结果
        Pageable<EntityPageOutput> result = entityPage.map(EntityPageOutput::from);
        log.debug("Found {} entities, total: {}", result.getNumberOfElements(), result.getTotal());
        return result;
    }

    /**
     * 验证搜索请求参数
     * 业务逻辑：确保请求参数的有效性
     */
    private void validateSearchRequest(EntityPageInput request) {
        if (request.getPage() < 0) {
            throw new IllegalArgumentException("页码不能为负数");
        }
        if (request.getSize() <= 0) {
            throw new IllegalArgumentException("每页大小必须大于0");
        }
        if (request.getSize() > 1000) {
            throw new IllegalArgumentException("每页大小不能超过1000");
        }
    }
    
    /**
     * 标准化名称关键字
     * 业务逻辑：对搜索关键字进行标准化处理
     */
    private String normalizeNameKeyword(String nameKeyword) {
        if (nameKeyword == null) {
            return null;
        }
        String normalized = nameKeyword.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}