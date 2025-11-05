package com.i0.location.gateway.persistence.dataobjects;

import com.baomidou.mybatisplus.annotation.*;
import com.i0.location.domain.entities.Location;
import com.i0.location.domain.valueobjects.LocationType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Location数据对象
 * 用于数据库映射
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("locations")
public class LocationDO {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 地理位置名称
     */
    @TableField("name")
    private String name;

    /**
     * 地理位置类型
     */
    @TableField("location_type")
    private String locationType;

    /**
     * ISO代码
     */
    @TableField("iso_code")
    private String isoCode;

    /**
     * 地理位置描述
     */
    @TableField("description")
    private String description;

    /**
     * 上级地理位置ID
     */
    @TableField("parent_id")
    private String parentId;

    /**
     * 层级深度
     */
    @TableField("level")
    private Integer level;

    /**
     * 排序序号
     */
    @TableField("sort_order")
    private Integer sortOrder;

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
     * @param location 领域实体
     * @return 数据对象
     */
    public static LocationDO from(Location location) {
        if (location == null) {
            return null;
        }

        return LocationDO.builder()
                .id(location.getId())
                .name(location.getName())
                .locationType(location.getLocationType().name())
                .isoCode(location.getIsoCode())
                .description(location.getDescription())
                .parentId(location.getParentId())
                .level(location.getLevel())
                .sortOrder(location.getSortOrder())
                .isActive(location.isActive())
                .createdAt(location.getCreatedAt())
                .updatedAt(location.getUpdatedAt())
                .isDeleted(false)
                .build();
    }

    /**
     * 转换为领域实体
     * @return 领域实体
     */
    public Location toDomain() {
        Location location = new Location();
        location.setId(this.id);
        location.setName(this.name);
        location.setLocationType(LocationType.valueOf(this.locationType));
        location.setIsoCode(this.isoCode);
        location.setDescription(this.description);
        location.setParentId(this.parentId);
        location.setLevel(this.level);
        location.setSortOrder(this.sortOrder);
        location.setActive(this.isActive != null ? this.isActive : true);
        location.setCreatedAt(this.createdAt);
        location.setUpdatedAt(this.updatedAt);

        return location;
    }

    /**
     * 更新数据对象字段
     * @param location 领域实体
     */
    public void updateFrom(Location location) {
        if (location == null) {
            return;
        }

        this.name = location.getName();
        this.locationType = location.getLocationType().name();
        this.isoCode = location.getIsoCode();
        this.description = location.getDescription();
        this.parentId = location.getParentId();
        this.level = location.getLevel();
        this.sortOrder = location.getSortOrder();
        this.isActive = location.isActive();
        this.updatedAt = location.getUpdatedAt();
    }
}