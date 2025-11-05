package com.i0.report.application.dto.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 数据集分页输出DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatasetPageOutput {

    /**
     * 数据集ID
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
     * 数据源类型
     */
    private String dataSourceType;

    /**
     * 更新策略
     */
    private String updateStrategy;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 创建时间
     */
    private java.time.LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private java.time.LocalDateTime updatedAt;

    /**
     * 从Domain实体转换为分页输出DTO
     */
    public static DatasetPageOutput from(com.i0.report.domain.entities.Dataset dataset) {
        if (dataset == null) {
            return null;
        }

        return DatasetPageOutput.builder()
                .id(dataset.getId())
                .name(dataset.getName())
                .description(dataset.getDescription())
                .dataSourceType(dataset.getDataSourceType())
                .updateStrategy(dataset.getUpdateStrategy())
                .enabled(dataset.getEnabled())
                .build();
    }

    /**
     * 从Domain实体列表转换为分页输出DTO列表
     */
    public static List<DatasetPageOutput> from(List<com.i0.report.domain.entities.Dataset> datasets) {
        if (datasets == null) {
            return new java.util.ArrayList<>();
        }
        return datasets.stream()
                .map(DatasetPageOutput::from)
                .collect(java.util.stream.Collectors.toList());
    }
}