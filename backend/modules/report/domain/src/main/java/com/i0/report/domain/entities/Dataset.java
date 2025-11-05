package com.i0.report.domain.entities;

import com.i0.report.domain.valueobjects.Filter;
import com.i0.report.domain.valueobjects.DataSourceType;
import com.i0.report.domain.valueobjects.UpdateStrategy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

/**
 * 数据集实体
 * 定义 SQL 查询、过滤条件与更新策略
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Dataset {

    /**
     * 数据集唯一标识
     */
    private String id;

    /**
     * 数据集名称
     */
    private String name;

    /**
     * 数据集描述
     */
    private String description;

    /**
     * 原始 SQL 查询语句
     */
    private String sql;

    /**
     * 数据集过滤条件列表
     */
    private List<Filter> filters;

    /**
     * 数据源类型（mysql, postgresql, oracle等）
     */
    private String dataSourceType;

    /**
     * 更新策略（real_time, scheduled, manual）
     */
    private String updateStrategy;

    /**
     * 更新间隔（分钟）
     */
    private Integer updateInterval;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 业务方法：验证数据集配置是否有效
     */
    public boolean isValid() {
        return hasValidId()
            && hasValidName()
            && hasValidSql()
            && hasValidDataSourceType()
            && hasValidUpdateStrategy();
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
     * 业务方法：检查是否有有效的SQL
     */
    public boolean hasValidSql() {
        return sql != null && !sql.trim().isEmpty();
    }

    /**
     * 业务方法：检查是否有有效的数据源类型
     */
    public boolean hasValidDataSourceType() {
        return dataSourceType != null && DataSourceType.isValid(dataSourceType);
    }

    /**
     * 业务方法：检查是否有有效的更新策略
     */
    public boolean hasValidUpdateStrategy() {
        return updateStrategy == null || UpdateStrategy.isValid(updateStrategy);
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
     * 业务方法：更新SQL语句
     */
    public void updateSql(String newSql) {
        if (newSql != null && !newSql.trim().isEmpty()) {
            this.sql = newSql;
        } else {
            throw new IllegalArgumentException("SQL语句不能为空");
        }
    }

    /**
     * 业务方法：设置更新策略
     */
    public void setUpdateStrategy(String strategy, Integer interval) {
        if (!UpdateStrategy.isValid(strategy)) {
            throw new IllegalArgumentException("不支持的更新策略: " + strategy);
        }

        UpdateStrategy updateStrategyEnum = UpdateStrategy.fromCode(strategy);

        if (updateStrategyEnum.requiresInterval()) {
            if (interval == null || interval <= 0) {
                throw new IllegalArgumentException("定时更新策略必须指定有效的更新间隔");
            }
            this.updateInterval = interval;
        } else {
            this.updateInterval = null;
        }

        this.updateStrategy = strategy.toLowerCase().trim();
    }

    /**
     * 业务方法：启用数据集
     */
    public void enable() {
        if (!isValid()) {
            throw new IllegalStateException("数据集配置无效，无法启用");
        }
        this.enabled = true;
    }

    /**
     * 业务方法：禁用数据集
     */
    public void disable() {
        this.enabled = false;
    }

    /**
     * 业务方法：检查是否需要更新
     */
    public boolean needsUpdate() {
        if (!enabled || updateStrategy == null) {
            return false;
        }

        UpdateStrategy strategy = UpdateStrategy.fromCode(updateStrategy);

        if (strategy.isManual()) {
            return false;
        }

        if (strategy.isRealTime()) {
            return true;
        }

        return strategy.isScheduled() && updateInterval != null && updateInterval > 0;
    }

    /**
     * 业务方法：检查是否为实时更新
     */
    public boolean isRealTimeUpdate() {
        return UpdateStrategy.isValid(updateStrategy)
            && UpdateStrategy.fromCode(updateStrategy).isRealTime();
    }

    /**
     * 业务方法：检查是否为定时更新
     */
    public boolean isScheduledUpdate() {
        return UpdateStrategy.isValid(updateStrategy)
            && UpdateStrategy.fromCode(updateStrategy).isScheduled();
    }

    /**
     * 业务方法：检查是否为手动更新
     */
    public boolean isManualUpdate() {
        return UpdateStrategy.isValid(updateStrategy)
            && UpdateStrategy.fromCode(updateStrategy).isManual();
    }

    /**
     * 业务方法：检查是否为关系型数据库
     */
    public boolean isRelationalDataSource() {
        return DataSourceType.isValid(dataSourceType)
            && DataSourceType.fromCode(dataSourceType).isRelational();
    }

    /**
     * 业务方法：检查是否为列式数据库
     */
    public boolean isColumnarDataSource() {
        return DataSourceType.isValid(dataSourceType)
            && DataSourceType.fromCode(dataSourceType).isColumnar();
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
        Dataset dataset = (Dataset) o;
        return Objects.equals(id, dataset.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Dataset{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", dataSourceType='" + dataSourceType + '\'' +
                ", updateStrategy='" + updateStrategy + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}