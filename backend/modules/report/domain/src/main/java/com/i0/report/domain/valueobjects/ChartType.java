package com.i0.report.domain.valueobjects;

/**
 * 图表类型值对象
 */
public enum ChartType {
    BAR("bar", "柱状图"),
    LINE("line", "折线图"),
    PIE("pie", "饼图"),
    AREA("area", "面积图"),
    SCATTER("scatter", "散点图"),
    GAUGE("gauge", "仪表盘"),
    TABLE("table", "表格"),
    METRIC("metric", "指标卡");

    private final String code;
    private final String description;

    ChartType(String code, String description) {
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
     * 检查图表类型代码是否有效
     */
    public static boolean isValid(String typeCode) {
        if (typeCode == null || typeCode.trim().isEmpty()) {
            return false;
        }

        for (ChartType type : ChartType.values()) {
            if (type.code.equals(typeCode.toLowerCase().trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据类型代码获取枚举
     */
    public static ChartType fromCode(String typeCode) {
        if (typeCode == null || typeCode.trim().isEmpty()) {
            throw new IllegalArgumentException("图表类型代码不能为空");
        }

        for (ChartType type : ChartType.values()) {
            if (type.code.equals(typeCode.toLowerCase().trim())) {
                return type;
            }
        }
        throw new IllegalArgumentException("无效的图表类型代码: " + typeCode);
    }

    /**
     * 检查是否为饼图
     */
    public boolean isPieChart() {
        return this == PIE;
    }

    /**
     * 检查是否为柱状图
     */
    public boolean isBarChart() {
        return this == BAR;
    }

    /**
     * 检查是否为折线图
     */
    public boolean isLineChart() {
        return this == LINE;
    }

    /**
     * 检查是否为面积图
     */
    public boolean isAreaChart() {
        return this == AREA;
    }

    /**
     * 检查是否为散点图
     */
    public boolean isScatterChart() {
        return this == SCATTER;
    }

    /**
     * 检查是否为表格
     */
    public boolean isTable() {
        return this == TABLE;
    }

    /**
     * 检查是否为指标卡
     */
    public boolean isMetric() {
        return this == METRIC;
    }

    /**
     * 检查是否为仪表盘
     */
    public boolean isGauge() {
        return this == GAUGE;
    }

    /**
     * 检查是否需要X轴
     */
    public boolean needsXAxis() {
        return !isPieChart() && !isMetric() && !isGauge();
    }

    /**
     * 检查是否需要Y轴
     */
    public boolean needsYAxis() {
        return !isPieChart() && !isMetric() && !isGauge();
    }

    /**
     * 检查是否支持多系列数据
     */
    public boolean supportsMultiSeries() {
        return this == BAR || this == LINE || this == AREA || this == SCATTER;
    }

    /**
     * 检查是否需要分类维度
     */
    public boolean needsDimension() {
        return !isMetric() && !isGauge();
    }
}