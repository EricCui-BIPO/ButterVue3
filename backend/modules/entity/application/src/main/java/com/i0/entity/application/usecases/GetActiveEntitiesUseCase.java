package com.i0.entity.application.usecases;

import com.i0.entity.application.dto.output.EntityOutput;
import com.i0.entity.domain.entities.Entity;
import com.i0.entity.domain.repositories.EntityRepository;
import com.i0.entity.domain.valueobjects.EntityType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 获取激活实体用例
 * 负责获取激活实体的业务逻辑
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GetActiveEntitiesUseCase {

    private final EntityRepository entityRepository;

    /**
     * 根据实体类型获取所有激活的实体
     * @param entityType 实体类型
     * @return 实体列表
     */
    public List<EntityOutput> getByType(EntityType entityType) {
        log.debug("Getting active entities by type: {}", entityType);

        List<Entity> entities = entityRepository.findActiveByEntityType(entityType);

        return entities.stream()
                .map(EntityOutput::from)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有激活的实体
     * @return 实体列表
     */
    public List<EntityOutput> getAll() {
        log.debug("Getting all active entities");

        List<Entity> entities = entityRepository.findAllActive();

        return entities.stream()
                .map(EntityOutput::from)
                .collect(Collectors.toList());
    }

    /**
     * 根据实体类型获取激活的实体（如果类型为null则返回所有激活实体）
     * @param entityType 实体类型（可选）
     * @return 实体列表
     */
    public List<EntityOutput> getByTypeOrNull(EntityType entityType) {
        if (entityType != null) {
            return getByType(entityType);
        } else {
            return getAll();
        }
    }
}