package com.i0.talent.domain.enums;

/**
 * 条件类型枚举
 *
 * 定义条件字段的触发条件（国家/项目）
 */
public enum ConditionType {
    
    /**
     * 国家特定字段
     */
    COUNTRY("国家", "基于国家代码的条件字段"),
    
    /**
     * 项目特定字段
     */
    PROJECT("项目", "基于项目ID的条件字段");
    
    private final String displayName;
    private final String description;
    
    ConditionType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 检查条件值是否有效
     */
    public boolean isValidConditionValue(String conditionValue) {
        if (conditionValue == null || conditionValue.trim().isEmpty()) {
            return false;
        }
        
        switch (this) {
            case COUNTRY:
                return conditionValue.matches("^[A-Z]{2}$");
            case PROJECT:
                return conditionValue.matches("^[A-Za-z0-9_-]{1,50}$");
            default:
                return false;
        }
    }
    
    /**
     * 格式化条件值
     */
    public String formatConditionValue(String conditionValue) {
        if (conditionValue == null) {
            return "";
        }
        
        switch (this) {
            case COUNTRY:
                return conditionValue.trim().toUpperCase();
            case PROJECT:
                return conditionValue.trim();
            default:
                return conditionValue.trim();
        }
    }
    
    /**
     * 获取条件值示例
     */
    public String getConditionValueExample() {
        switch (this) {
            case COUNTRY:
                return "CN, US, SG, DE";
            case PROJECT:
                return "PROJECT_001, talent-mgmt-2024";
            default:
                return "";
        }
    }
}
