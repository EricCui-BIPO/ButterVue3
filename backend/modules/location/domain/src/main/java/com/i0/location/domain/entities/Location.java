package com.i0.location.domain.entities;

import com.i0.location.domain.valueobjects.LocationType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Location实体类
 * 代表系统中的地理位置（大洲、国家、省、市）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    /**
     * 地理位置唯一标识
     */
    private String id;

    /**
     * 地理位置名称
     */
    private String name;

    /**
     * 地理位置类型
     */
    private LocationType locationType;

    /**
     * ISO代码（两位字母代码，遵循ISO 3166-1标准）
     */
    private String isoCode;

    /**
     * 地理位置描述
     */
    private String description;

    /**
     * 上级地理位置ID（用于建立层级关系）
     */
    private String parentId;

    /**
     * 层级深度（0:大洲, 1:国家, 2:省/州, 3:市）
     */
    private Integer level;

    /**
     * 排序序号
     */
    private Integer sortOrder;

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
     * 创建新的Location实例
     * @param name 地理位置名称
     * @param locationType 地理位置类型
     * @param isoCode ISO代码
     * @param description 地理位置描述
     * @param parentId 上级地理位置ID
     * @return 新的Location实例
     */
    public static Location create(String name, LocationType locationType, String isoCode, String description, String parentId) {
        validateName(name);
        validateLocationType(locationType);
        validateIsoCode(isoCode, locationType);
        validateParentRelationship(locationType, parentId);

        return Location.builder()
                .name(name)
                .locationType(locationType)
                .isoCode(isoCode)
                .description(description)
                .parentId(parentId)
                .level(calculateLevel(locationType))
                .sortOrder(0)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .version(0L)
                .build();
    }

    /**
     * 更新地理位置信息
     * @param name 新的地理位置名称
     * @param description 新的地理位置描述
     * @param sortOrder 新的排序序号
     */
    public void update(String name, String description, Integer sortOrder) {
        validateName(name);

        this.name = name;
        this.description = description;
        if (sortOrder != null) {
            this.sortOrder = sortOrder;
        }
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 更新上级地理位置
     * @param parentId 新的上级地理位置ID
     */
    public void updateParent(String parentId) {
        validateParentRelationship(this.locationType, parentId);
        this.parentId = parentId;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 激活地理位置
     */
    public void activate() {
        this.active = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 停用地理位置
     */
    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 检查地理位置是否为大洲
     * @return true如果是大洲
     */
    public boolean isContinent() {
        return locationType != null && locationType.isContinent();
    }

    /**
     * 检查地理位置是否为国家
     * @return true如果是国家
     */
    public boolean isCountry() {
        return locationType != null && locationType.isCountry();
    }

    /**
     * 检查地理位置是否为省/州
     * @return true如果是省/州
     */
    public boolean isProvince() {
        return locationType != null && locationType.isProvince();
    }

    /**
     * 检查地理位置是否为城市
     * @return true如果是城市
     */
    public boolean isCity() {
        return locationType != null && locationType.isCity();
    }

    /**
     * 检查地理位置是否激活
     * @return true如果地理位置激活
     */
    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }

    /**
     * 检查是否有上级地理位置
     * @return true如果有上级地理位置
     */
    public boolean hasParent() {
        return parentId != null && !parentId.trim().isEmpty();
    }

    /**
     * 检查是否可以拥有下级地理位置
     * @return true如果可以拥有下级
     */
    public boolean canHaveChildren() {
        return locationType != null && locationType.canHaveChildren();
    }

    /**
     * 验证地理位置完整性
     * 检查地理位置的所有必要字段是否有效
     */
    public void validate() {
        validateName(this.name);
        validateLocationType(this.locationType);
        validateIsoCode(this.isoCode, this.locationType);
        validateParentRelationship(this.locationType, this.parentId);
    }

    /**
     * 验证地理位置名称
     * @param name 地理位置名称
     */
    private static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("地理位置名称不能为空");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("地理位置名称不能超过100个字符");
        }
    }

    /**
     * 验证地理位置类型
     * @param locationType 地理位置类型
     */
    private static void validateLocationType(LocationType locationType) {
        if (locationType == null) {
            throw new IllegalArgumentException("地理位置类型不能为空");
        }
    }

    /**
     * 验证ISO代码
     * @param isoCode ISO代码
     * @param locationType 地理位置类型
     */
    private static void validateIsoCode(String isoCode, LocationType locationType) {
        if (isoCode == null || isoCode.trim().isEmpty()) {
            if (locationType == LocationType.COUNTRY) {
                throw new IllegalArgumentException("国家的ISO代码不能为空");
            }
            // 其他类型的ISO代码可以为空
            return;
        }

        // 支持ISO 3166-1（2位）和ISO 3166-2（最长10位）标准
        if (isoCode.length() > 10) {
            throw new IllegalArgumentException("ISO代码长度不能超过10位");
        }

        // 允许字母、数字和连字符，支持ISO 3166-2格式（如CN-44-GZ）
        if (!isoCode.matches("[A-Za-z0-9-]+")) {
            throw new IllegalArgumentException("ISO代码只能包含字母、数字和连字符");
        }
    }

    /**
     * 验证上级关系
     * @param locationType 地理位置类型
     * @param parentId 上级地理位置ID
     */
    private static void validateParentRelationship(LocationType locationType, String parentId) {
        if (locationType == LocationType.CONTINENT && parentId != null) {
            throw new IllegalArgumentException("大洲不能有上级地理位置");
        }

        if (locationType != LocationType.CONTINENT && (parentId == null || parentId.trim().isEmpty())) {
            throw new IllegalArgumentException(locationType.getChineseName() + "必须有上级地理位置");
        }
    }

    /**
     * 计算层级深度
     * @param locationType 地理位置类型
     * @return 层级深度
     */
    private static Integer calculateLevel(LocationType locationType) {
        switch (locationType) {
            case CONTINENT:
                return 0;
            case COUNTRY:
                return 1;
            case PROVINCE:
                return 2;
            case CITY:
                return 3;
            default:
                throw new IllegalArgumentException("Unknown location type: " + locationType);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Objects.equals(id, location.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Location{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", locationType=" + locationType +
                ", isoCode='" + isoCode + '\'' +
                ", level=" + level +
                ", active=" + active +
                '}';
    }
}