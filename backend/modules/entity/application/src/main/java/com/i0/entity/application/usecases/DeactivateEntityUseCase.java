package com.i0.entity.application.usecases;

import com.i0.entity.application.dto.output.EntityOutput;
import com.i0.entity.domain.entities.Entity;
import com.i0.entity.domain.repositories.EntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 停用实体用例
 * 负责停用实体的业务逻辑
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeactivateEntityUseCase {

    private final EntityRepository entityRepository;

    /**
     * 执行停用实体用例
     * @param id 实体ID
     * @return 更新后的实体响应
     */
    public EntityOutput execute(String id) {
        log.info("Deactivating entity with id: {}", id);

        Entity entity = entityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("实体不存在: " + id));

        entity.deactivate();
        Entity savedEntity = entityRepository.save(entity);

        log.info("Entity deactivated successfully with id: {}", savedEntity.getId());
        return EntityOutput.from(savedEntity);
    }
}