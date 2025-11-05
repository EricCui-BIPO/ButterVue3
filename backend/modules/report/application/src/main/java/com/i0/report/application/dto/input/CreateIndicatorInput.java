package com.i0.report.application.dto.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 创建指标输入DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateIndicatorInput {

    /**
     * 指标名称
     */
    @NotBlank(message = "指标名称不能为空")
    @Size(max = 100, message = "指标名称长度不能超过100个字符")
    private String name;

    /**
     * 指标描述
     */
    @Size(max = 500, message = "指标描述长度不能超过500个字符")
    private String description;

    /**
     * 引用的数据集ID
     */
    @NotBlank(message = "数据集ID不能为空")
    private String datasetId;

    /**
     * 计算表达式
     */
    @NotBlank(message = "计算表达式不能为空")
    @Size(max = 200, message = "计算表达式长度不能超过200个字符")
    private String calculation;

    /**
     * 维度字段列表
     */
    private List<String> dimensions;

    /**
     * 指标过滤条件列表
     */
    private List<FilterInput> filters;

    /**
     * 指标类型
     */
    @NotBlank(message = "指标类型不能为空")
    @Size(max = 20, message = "指标类型长度不能超过20个字符")
    private String type;

    /**
     * 数据单位
     */
    @Size(max = 20, message = "数据单位长度不能超过20个字符")
    private String unit;

    /**
     * 数据格式化模式
     */
    @Size(max = 50, message = "数据格式化模式长度不能超过50个字符")
    private String formatPattern;

    /**
     * 是否启用
     */
    @Builder.Default
    private Boolean enabled = true;
}