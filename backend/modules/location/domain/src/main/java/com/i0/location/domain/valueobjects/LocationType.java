package com.i0.location.domain.valueobjects;

/**
 * Location类型枚举
 * 定义系统中支持的地理位置类型
 */
public enum LocationType {
    /**
     * 大洲 - 地理上的大洲
     */
    CONTINENT("Continent", "大洲"),

    /**
     * 国家 - 主权国家
     */
    COUNTRY("Country", "国家"),

    /**
     * 省/州 - 国家的行政区划
     */
    PROVINCE("Province", "省/州"),

    /**
     * 市 - 城市
     */
    CITY("City", "市");

    private final String displayName;
    private final String chineseName;

    LocationType(String displayName, String chineseName) {
        this.displayName = displayName;
        this.chineseName = chineseName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getChineseName() {
        return chineseName;
    }

    /**
     * 根据显示名称获取枚举值
     * @param displayName 显示名称
     * @return LocationType枚举值
     * @throws IllegalArgumentException 如果找不到对应的枚举值
     */
    public static LocationType fromDisplayName(String displayName) {
        for (LocationType type : values()) {
            if (type.displayName.equals(displayName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown location type: " + displayName);
    }

    /**
     * 根据中文名称获取枚举值
     * @param chineseName 中文名称
     * @return LocationType枚举值
     * @throws IllegalArgumentException 如果找不到对应的枚举值
     */
    public static LocationType fromChineseName(String chineseName) {
        for (LocationType type : values()) {
            if (type.chineseName.equals(chineseName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown location type: " + chineseName);
    }

    /**
     * 检查是否为大洲
     * @return true如果是大洲
     */
    public boolean isContinent() {
        return this == CONTINENT;
    }

    /**
     * 检查是否为国家
     * @return true如果是国家
     */
    public boolean isCountry() {
        return this == COUNTRY;
    }

    /**
     * 检查是否为省/州
     * @return true如果是省/州
     */
    public boolean isProvince() {
        return this == PROVINCE;
    }

    /**
     * 检查是否为城市
     * @return true如果是城市
     */
    public boolean isCity() {
        return this == CITY;
    }

    /**
     * 检查是否可以拥有上级地理位置
     * @return true如果可以拥有上级（国家、省、市）
     */
    public boolean canHaveParent() {
        return this == COUNTRY || this == PROVINCE || this == CITY;
    }

    /**
     * 检查是否可以拥有下级地理位置
     * @return true如果可以拥有下级（大洲、国家、省）
     */
    public boolean canHaveChildren() {
        return this == CONTINENT || this == COUNTRY || this == PROVINCE;
    }

    @Override
    public String toString() {
        return displayName;
    }
}