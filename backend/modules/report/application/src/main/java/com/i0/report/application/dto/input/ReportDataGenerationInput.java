package com.i0.report.application.dto.input;

import com.i0.report.domain.valueobjects.Filter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 报表数据生成输入DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDataGenerationInput {

    /**
     * 报表ID
     */
    @NotBlank(message = "报表ID不能为空")
    private String reportId;

    /**
     * 额外的过滤条件
     */
    private List<Filter> additionalFilters;

    /**
     * 是否包含缓存数据
     */
    @Builder.Default
    private Boolean includeCacheData = true;

    /**
     * 强制刷新
     */
    @Builder.Default
    private Boolean forceRefresh = false;
}