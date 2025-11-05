package com.i0.report.application.dto.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 创建数据集输入DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDatasetInput {

    /**
     * 数据集名称
     */
    @NotBlank(message = "数据集名称不能为空")
    @Size(max = 100, message = "数据集名称长度不能超过100个字符")
    private String name;

    /**
     * 数据集描述
     */
    @Size(max = 500, message = "数据集描述长度不能超过500个字符")
    private String description;

    /**
     * 原始 SQL 查询语句
     */
    @NotBlank(message = "SQL查询语句不能为空")
    @Size(max = 2000, message = "SQL查询语句长度不能超过2000个字符")
    private String sql;

    /**
     * 数据集过滤条件列表
     */
    private List<FilterInput> filters;

    /**
     * 数据源类型
     */
    @NotBlank(message = "数据源类型不能为空")
    @Size(max = 50, message = "数据源类型长度不能超过50个字符")
    private String dataSourceType;

    /**
     * 更新策略
     */
    @Builder.Default
    private String updateStrategy = "manual";

    /**
     * 更新间隔（分钟）
     */
    private Integer updateInterval;

    /**
     * 是否启用
     */
    @Builder.Default
    private Boolean enabled = true;
}