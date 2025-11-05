package com.i0.report.domain.services;

import com.i0.report.domain.valueobjects.Filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 过滤条件合并服务
 * 负责合并来自不同层级的过滤条件
 */
public class FilterMergingService {

    /**
     * 合并报表和额外的过滤条件
     *
     * @param reportFilters 报表过滤条件
     * @param additionalFilters 额外的过滤条件
     * @return 合并后的过滤条件列表
     */
    public List<Filter> mergeFilters(List<Filter> reportFilters, List<Filter> additionalFilters) {
        List<Filter> allFilters = new ArrayList<>();

        if (reportFilters != null) {
            allFilters.addAll(reportFilters);
        }
        if (additionalFilters != null) {
            allFilters.addAll(additionalFilters);
        }

        return allFilters;
    }

    /**
     * 合并所有层级的过滤条件（数据集 + 指标 + 图表 + 全局）
     *
     * @param datasetFilters 数据集过滤条件
     * @param indicatorFilters 指标过滤条件
     * @param chartFilters 图表过滤条件
     * @param globalFilters 全局过滤条件
     * @return 合并后的过滤条件列表
     */
    public List<Filter> mergeAllFilters(List<Filter> datasetFilters, List<Filter> indicatorFilters,
                                       List<Filter> chartFilters, List<Filter> globalFilters) {
        List<Filter> allFilters = new ArrayList<>();

        if (datasetFilters != null) {
            allFilters.addAll(datasetFilters);
        }
        if (indicatorFilters != null) {
            allFilters.addAll(indicatorFilters);
        }
        if (chartFilters != null) {
            allFilters.addAll(chartFilters);
        }
        if (globalFilters != null) {
            allFilters.addAll(globalFilters);
        }

        return allFilters;
    }

    /**
     * 去重过滤条件
     *
     * @param filters 原始过滤条件列表
     * @return 去重后的过滤条件列表
     */
    public List<Filter> deduplicateFilters(List<Filter> filters) {
        if (filters == null || filters.isEmpty()) {
            return new ArrayList<>();
        }

        return filters.stream()
                .filter(Objects::nonNull)
                .distinct()
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    /**
     * 验证过滤条件列表是否有效
     *
     * @param filters 过滤条件列表
     * @return 是否有效
     */
    public boolean validateFilters(List<Filter> filters) {
        if (filters == null) {
            return true;
        }

        return filters.stream()
                .allMatch(filter -> filter == null || filter.isValid());
    }
}