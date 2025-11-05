package com.i0.report.application.dto.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 数据集输出DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatasetOutput {

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
    private List<FilterOutput> filters;

    /**
     * 数据源类型
     */
    private String dataSourceType;

    /**
     * 更新策略
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
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 从Domain实体转换为输出DTO
     */
    public static DatasetOutput from(com.i0.report.domain.entities.Dataset dataset) {
        if (dataset == null) {
            return null;
        }

        List<FilterOutput> filterOutputs = null;
        if (dataset.getFilters() != null) {
            filterOutputs = dataset.getFilters().stream()
                .map(FilterOutput::from)
                .collect(java.util.stream.Collectors.toList());
        }

        return DatasetOutput.builder()
                .id(dataset.getId())
                .name(dataset.getName())
                .description(dataset.getDescription())
                .sql(dataset.getSql())
                .filters(filterOutputs)
                .dataSourceType(dataset.getDataSourceType())
                .updateStrategy(dataset.getUpdateStrategy())
                .updateInterval(dataset.getUpdateInterval())
                .enabled(dataset.getEnabled())
                .build();
    }

    /**
     * 从Domain实体列表转换为输出DTO列表
     */
    public static List<DatasetOutput> from(List<com.i0.report.domain.entities.Dataset> datasets) {
        if (datasets == null) {
            return new java.util.ArrayList<>();
        }
        return datasets.stream()
                .map(DatasetOutput::from)
                .collect(java.util.stream.Collectors.toList());
    }
}