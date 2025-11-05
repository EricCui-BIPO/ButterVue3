package com.i0.report.domain.entities;

import com.i0.report.domain.valueobjects.Filter;
import com.i0.report.domain.valueobjects.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

/**
 * 报表聚合根
 * 聚合多个 Chart，并管理布局、过滤、刷新周期
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Report {

    /**
     * 报表唯一标识
     */
    private String id;

    /**
     * 报表名称
     */
    private String name;

    /**
     * 报表描述
     */
    private String description;

    /**
     * 报表包含的图表列表
     */
    private List<Chart> charts;

    /**
     * 报表全局过滤条件列表
     */
    private List<Filter> filters;

    /**
     * 报表布局配置（JSON格式）
     */
    private String layout;

    /**
     * 刷新间隔（分钟）
     */
    private Integer refreshInterval;

    /**
     * 报表状态（draft, published, archived）
     */
    private String status;

    /**
     * 报表主题（light, dark）
     */
    private String theme;

    /**
     * 报表标签
     */
    private String tags;

    /**
     * 报表分类
     */
    private String category;

    /**
     * 是否公开访问
     */
    private Boolean publicAccess;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 业务方法：验证报表配置是否有效
     */
    public boolean isValid() {
        return hasValidId()
            && hasValidName()
            && hasValidStatus()
            && hasValidCharts();
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
     * 业务方法：检查是否有有效的状态
     */
    public boolean hasValidStatus() {
        return status != null && isValidStatus(status);
    }

    /**
     * 业务方法：检查是否有有效的图表
     */
    public boolean hasValidCharts() {
        return charts != null && !charts.isEmpty() && charts.stream().anyMatch(Chart::isValid);
    }

    /**
     * 验证报表状态是否有效
     */
    private boolean isValidStatus(String status) {
        return ReportStatus.isValid(status);
    }

    /**
     * 业务方法：添加图表
     */
    public void addChart(Chart chart) {
        if (chart != null && chart.isValid()) {
            if (charts == null) {
                charts = new java.util.ArrayList<>();
            }
            // 检查是否已存在相同ID的图表
            boolean exists = charts.stream()
                .anyMatch(c -> Objects.equals(c.getId(), chart.getId()));
            if (!exists) {
                charts.add(chart);
            }
        }
    }

    /**
     * 业务方法：移除图表
     */
    public void removeChart(String chartId) {
        if (charts != null && chartId != null) {
            charts.removeIf(chart -> Objects.equals(chart.getId(), chartId));
        }
    }

    /**
     * 业务方法：根据ID获取图表
     */
    public Chart getChartById(String chartId) {
        if (charts == null || chartId == null) {
            return null;
        }
        return charts.stream()
            .filter(chart -> Objects.equals(chart.getId(), chartId))
            .findFirst()
            .orElse(null);
    }

    /**
     * 业务方法：添加全局过滤条件
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
     * 业务方法：移除全局过滤条件
     */
    public void removeFilter(Filter filter) {
        if (filters != null && filter != null) {
            filters.remove(filter);
        }
    }

    /**
     * 业务方法：设置报表状态
     */
    public void setStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("报表状态不能为空");
        }

        String normalizedStatus = status.toLowerCase().trim();
        if (!isValidStatus(normalizedStatus)) {
            throw new IllegalArgumentException("不支持的报表状态: " + status);
        }

        this.status = normalizedStatus;
    }

    /**
     * 业务方法：设置刷新间隔
     */
    public void setRefreshInterval(Integer interval) {
        if (interval != null && interval < 0) {
            throw new IllegalArgumentException("刷新间隔不能为负数");
        }
        this.refreshInterval = interval;
    }

    /**
     * 业务方法：发布报表
     */
    public void publish() {
        if (!isValid()) {
            throw new IllegalStateException("报表配置无效，无法发布");
        }
        if (charts == null || charts.isEmpty()) {
            throw new IllegalStateException("报表没有图表，无法发布");
        }
        this.status = "published";
    }

    /**
     * 业务方法：归档报表
     */
    public void archive() {
        this.status = "archived";
    }

    /**
     * 业务方法：设为草稿
     */
    public void draft() {
        this.status = "draft";
    }

    /**
     * 业务方法：检查是否为草稿状态
     */
    public boolean isDraft() {
        return "draft".equals(status);
    }

    /**
     * 业务方法：检查是否已发布
     */
    public boolean isPublished() {
        return "published".equals(status);
    }

    /**
     * 业务方法：检查是否已归档
     */
    public boolean isArchived() {
        return "archived".equals(status);
    }

    /**
     * 业务方法：检查是否需要自动刷新
     */
    public boolean needsAutoRefresh() {
        return enabled && refreshInterval != null && refreshInterval > 0;
    }

    /**
     * 业务方法：启用报表
     */
    public void enable() {
        if (!isValid()) {
            throw new IllegalStateException("报表配置无效，无法启用");
        }
        this.enabled = true;
    }

    /**
     * 业务方法：禁用报表
     */
    public void disable() {
        this.enabled = false;
    }

    /**
     * 业务方法：获取图表数量
     */
    public int getChartCount() {
        return charts != null ? charts.size() : 0;
    }

    /**
     * 业务方法：获取过滤条件数量
     */
    public int getFilterCount() {
        return filters != null ? filters.size() : 0;
    }

    /**
     * 业务方法：获取启用的图表数量
     */
    public long getEnabledChartCount() {
        if (charts == null) {
            return 0;
        }
        return charts.stream()
            .filter(chart -> Boolean.TRUE.equals(chart.getEnabled()))
            .count();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Report report = (Report) o;
        return Objects.equals(id, report.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Report{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", chartCount=" + getChartCount() +
                ", enabled=" + enabled +
                '}';
    }
}