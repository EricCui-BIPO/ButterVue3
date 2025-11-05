package com.i0.entity.application.usecases;

import com.i0.entity.application.dto.input.CreateEntityInput;
import com.i0.entity.application.dto.output.EntityOutput;
import com.i0.entity.domain.entities.Entity;
import com.i0.entity.domain.repositories.EntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 创建实体用例
 * 负责实体的创建业务逻辑
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CreateEntityUseCase {

    private final EntityRepository entityRepository;

    /**
     * 执行创建实体用例
     * @param request 创建请求
     * @return 创建的实体响应
     */
    public EntityOutput execute(CreateEntityInput request) {
        log.info("Creating entity with name: {}, type: {}", request.getName(), request.getEntityType());

        // 验证实体名称唯一性
        if (entityRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("实体名称已存在: " + request.getName());
        }

        // 验证实体代码唯一性（如果提供了代码）
        if (request.getCode() != null && !request.getCode().trim().isEmpty()) {
            if (entityRepository.existsByCode(request.getCode())) {
                throw new IllegalArgumentException("实体代码已存在: " + request.getCode());
            }
        }

        // 创建实体
        Entity entity = Entity.create(
                request.getName(),
                request.getEntityType(),
                request.getCode(),
                request.getDescription()
        );

        // 生成ID
        entity.setId(UUID.randomUUID().toString());

        // 保存实体
        Entity savedEntity = entityRepository.save(entity);

        log.info("Entity created successfully with id: {}", savedEntity.getId());
        return EntityOutput.from(savedEntity);
    }
}