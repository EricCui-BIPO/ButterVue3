package com.i0.talent.domain.valueobjects;

import com.i0.talent.domain.exception.DomainException;

import java.util.Objects;

/**
 * 工作地点值对象
 *
 * 封装员工工作地点信息，支持COUNTRY、PROVINCE、CITY三种类型的Location
 */
public class WorkLocation {

    /**
     * 地理位置ID
     */
    private final String locationId;

    /**
     * 地理位置名称（冗余存储，便于显示）
     */
    private final String locationName;

    /**
     * 地理位置类型（字符串形式，避免跨领域依赖）
     */
    private final String locationType;

    /**
     * ISO代码（如CN、US、CN-44等）
     */
    private final String isoCode;

    /**
     * 私有构造函数
     */
    private WorkLocation(String locationId, String locationName, String locationType, String isoCode) {
        validate(locationId, locationName, locationType, isoCode);
        this.locationId = Objects.requireNonNull(locationId, "工作地点ID不能为空");
        this.locationName = locationName;
        this.locationType = locationType;
        this.isoCode = isoCode;
    }

    /**
     * 创建工作地点值对象
     *
     * @param locationId 地理位置ID
     * @param locationName 地理位置名称
     * @param locationType 地理位置类型（COUNTRY、PROVINCE、CITY）
     * @param isoCode ISO代码
     * @return 工作地点值对象
     */
    public static WorkLocation of(String locationId, String locationName, String locationType, String isoCode) {
        return new WorkLocation(locationId, locationName, locationType, isoCode);
    }

    /**
     * 创建工作地点值对象（向后兼容）
     *
     * @param locationId 地理位置ID
     * @param locationName 地理位置名称
     * @param locationType 地理位置类型（COUNTRY、PROVINCE、CITY）
     * @return 工作地点值对象
     */
    public static WorkLocation of(String locationId, String locationName, String locationType) {
        return new WorkLocation(locationId, locationName, locationType, null);
    }

    /**
     * 验证工作地点信息
     */
    private void validate(String locationId, String locationName, String locationType, String isoCode) {
        if (locationId == null || locationId.trim().isEmpty()) {
            throw new DomainException("工作地点ID不能为空");
        }

        // 验证工作地点类型必须是COUNTRY、PROVINCE、CITY之一
        if (locationType != null &&
            !"COUNTRY".equals(locationType) &&
            !"PROVINCE".equals(locationType) &&
            !"CITY".equals(locationType)) {
            throw new DomainException("工作地点类型只能是国家、省份或城市");
        }

        // ISO代码可以为空，但如果提供则验证格式
        if (isoCode != null && !isoCode.trim().isEmpty()) {
            // 支持ISO 3166-1（2位）和ISO 3166-2（最长10位）标准
            if (isoCode.length() > 10) {
                throw new DomainException("ISO代码长度不能超过10位");
            }

            // 允许字母、数字和连字符，支持ISO 3166-2格式（如CN-44-GZ）
            if (!isoCode.matches("[A-Za-z0-9-]+")) {
                throw new DomainException("ISO代码只能包含字母、数字和连字符");
            }
        }
    }

    /**
     * 检查是否为国家
     */
    public boolean isCountry() {
        return "COUNTRY".equals(locationType);
    }

    /**
     * 检查是否为省份
     */
    public boolean isProvince() {
        return "PROVINCE".equals(locationType);
    }

    /**
     * 检查是否为城市
     */
    public boolean isCity() {
        return "CITY".equals(locationType);
    }

    /**
     * 获取显示名称
     */
    public String getDisplayName() {
        return locationName != null ? locationName : locationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkLocation that = (WorkLocation) o;
        return Objects.equals(locationId, that.locationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(locationId);
    }

    @Override
    public String toString() {
        return "WorkLocation{" +
                "locationId='" + locationId + '\'' +
                ", locationName='" + locationName + '\'' +
                ", locationType=" + locationType +
                ", isoCode='" + isoCode + '\'' +
                '}';
    }

    // Getters
    public String getLocationId() {
        return locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getLocationType() {
        return locationType;
    }

    public String getIsoCode() {
        return isoCode;
    }
}