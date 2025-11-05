package com.i0.entity.application.usecases;

import com.i0.entity.application.dto.output.EntityOutput;
import com.i0.entity.application.dto.input.UpdateEntityInput;
import com.i0.entity.domain.entities.Entity;
import com.i0.entity.domain.repositories.EntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 更新实体用例
 * 负责更新实体信息的业务逻辑
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateEntityUseCase {

    private final EntityRepository entityRepository;

    /**
     * 执行更新实体用例
     * @param id 实体ID
     * @param request 更新请求
     * @return 更新后的实体响应
     */
    public EntityOutput execute(String id, UpdateEntityInput request) {
        log.info("Updating entity with id: {}", id);

        // 查找实体
        Entity entity = entityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("实体不存在: " + id));

        // 验证实体名称唯一性（排除当前实体）
        if (entityRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new IllegalArgumentException("实体名称已存在: " + request.getName());
        }

        // 更新实体
        entity.update(request.getName(), request.getDescription());

        // 保存实体
        Entity savedEntity = entityRepository.save(entity);

        log.info("Entity updated successfully with id: {}", savedEntity.getId());
        return EntityOutput.from(savedEntity);
    }
}