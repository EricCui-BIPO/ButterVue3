package com.i0.talent.domain.enums;

/**
 * 数据存储位置枚举
 *
 * 支持多区域数据存储，确保数据合规性
 */
public enum DataLocation {
    
    /**
     * 中国宁夏数据中心
     */
    NINGXIA("CN", "中国宁夏", "asia-ninxia-1"),
    
    /**
     * 新加坡数据中心
     */
    SINGAPORE("SG", "新加坡", "asia-southeast-1"),
    
    /**
     * 德国法兰克福数据中心
     */
    GERMANY("DE", "德国", "eu-central-1");
    
    private final String countryCode;
    private final String description;
    private final String region;
    
    DataLocation(String countryCode, String description, String region) {
        this.countryCode = countryCode;
        this.description = description;
        this.region = region;
    }
    
    public String getCountryCode() {
        return countryCode;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getRegion() {
        return region;
    }
    
    /**
     * 根据国家代码获取数据存储位置
     */
    public static DataLocation fromCountryCode(String countryCode) {
        if (countryCode == null || countryCode.trim().isEmpty()) {
            return NINGXIA; // 默认宁夏
        }
        
        for (DataLocation location : values()) {
            if (location.countryCode.equalsIgnoreCase(countryCode)) {
                return location;
            }
        }
        
        return NINGXIA; // 默认宁夏
    }
    
    /**
     * 检查是否需要数据合规提示
     */
    public boolean requiresComplianceNotice() {
        return this != NINGXIA;
    }
    
    /**
     * 获取数据合规提示信息
     */
    public String getComplianceNotice() {
        switch (this) {
            case SINGAPORE:
                return "员工数据将存储在新加坡数据中心，需符合新加坡个人数据保护法(PDPA)";
            case GERMANY:
                return "员工数据将存储在德国数据中心，需符合欧盟通用数据保护条例(GDPR)";
            case NINGXIA:
            default:
                return "员工数据将存储在中国宁夏数据中心，需符合中国个人信息保护法";
        }
    }
}
