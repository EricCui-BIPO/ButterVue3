package com.i0.report.domain.entities;

import com.i0.report.domain.valueobjects.Filter;
import com.i0.report.domain.valueobjects.ChartType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

/**
 * 图表实体
 * 每个图表绑定一个指标，定义展示类型、维度与样式
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Chart {

    /**
     * 图表唯一标识
     */
    private String id;

    /**
     * 图表名称
     */
    private String name;

    /**
     * 图表描述
     */
    private String description;

    /**
     * 图表类型（bar, line, pie, area, scatter等）
     */
    private String type;

    /**
     * 绑定的指标ID
     */
    private String indicatorId;

    /**
     * 主维度字段（用于X轴或分类）
     */
    private String dimension;

    /**
     * 图表过滤条件列表
     */
    private List<Filter> filters;

    /**
     * 图表标题
     */
    private String title;

    /**
     * X轴标签
     */
    private String xAxisLabel;

    /**
     * Y轴标签
     */
    private String yAxisLabel;

    /**
     * 图表宽度
     */
    private Integer width;

    /**
     * 图表高度
     */
    private Integer height;

    /**
     * 图表配置（JSON格式）
     */
    private String config;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 业务方法：验证图表配置是否有效
     */
    public boolean isValid() {
        return hasValidId()
            && hasValidName()
            && hasValidType()
            && hasValidIndicatorId();
    }

    /**
     * 业务方法：检查是否有有效的ID
     */
    public boolean hasValidId() {
        return id != null && !id.trim().isEmpty();
    }

    /**
     * 业务方法：检查是否有有效的名称
     */
    public boolean hasValidName() {
        return name != null && !name.trim().isEmpty() && name.length() <= 200;
    }

    /**
     * 业务方法：检查是否有有效的类型
     */
    public boolean hasValidType() {
        return type != null && ChartType.isValid(type);
    }

    /**
     * 业务方法：检查是否有有效的指标ID
     */
    public boolean hasValidIndicatorId() {
        return indicatorId != null && !indicatorId.trim().isEmpty();
    }

    /**
     * 业务方法：设置图表类型
     */
    public void setType(String type) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("图表类型不能为空");
        }

        if (!ChartType.isValid(type)) {
            throw new IllegalArgumentException("不支持的图表类型: " + type);
        }

        this.type = type.toLowerCase().trim();
    }

    /**
     * 业务方法：添加过滤条件
     */
    public void addFilter(Filter filter) {
        if (filter != null && filter.isValid()) {
            if (filters == null) {
                filters = new java.util.ArrayList<>();
            }
            if (!filters.contains(filter)) {
                filters.add(filter);
            }
        }
    }

    /**
     * 业务方法：移除过滤条件
     */
    public void removeFilter(Filter filter) {
        if (filters != null && filter != null) {
            filters.remove(filter);
        }
    }

    /**
     * 业务方法：设置尺寸
     */
    public void setSize(Integer width, Integer height) {
        if (width != null && width > 0) {
            this.width = width;
        }
        if (height != null && height > 0) {
            this.height = height;
        }
    }

    /**
     * 业务方法：检查是否为饼图
     */
    public boolean isPieChart() {
        return ChartType.PIE.getCode().equals(type);
    }

    /**
     * 业务方法：检查是否为柱状图
     */
    public boolean isBarChart() {
        return ChartType.BAR.getCode().equals(type);
    }

    /**
     * 业务方法：检查是否为折线图
     */
    public boolean isLineChart() {
        return ChartType.LINE.getCode().equals(type);
    }

    /**
     * 业务方法：检查是否为面积图
     */
    public boolean isAreaChart() {
        return ChartType.AREA.getCode().equals(type);
    }

    /**
     * 业务方法：检查是否为散点图
     */
    public boolean isScatterChart() {
        return ChartType.SCATTER.getCode().equals(type);
    }

    /**
     * 业务方法：检查是否为表格
     */
    public boolean isTable() {
        return ChartType.TABLE.getCode().equals(type);
    }

    /**
     * 业务方法：检查是否为指标卡
     */
    public boolean isMetric() {
        return ChartType.METRIC.getCode().equals(type);
    }

    /**
     * 业务方法：检查是否为仪表盘
     */
    public boolean isGauge() {
        return ChartType.GAUGE.getCode().equals(type);
    }

    /**
     * 业务方法：检查是否需要X轴
     */
    public boolean needsXAxis() {
        return ChartType.isValid(type) && ChartType.fromCode(type).needsXAxis();
    }

    /**
     * 业务方法：检查是否需要Y轴
     */
    public boolean needsYAxis() {
        return ChartType.isValid(type) && ChartType.fromCode(type).needsYAxis();
    }

    /**
     * 业务方法：检查是否支持多系列数据
     */
    public boolean supportsMultiSeries() {
        return ChartType.isValid(type) && ChartType.fromCode(type).supportsMultiSeries();
    }

    /**
     * 业务方法：检查是否需要分类维度
     */
    public boolean needsDimension() {
        return ChartType.isValid(type) && ChartType.fromCode(type).needsDimension();
    }

    /**
     * 业务方法：启用图表
     */
    public void enable() {
        if (!isValid()) {
            throw new IllegalStateException("图表配置无效，无法启用");
        }
        this.enabled = true;
    }

    /**
     * 业务方法：禁用图表
     */
    public void disable() {
        this.enabled = false;
    }

    /**
     * 业务方法：获取过滤条件数量
     */
    public int getFilterCount() {
        return filters != null ? filters.size() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chart chart = (Chart) o;
        return Objects.equals(id, chart.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Chart{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", indicatorId='" + indicatorId + '\'' +
                ", dimension='" + dimension + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}