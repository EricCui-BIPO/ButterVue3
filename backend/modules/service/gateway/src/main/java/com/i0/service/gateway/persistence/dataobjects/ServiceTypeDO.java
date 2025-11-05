package com.i0.service.gateway.persistence.dataobjects;

import com.baomidou.mybatisplus.annotation.*;
import com.i0.service.domain.entities.ServiceTypeEntity;
import com.i0.service.domain.valueobjects.ServiceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 服务类型数据对象
 * 用于与数据库表映射
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("service_types")
public class ServiceTypeDO {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @TableField("name")
    private String name;

    @TableField("code")
    private String code;

    @TableField("description")
    private String description;

    @TableField("is_active")
    private Boolean isActive;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    @TableField("is_deleted")
    private Boolean isDeleted;

    /**
     * 从领域实体创建数据对象
     * @param entity 领域实体
     * @return 数据对象
     */
    public static ServiceTypeDO from(ServiceTypeEntity entity) {
        if (entity == null) {
            return null;
        }

        return ServiceTypeDO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .code(entity.getCode())
                .description(entity.getDescription())
                .isActive(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .isDeleted(false)
                .build();
    }

    /**
     * 更新数据对象从领域实体
     * @param entity 领域实体
     */
    public void updateFrom(ServiceTypeEntity entity) {
        if (entity == null) {
            return;
        }

        this.name = entity.getName();
        this.description = entity.getDescription();
        this.isActive = entity.isActive();
        this.updatedAt = entity.getUpdatedAt();
    }

    /**
     * 转换为领域实体
     * @return 领域实体
     */
    public ServiceTypeEntity toDomain() {
        ServiceTypeEntity entity = new ServiceTypeEntity();
        entity.setId(this.id);
        entity.setName(this.name);

        // 安全地设置服务类型，如果code无效则设为null
        try {
            entity.setServiceType(this.code != null ? ServiceType.fromCode(this.code) : null);
        } catch (IllegalArgumentException e) {
            // 记录警告但继续处理，使用null作为serviceType
            System.err.println("Warning: Invalid service type code '" + this.code + "' for entity " + this.id + ": " + e.getMessage());
            entity.setServiceType(null);
        }

        entity.setDescription(this.description);
        entity.setActive(this.isActive);
        entity.setCreatedAt(this.createdAt);
        entity.setUpdatedAt(this.updatedAt);
        return entity;
    }
}