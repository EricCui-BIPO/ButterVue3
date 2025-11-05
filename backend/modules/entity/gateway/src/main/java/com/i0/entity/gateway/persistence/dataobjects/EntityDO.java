package com.i0.entity.gateway.persistence.dataobjects;

import com.baomidou.mybatisplus.annotation.*;
import com.i0.entity.domain.entities.Entity;
import com.i0.entity.domain.valueobjects.EntityType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Entity数据对象
 * 用于数据库映射
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("entities")
public class EntityDO {
    
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;
    
    /**
     * 实体名称
     */
    @TableField("name")
    private String name;
    
    /**
     * 实体类型
     */
    @TableField("entity_type")
    private String entityType;
    
    /**
     * 实体代码
     */
    @TableField("code")
    private String code;
    
    /**
     * 实体描述
     */
    @TableField("description")
    private String description;
    
    /**
     * 是否激活
     */
    @TableField("is_active")
    private Boolean isActive;
    
    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    /**
     * 是否删除（软删除标记）
     */
    @TableLogic(value = "false", delval = "true")
    @TableField("is_deleted")
    private Boolean isDeleted;
    
    /**
     * 从领域实体转换为数据对象
     * @param entity 领域实体
     * @return 数据对象
     */
    public static EntityDO from(Entity entity) {
        if (entity == null) {
            return null;
        }
        
        return EntityDO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .entityType(entity.getEntityType().name())
                .code(entity.getCode())
                .description(entity.getDescription())
                .isActive(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .isDeleted(false)
                .build();
    }
    
    /**
     * 转换为领域实体
     * @return 领域实体
     */
    public Entity toDomain() {
        Entity entity = new Entity();
        entity.setId(this.id);
        entity.setName(this.name);
        entity.setEntityType(EntityType.valueOf(this.entityType));
        entity.setCode(this.code);
        entity.setDescription(this.description);
        entity.setActive(this.isActive != null ? this.isActive : true);
        entity.setCreatedAt(this.createdAt != null ? this.createdAt : LocalDateTime.now());
        entity.setUpdatedAt(this.updatedAt != null ? this.updatedAt : LocalDateTime.now());
        
        return entity;
    }
    
    /**
     * 更新数据对象字段
     * @param entity 领域实体
     */
    public void updateFrom(Entity entity) {
        if (entity == null) {
            return;
        }
        
        this.name = entity.getName();
        this.entityType = entity.getEntityType().name();
        this.code = entity.getCode();
        this.description = entity.getDescription();
        this.isActive = entity.isActive();
        this.updatedAt = entity.getUpdatedAt();
    }
}