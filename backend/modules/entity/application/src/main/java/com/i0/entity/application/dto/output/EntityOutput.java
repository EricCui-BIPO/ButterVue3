package com.i0.entity.application.dto.output;

import com.i0.entity.domain.entities.Entity;
import com.i0.entity.domain.valueobjects.EntityType;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity输出DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityOutput {
    
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
    private EntityType entityType;
    
    /**
     * 实体类型显示名称
     */
    private String entityTypeDisplayName;
    
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
    private Boolean active;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 版本号
     */
    private Long version;
    
    /**
     * 从Entity实体转换为EntityOutput
     * @param entity Entity实体
     * @return EntityOutput
     */
    public static EntityOutput from(Entity entity) {
        if (entity == null) {
            return null;
        }
        
        return EntityOutput.builder()
                .id(entity.getId())
                .name(entity.getName())
                .entityType(entity.getEntityType())
                .entityTypeDisplayName(entity.getEntityType() != null ? entity.getEntityType().getDisplayName() : null)
                .code(entity.getCode())
                .description(entity.getDescription())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .version(entity.getVersion())
                .build();
    }
}