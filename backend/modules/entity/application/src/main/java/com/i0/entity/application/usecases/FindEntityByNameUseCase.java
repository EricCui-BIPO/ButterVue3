package com.i0.entity.application.usecases;

import com.i0.entity.application.dto.output.EntityOutput;
import com.i0.entity.domain.entities.Entity;
import com.i0.entity.domain.repositories.EntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 通过名称查询实体用例
 * 支持精确匹配、模糊匹配和多个结果处理
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FindEntityByNameUseCase {

    private final EntityRepository entityRepository;

    /**
     * 通过名称查询实体
     * 优先精确匹配，如果没有找到则进行模糊匹配
     * @param name 实体名称
     * @return 实体响应
     * @throws IllegalArgumentException 如果未找到实体或需要更具体信息
     */
    public EntityOutput execute(String name) {
        log.debug("Finding entity by name: {}", name);

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("实体名称不能为空");
        }

        String trimmedName = name.trim();

        // 1. 首先尝试精确匹配
        Entity exactMatch = findExactMatch(trimmedName);
        if (exactMatch != null) {
            return EntityOutput.from(exactMatch);
        }

        // 2. 如果精确匹配没找到，尝试模糊匹配
        List<Entity> fuzzyMatches = findFuzzyMatches(trimmedName);

        if (fuzzyMatches.isEmpty()) {
            throw new IllegalArgumentException("未找到名称包含 '" + trimmedName + "' 的实体");
        }

        if (fuzzyMatches.size() == 1) {
            return EntityOutput.from(fuzzyMatches.get(0));
        }

        // 3. 如果找到多个模糊匹配，返回详细信息让用户选择
        StringBuilder errorMsg = new StringBuilder("找到多个名称包含 '").append(trimmedName).append("' 的实体，请提供更具体的名称或使用代码查询：\n");
        for (int i = 0; i < fuzzyMatches.size(); i++) {
            Entity entity = fuzzyMatches.get(i);
            errorMsg.append(String.format("%d. %s (代码: %s, ID: %s, 类型: %s)\n",
                i + 1,
                entity.getName(),
                entity.getCode() != null ? entity.getCode() : "无",
                entity.getId(),
                entity.getEntityType().getDisplayName()));
        }
        throw new IllegalArgumentException(errorMsg.toString());
    }

    /**
     * 精确匹配实体名称
     */
    private Entity findExactMatch(String name) {
        return entityRepository.findAll()
                .stream()
                .filter(entity -> name.equals(entity.getName()))
                .findFirst()
                .orElse(null);
    }

    /**
     * 模糊匹配实体名称
     */
    private List<Entity> findFuzzyMatches(String namePattern) {
        return entityRepository.findByNameContaining(namePattern);
    }
}