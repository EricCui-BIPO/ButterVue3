package com.i0.client.gateway.persistence.dataobjects;

import com.baomidou.mybatisplus.annotation.*;
import com.i0.client.domain.entities.Client;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 客户数据对象
 * 用于数据库持久化映射
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("clients")
public class ClientDO {
    
    /**
     * 客户ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 客户名称
     */
    @TableField("name")
    private String name;
    
    /**
     * 客户代码
     */
    @TableField("code")
    private String code;
    
    /**
     * 客户别名
     */
    @TableField("alias_name")
    private String aliasName;
    
    /**
     * 位置ID
     */
    @TableField("location_id")
    private String locationId;
    
    /**
     * 描述
     */
    @TableField("description")
    private String description;
    
    /**
     * 是否激活 (1=激活,0=未激活)
     */
    @TableField("is_active")
    private Integer isActive;
    
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
     * 版本号（用于乐观锁）
     */
    @Version
    @TableField("version")
    private Long version;
    
    /**
     * 逻辑删除标记 (1=已删除,0=未删除)
     */
    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;

    /**
     * 创建者ID
     */
    @TableField("creator_id")
    private String creatorId;

    /**
     * 更新者ID
     */
    @TableField("updater_id")
    private String updaterId;

    /**
     * 创建者姓名
     */
    @TableField("creator")
    private String creator;

    /**
     * 更新者姓名
     */
    @TableField("updater")
    private String updater;
    
    /**
     * 从领域实体转换为数据对象
     * @param client 客户领域实体
     * @return 客户数据对象
     */
    public static ClientDO from(Client client) {
        if (client == null) {
            return null;
        }

        return ClientDO.builder()
                .id(client.getId())
                .name(client.getName())
                .code(client.getCode())
                .aliasName(client.getAliasName())
                .locationId(client.getLocationId())
                .description(client.getDescription())
                .isActive(client.getActive() ? 1 : 0)
                .createdAt(client.getCreatedAt())
                .updatedAt(client.getUpdatedAt())
                .version(client.getVersion())
                .isDeleted(0)
                .creatorId(client.getCreatorId())
                .updaterId(client.getUpdaterId())
                .creator(client.getCreator())
                .updater(client.getUpdater())
                .build();
    }
    
    /**
     * 转换为领域实体
     * @return 客户领域实体
     */
    public Client toDomain() {
        return Client.builder()
                .id(this.id)
                .name(this.name)
                .code(this.code)
                .aliasName(this.aliasName)
                .locationId(this.locationId)
                .description(this.description)
                .active(this.isActive != null && this.isActive == 1)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .version(this.version)
                .creatorId(this.creatorId)
                .updaterId(this.updaterId)
                .creator(this.creator)
                .updater(this.updater)
                .build();
    }
    
    /**
     * 从领域实体更新数据对象
     * @param client 客户领域实体
     */
    public void updateFrom(Client client) {
        if (client != null) {
            this.name = client.getName();
            this.code = client.getCode();
            this.aliasName = client.getAliasName();
            this.locationId = client.getLocationId();
            this.description = client.getDescription();
            this.isActive = client.getActive() ? 1 : 0;
            this.version = client.getVersion();
            this.updaterId = client.getUpdaterId();
            this.updater = client.getUpdater();
        }
    }
}