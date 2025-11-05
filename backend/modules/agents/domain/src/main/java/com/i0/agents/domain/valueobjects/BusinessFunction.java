package com.i0.agents.domain.valueobjects;

import java.util.List;
import java.util.Map;

/**
 * 业务函数定义
 * 用于描述一个可被AI调用的业务API
 */
public class BusinessFunction {

    private final String name;
    private final String description;
    private final Map<String, PropertyDefinition> parameters;
    private final List<String> required;
    private final FunctionHandler handler;

    private BusinessFunction(Builder builder) {
        this.name = builder.name;
        this.description = builder.description;
        this.parameters = builder.parameters;
        this.required = builder.required;
        this.handler = builder.handler;
    }

    /**
     * 创建业务函数构建器
     */
    public static Builder builder() {
        return new Builder();
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Map<String, PropertyDefinition> getParameters() {
        return parameters;
    }

    public List<String> getRequired() {
        return required;
    }

    public FunctionHandler getHandler() {
        return handler;
    }

    /**
     * 转换为BigModel API格式的函数定义
     */
    public Map<String, Object> toBigModelFunctionDefinition() {
        return Map.of(
            "name", this.name,
            "description", this.description,
            "parameters", Map.of(
                "type", "object",
                "properties", convertParametersToMap(),
                "required", this.required
            )
        );
    }

    private Map<String, Object> convertParametersToMap() {
        return parameters.entrySet().stream()
            .collect(java.util.stream.Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().toMap()
            ));
    }

    /**
     * 属性定义
     */
    public static class PropertyDefinition {
        private final String type;
        private final String description;
        private final List<String> enumValues;
        private final Object defaultValue;

        private PropertyDefinition(Builder builder) {
            this.type = builder.type;
            this.description = builder.description;
            this.enumValues = builder.enumValues;
            this.defaultValue = builder.defaultValue;
        }

        public static Builder builder() {
            return new Builder();
        }

        public Map<String, Object> toMap() {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("type", type);
            map.put("description", description);

            if (enumValues != null && !enumValues.isEmpty()) {
                map.put("enum", enumValues);
            }

            if (defaultValue != null) {
                map.put("default", defaultValue);
            }

            return map;
        }

        // Getters
        public String getType() { return type; }
        public String getDescription() { return description; }
        public List<String> getEnumValues() { return enumValues; }
        public Object getDefaultValue() { return defaultValue; }

        public static class Builder {
            private String type;
            private String description;
            private List<String> enumValues;
            private Object defaultValue;

            public Builder type(String type) {
                this.type = type;
                return this;
            }

            public Builder description(String description) {
                this.description = description;
                return this;
            }

            public Builder enumValues(List<String> enumValues) {
                this.enumValues = enumValues;
                return this;
            }

            public Builder defaultValue(Object defaultValue) {
                this.defaultValue = defaultValue;
                return this;
            }

            public PropertyDefinition build() {
                return new PropertyDefinition(this);
            }
        }
    }

    /**
     * 函数处理器接口
     */
    @FunctionalInterface
    public interface FunctionHandler {
        FunctionCallResult execute(Map<String, Object> arguments);
    }

    /**
     * 函数调用结果
     */
    public static class FunctionCallResult {
        private final boolean success;
        private final String result;
        private final String error;
        private final Map<String, Object> data;
        private final Object uiComponent;

        private FunctionCallResult(Builder builder) {
            this.success = builder.success;
            this.result = builder.result;
            this.error = builder.error;
            this.data = builder.data;
            this.uiComponent = builder.uiComponent;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static FunctionCallResult success(String result) {
            return builder().success(true).result(result).build();
        }

        public static FunctionCallResult success(String result, Map<String, Object> data) {
            return builder().success(true).result(result).data(data).build();
        }

        public static FunctionCallResult success(String result, Map<String, Object> data, Object uiComponent) {
            return builder().success(true).result(result).data(data).uiComponent(uiComponent).build();
        }

        public static FunctionCallResult failure(String error) {
            return builder().success(false).error(error).build();
        }

        // Getters
        public boolean isSuccess() { return success; }
        public String getResult() { return result; }
        public String getError() { return error; }
        public Map<String, Object> getData() { return data; }
        public Object getUiComponent() { return uiComponent; }

        public static class Builder {
            private boolean success;
            private String result;
            private String error;
            private Map<String, Object> data;
            private Object uiComponent;

            public Builder success(boolean success) {
                this.success = success;
                return this;
            }

            public Builder result(String result) {
                this.result = result;
                return this;
            }

            public Builder error(String error) {
                this.error = error;
                return this;
            }

            public Builder data(Map<String, Object> data) {
                this.data = data;
                return this;
            }

            public Builder uiComponent(Object uiComponent) {
                this.uiComponent = uiComponent;
                return this;
            }

            public FunctionCallResult build() {
                return new FunctionCallResult(this);
            }
        }
    }

    public static class Builder {
        private String name;
        private String description;
        private Map<String, PropertyDefinition> parameters = new java.util.HashMap<>();
        private List<String> required = new java.util.ArrayList<>();
        private FunctionHandler handler;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder parameter(String name, PropertyDefinition parameter) {
            this.parameters.put(name, parameter);
            return this;
        }

        public Builder parameter(String name, String type, String description) {
            return parameter(name, PropertyDefinition.builder()
                .type(type)
                .description(description)
                .build());
        }

        public Builder required(String parameterName) {
            if (!this.required.contains(parameterName)) {
                this.required.add(parameterName);
            }
            return this;
        }

        public Builder handler(FunctionHandler handler) {
            this.handler = handler;
            return this;
        }

        public BusinessFunction build() {
            if (name == null || description == null || handler == null) {
                throw new IllegalArgumentException("name, description, and handler are required");
            }
            return new BusinessFunction(this);
        }
    }
}