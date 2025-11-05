package com.i0.report.application.dto.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 过滤条件输入DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterInput {

    /**
     * 过滤字段名
     */
    @NotBlank(message = "过滤字段名不能为空")
    private String field;

    /**
     * 操作符：=, >, <, IN, LIKE, BETWEEN等
     */
    @NotBlank(message = "操作符不能为空")
    private String operator;

    /**
     * 过滤值
     */
    @NotNull(message = "过滤值不能为空")
    private Object value;

    /**
     * 是否为强制过滤条件（不可绕过）
     */
    @Builder.Default
    private Boolean mandatory = false;

    /**
     * 验证过滤条件是否为范围操作
     */
    public boolean isRangeOperator() {
        return "BETWEEN".equalsIgnoreCase(operator) || "between".equals(operator);
    }

    /**
     * 验证过滤条件是否为列表操作
     */
    public boolean isListOperator() {
        return "IN".equalsIgnoreCase(operator) || "in".equals(operator)
            || "NOT IN".equalsIgnoreCase(operator) || "not in".equals(operator);
    }

    /**
     * 获取过滤值的字符串表示
     */
    public String getValueAsString() {
        if (value == null) {
            return "";
        }
        if (value instanceof List) {
            List<?> list = (List<?>) value;
            return String.join(",", list.stream().map(Object::toString).toArray(String[]::new));
        }
        return value.toString();
    }
}