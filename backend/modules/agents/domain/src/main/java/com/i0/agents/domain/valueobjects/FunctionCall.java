package com.i0.agents.domain.valueobjects;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Objects;

/**
 * 函数调用值对象
 * 表示AI模型识别的函数调用信息
 */
public class FunctionCall {
    private final String functionName;
    private final Map<String, Object> arguments;
    private final String description;

    private FunctionCall(String functionName, Map<String, Object> arguments, String description) {
        this.functionName = functionName;
        this.arguments = arguments;
        this.description = description;
    }

    /**
     * 创建函数调用
     */
    public static FunctionCall of(String functionName, Map<String, Object> arguments) {
        if (StringUtils.isBlank(functionName)) {
            throw new IllegalArgumentException("函数名称不能为空");
        }

        if (arguments == null) {
            throw new IllegalArgumentException("函数参数不能为空");
        }

        return new FunctionCall(functionName.trim(), arguments, null);
    }

    /**
     * 创建带描述的函数调用
     */
    public static FunctionCall of(String functionName, Map<String, Object> arguments, String description) {
        FunctionCall functionCall = of(functionName, arguments);
        return new FunctionCall(functionCall.functionName, functionCall.arguments, description);
    }

    /**
     * 获取函数名称
     */
    public String getFunctionName() {
        return functionName;
    }

    /**
     * 获取函数参数
     */
    public Map<String, Object> getArguments() {
        return arguments;
    }

    /**
     * 获取描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 获取指定参数值
     */
    public Object getArgument(String key) {
        return arguments.get(key);
    }

    /**
     * 获取指定参数值，带默认值
     */
    @SuppressWarnings("unchecked")
    public <T> T getArgument(String key, T defaultValue) {
        Object value = arguments.get(key);
        return value != null ? (T) value : defaultValue;
    }

    /**
     * 检查是否包含指定参数
     */
    public boolean hasArgument(String key) {
        return arguments.containsKey(key);
    }

    /**
     * 检查是否为创建实体函数
     */
    public boolean isCreateEntityFunction() {
        return "create_entity".equals(functionName) || "createEntity".equals(functionName);
    }

    /**
     * 检查是否为查询函数
     */
    public boolean isQueryFunction() {
        return functionName.startsWith("get_") || functionName.startsWith("query_") ||
               functionName.startsWith("find_") || functionName.startsWith("search_");
    }

    /**
     * 检查是否为更新函数
     */
    public boolean isUpdateFunction() {
        return functionName.startsWith("update_") || functionName.startsWith("modify_") ||
               functionName.startsWith("edit_") || functionName.startsWith("change_");
    }

    /**
     * 检查是否为删除函数
     */
    public boolean isDeleteFunction() {
        return functionName.startsWith("delete_") || functionName.startsWith("remove_") ||
               functionName.startsWith("destroy_");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FunctionCall that = (FunctionCall) o;
        return functionName.equals(that.functionName) && arguments.equals(that.arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(functionName, arguments);
    }

    @Override
    public String toString() {
        return "FunctionCall{" +
                "functionName='" + functionName + '\'' +
                ", arguments=" + arguments.keySet() +
                ", description='" + description + '\'' +
                '}';
    }
}