package com.i0.entity.application.usecases;

import com.i0.entity.domain.entities.Entity;
import com.i0.entity.domain.repositories.EntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 删除实体用例
 * 负责删除实体的业务逻辑
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteEntityUseCase {

    private final EntityRepository entityRepository;

    /**
     * 执行删除实体用例
     * @param id 实体ID
     */
    public void execute(String id) {
        log.info("Deleting entity with id: {}", id);

        Entity entity = entityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("实体不存在: " + id));

        entityRepository.delete(entity);

        log.info("Entity deleted successfully with id: {}", id);
    }
}