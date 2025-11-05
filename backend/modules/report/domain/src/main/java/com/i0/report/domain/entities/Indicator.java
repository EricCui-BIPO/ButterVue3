package com.i0.report.domain.entities;

import com.i0.report.domain.valueobjects.Filter;
import com.i0.report.domain.valueobjects.IndicatorType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

/**
 * 指标实体
 * 定义业务指标计算逻辑，引用 Dataset 并指定聚合方式
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Indicator {

    /**
     * 指标唯一标识
     */
    private String id;

    /**
     * 指标名称
     */
    private String name;

    /**
     * 指标描述
     */
    private String description;

    /**
     * 引用的数据集ID
     */
    private String datasetId;

    /**
     * 计算表达式（SUM(order_count), AVG(price)等）
     */
    private String calculation;

    /**
     * 维度字段列表（date, service_type等）
     */
    private List<String> dimensions;

    /**
     * 指标过滤条件列表
     */
    private List<Filter> filters;

    /**
     * 指标类型（count, sum, avg, max, min等）
     */
    private String type;

    /**
     * 数据单位
     */
    private String unit;

    /**
     * 数据格式化模式
     */
    private String formatPattern;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 业务方法：验证指标配置是否有效
     */
    public boolean isValid() {
        return hasValidId()
            && hasValidName()
            && hasValidDatasetId()
            && hasValidCalculation()
            && hasValidType();
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
     * 业务方法：检查是否有有效的数据集ID
     */
    public boolean hasValidDatasetId() {
        return datasetId != null && !datasetId.trim().isEmpty();
    }

    /**
     * 业务方法：检查是否有有效的计算表达式
     */
    public boolean hasValidCalculation() {
        return calculation != null && !calculation.trim().isEmpty();
    }

    /**
     * 业务方法：检查是否有有效的类型
     */
    public boolean hasValidType() {
        return type != null && IndicatorType.isValid(type);
    }

    /**
     * 业务方法：添加维度
     */
    public void addDimension(String dimension) {
        if (dimension != null && !dimension.trim().isEmpty()) {
            if (dimensions == null) {
                dimensions = new java.util.ArrayList<>();
            }
            if (!dimensions.contains(dimension)) {
                dimensions.add(dimension);
            }
        }
    }

    /**
     * 业务方法：移除维度
     */
    public void removeDimension(String dimension) {
        if (dimensions != null && dimension != null) {
            dimensions.remove(dimension);
        }
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
     * 业务方法：更新计算表达式
     */
    public void updateCalculation(String newCalculation) {
        if (newCalculation != null && !newCalculation.trim().isEmpty()) {
            this.calculation = newCalculation;
        } else {
            throw new IllegalArgumentException("计算表达式不能为空");
        }
    }

    /**
     * 业务方法：设置指标类型
     */
    public void setType(String type) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("指标类型不能为空");
        }

        if (!IndicatorType.isValid(type)) {
            throw new IllegalArgumentException("不支持的指标类型: " + type);
        }

        this.type = type.toLowerCase().trim();
    }

    /**
     * 业务方法：检查是否为聚合指标
     */
    public boolean isAggregateIndicator() {
        return IndicatorType.isValid(type) && IndicatorType.fromCode(type).isAggregateIndicator();
    }

    /**
     * 业务方法：检查是否为计数指标
     */
    public boolean isCountIndicator() {
        return IndicatorType.isValid(type) && IndicatorType.fromCode(type).isCountIndicator();
    }

    /**
     * 业务方法：检查是否为统计指标
     */
    public boolean isStatisticalIndicator() {
        return IndicatorType.isValid(type) && IndicatorType.fromCode(type).isStatisticalIndicator();
    }

    /**
     * 业务方法：检查是否需要数值数据
     */
    public boolean requiresNumericData() {
        return IndicatorType.isValid(type) && IndicatorType.fromCode(type).requiresNumericData();
    }

    /**
     * 业务方法：检查是否适用于任何数据类型
     */
    public boolean appliesToAnyDataType() {
        return IndicatorType.isValid(type) && IndicatorType.fromCode(type).appliesToAnyDataType();
    }

    /**
     * 业务方法：启用指标
     */
    public void enable() {
        if (!isValid()) {
            throw new IllegalStateException("指标配置无效，无法启用");
        }
        this.enabled = true;
    }

    /**
     * 业务方法：禁用指标
     */
    public void disable() {
        this.enabled = false;
    }

    /**
     * 业务方法：获取维度数量
     */
    public int getDimensionCount() {
        return dimensions != null ? dimensions.size() : 0;
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
        Indicator indicator = (Indicator) o;
        return Objects.equals(id, indicator.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Indicator{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", datasetId='" + datasetId + '\'' +
                ", calculation='" + calculation + '\'' +
                ", type='" + type + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}