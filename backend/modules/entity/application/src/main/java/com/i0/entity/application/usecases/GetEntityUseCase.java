package com.i0.entity.application.usecases;

import com.i0.entity.application.dto.output.EntityOutput;
import com.i0.entity.domain.entities.Entity;
import com.i0.entity.domain.repositories.EntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 获取实体用例
 * 负责根据ID获取实体详情
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GetEntityUseCase {

    private final EntityRepository entityRepository;

    /**
     * 执行获取实体用例
     * @param id 实体ID
     * @return 实体响应
     */
    public EntityOutput execute(String id) {
        log.debug("Getting entity by id: {}", id);

        Entity entity = entityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("实体不存在: " + id));

        return EntityOutput.from(entity);
    }
}