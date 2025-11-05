package com.i0.report.domain.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * 过滤条件值对象
 * 统一定义过滤条件（权限、默认、动态）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Filter {

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
     * 验证过滤条件是否有效
     */
    public boolean isValid() {
        return field != null && !field.trim().isEmpty()
            && operator != null && !operator.trim().isEmpty()
            && value != null;
    }

    /**
     * 判断是否为相等操作
     */
    public boolean isEqualOperator() {
        return "=".equals(operator) || "==".equals(operator);
    }

    /**
     * 判断是否为包含操作
     */
    public boolean isInOperator() {
        return "IN".equalsIgnoreCase(operator) || "in".equals(operator);
    }

    /**
     * 判断是否为模糊匹配操作
     */
    public boolean isLikeOperator() {
        return "LIKE".equalsIgnoreCase(operator) || "like".equals(operator);
    }

    /**
     * 判断是否为范围操作
     */
    public boolean isBetweenOperator() {
        return "BETWEEN".equalsIgnoreCase(operator) || "between".equals(operator);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Filter filter = (Filter) o;
        return Objects.equals(field, filter.field) &&
               Objects.equals(operator, filter.operator) &&
               Objects.equals(value, filter.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, operator, value);
    }

    @Override
    public String toString() {
        return "Filter{" +
                "field='" + field + '\'' +
                ", operator='" + operator + '\'' +
                ", value=" + value +
                ", mandatory=" + mandatory +
                '}';
    }
}