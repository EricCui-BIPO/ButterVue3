package com.i0.entity.domain.entities;

import com.i0.entity.domain.valueobjects.EntityType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity实体类
 * 代表系统中的各种实体（BIPO实体、客户实体、供应商实体）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Entity {
    
    /**
     * 实体唯一标识
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
     * 实体代码（可选，用于系统内部标识）
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
     * 版本号（用于乐观锁）
     */
    private Long version;
    
    /**
     * 创建新的Entity实例
     * @param name 实体名称
     * @param entityType 实体类型
     * @param code 实体代码
     * @param description 实体描述
     * @return 新的Entity实例
     */
    public static Entity create(String name, EntityType entityType, String code, String description) {
        validateName(name);
        validateEntityType(entityType);
        
        return Entity.builder()
                .name(name)
                .entityType(entityType)
                .code(code)
                .description(description)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .version(0L)
                .build();
    }
    
    /**
     * 更新实体信息
     * @param name 新的实体名称
     * @param description 新的实体描述
     */
    public void update(String name, String description) {
        validateName(name);
        
        this.name = name;
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 激活实体
     */
    public void activate() {
        this.active = true;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 停用实体
     */
    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 检查实体是否为BIPO内部实体
     * @return true如果是BIPO实体
     */
    public boolean isBipoEntity() {
        return entityType != null && entityType.isBipoEntity();
    }
    
    /**
     * 检查实体是否为外部实体
     * @return true如果是外部实体
     */
    public boolean isExternalEntity() {
        return entityType != null && entityType.isExternalEntity();
    }
    
    /**
     * 检查实体是否激活
     * @return true如果实体激活
     */
    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }
    
    /**
     * 验证实体完整性
     * 检查实体的所有必要字段是否有效
     */
    public void validate() {
        validateName(this.name);
        validateEntityType(this.entityType);
    }
    
    /**
     * 验证实体名称
     * @param name 实体名称
     */
    private static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("实体名称不能为空");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("实体名称不能超过100个字符");
        }
    }
    
    /**
     * 验证实体类型
     * @param entityType 实体类型
     */
    private static void validateEntityType(EntityType entityType) {
        if (entityType == null) {
            throw new IllegalArgumentException("实体类型不能为空");
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return Objects.equals(id, entity.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Entity{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", entityType=" + entityType +
                ", code='" + code + '\'' +
                ", active=" + active +
                '}';
    }
}