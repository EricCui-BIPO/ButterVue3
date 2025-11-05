package com.i0.entity.application.usecases;

import com.i0.entity.application.dto.output.EntityOutput;
import com.i0.entity.domain.entities.Entity;
import com.i0.entity.domain.repositories.EntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 通过代码查询实体用例
 * 支持精确匹配和多个结果处理
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FindEntityByCodeUseCase {

    private final EntityRepository entityRepository;

    /**
     * 通过代码精确查询实体
     * @param code 实体代码
     * @return 实体响应
     * @throws IllegalArgumentException 如果未找到实体或找到多个实体
     */
    public EntityOutput execute(String code) {
        log.debug("Finding entity by code: {}", code);

        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("实体代码不能为空");
        }

        // 使用精确匹配查找实体
        List<Entity> entities = entityRepository.findAll()
                .stream()
                .filter(entity -> code.equals(entity.getCode()))
                .collect(Collectors.toList());

        if (entities.isEmpty()) {
            throw new IllegalArgumentException("未找到代码为 '" + code + "' 的实体");
        }

        if (entities.size() > 1) {
            // 如果找到多个相同代码的实体，返回错误信息
            StringBuilder errorMsg = new StringBuilder("找到多个代码为 '").append(code).append("' 的实体，请提供更具体的信息：\n");
            for (int i = 0; i < entities.size(); i++) {
                Entity entity = entities.get(i);
                errorMsg.append(String.format("%d. %s (ID: %s, 类型: %s)\n",
                    i + 1,
                    entity.getName(),
                    entity.getId(),
                    entity.getEntityType().getDisplayName()));
            }
            throw new IllegalArgumentException(errorMsg.toString());
        }

        return EntityOutput.from(entities.get(0));
    }
}