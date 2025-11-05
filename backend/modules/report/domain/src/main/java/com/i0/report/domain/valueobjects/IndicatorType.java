package com.i0.report.domain.valueobjects;

/**
 * 指标类型值对象
 */
public enum IndicatorType {
    COUNT("count", "计数"),
    SUM("sum", "求和"),
    AVG("avg", "平均值"),
    MAX("max", "最大值"),
    MIN("min", "最小值"),
    DISTINCT("distinct", "去重计数");

    private final String code;
    private final String description;

    IndicatorType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 检查指标类型代码是否有效
     */
    public static boolean isValid(String typeCode) {
        if (typeCode == null || typeCode.trim().isEmpty()) {
            return false;
        }

        for (IndicatorType type : IndicatorType.values()) {
            if (type.code.equals(typeCode.toLowerCase().trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据类型代码获取枚举
     */
    public static IndicatorType fromCode(String typeCode) {
        if (typeCode == null || typeCode.trim().isEmpty()) {
            throw new IllegalArgumentException("指标类型代码不能为空");
        }

        for (IndicatorType type : IndicatorType.values()) {
            if (type.code.equals(typeCode.toLowerCase().trim())) {
                return type;
            }
        }
        throw new IllegalArgumentException("无效的指标类型代码: " + typeCode);
    }

    /**
     * 检查是否为计数指标
     */
    public boolean isCountIndicator() {
        return this == COUNT || this == DISTINCT;
    }

    /**
     * 检查是否为聚合指标
     */
    public boolean isAggregateIndicator() {
        return this == SUM || this == AVG || this == MAX || this == MIN;
    }

    /**
     * 检查是否为统计指标
     */
    public boolean isStatisticalIndicator() {
        return this == AVG || this == MAX || this == MIN;
    }

    /**
     * 检查是否需要数值数据
     */
    public boolean requiresNumericData() {
        return this == SUM || this == AVG || this == MAX || this == MIN;
    }

    /**
     * 检查是否适用于任何数据类型
     */
    public boolean appliesToAnyDataType() {
        return this == COUNT || this == DISTINCT;
    }
}