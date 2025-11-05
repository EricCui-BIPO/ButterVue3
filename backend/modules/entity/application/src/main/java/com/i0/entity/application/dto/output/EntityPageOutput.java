package com.i0.entity.application.dto.output;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity输出DTO
 * 遵循整洁架构规范，作为Application层的输出DTO
 * 只包含前端需要展示的属性
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityPageOutput {

    /**
     * 实体ID
     */
    private String id;

    /**
     * 实体名称
     */
    private String name;

    /**
     * 实体类型
     */
    private String entityType;

    /**
     * 实体代码
     */
    private String code;

    /**
     * 实体描述
     */
    private String description;

    /**
     * 是否激活
     */
    private boolean active;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 从Entity转换为EntityPageOutput
     * @param entity 实体对象
     * @return EntityPageOutput
     */
    public static EntityPageOutput from(com.i0.entity.domain.entities.Entity entity) {
        if (entity == null) {
            return null;
        }

        return EntityPageOutput.builder()
                .id(entity.getId())
                .name(entity.getName())
                .entityType(entity.getEntityType() != null ? entity.getEntityType().name() : null)
                .code(entity.getCode())
                .description(entity.getDescription())
                .active(entity.isActive())
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt() : LocalDateTime.now())
                .updatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt() : LocalDateTime.now())
                .build();
    }
}