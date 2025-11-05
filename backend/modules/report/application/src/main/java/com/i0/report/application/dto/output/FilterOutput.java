package com.i0.report.application.dto.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 过滤条件输出DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterOutput {

    /**
     * 过滤字段名
     */
    private String field;

    /**
     * 操作符：=, >, <, IN, LIKE, BETWEEN等
     */
    private String operator;

    /**
     * 过滤值
     */
    private Object value;

    /**
     * 是否为强制过滤条件（不可绕过）
     */
    private Boolean mandatory;

    /**
     * 从Domain值对象转换为输出DTO
     */
    public static FilterOutput from(com.i0.report.domain.valueobjects.Filter filter) {
        if (filter == null) {
            return null;
        }

        return FilterOutput.builder()
                .field(filter.getField())
                .operator(filter.getOperator())
                .value(filter.getValue())
                .mandatory(filter.getMandatory())
                .build();
    }

    /**
     * 从Domain值对象列表转换为输出DTO列表
     */
    public static java.util.List<FilterOutput> from(java.util.List<com.i0.report.domain.valueobjects.Filter> filters) {
        if (filters == null) {
            return new java.util.ArrayList<>();
        }
        return filters.stream()
                .map(FilterOutput::from)
                .collect(java.util.stream.Collectors.toList());
    }
}