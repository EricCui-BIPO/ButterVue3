package com.i0.agents.domain.services.mcp;

import java.util.Map;
import java.util.Objects;

/**
 * MCP工具执行结果
 * 封装MCP工具执行的返回结果
 */
public class MCPToolResult {

    private final boolean success;
    private final String result;
    private final String error;
    private final Map<String, Object> data;
    private final Object uiComponent;
    private final String toolName;
    private final String serverName;

    private MCPToolResult(Builder builder) {
        this.success = builder.success;
        this.result = builder.result;
        this.error = builder.error;
        this.data = builder.data;
        this.uiComponent = builder.uiComponent;
        this.toolName = builder.toolName;
        this.serverName = builder.serverName;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static MCPToolResult success(String toolName, String serverName, String result) {
        return builder()
                .success(true)
                .toolName(toolName)
                .serverName(serverName)
                .result(result)
                .build();
    }

    public static MCPToolResult success(String toolName, String serverName, String result, Map<String, Object> data) {
        return builder()
                .success(true)
                .toolName(toolName)
                .serverName(serverName)
                .result(result)
                .data(data)
                .build();
    }

    public static MCPToolResult success(String toolName, String serverName, String result, Map<String, Object> data, Object uiComponent) {
        return builder()
                .success(true)
                .toolName(toolName)
                .serverName(serverName)
                .result(result)
                .data(data)
                .uiComponent(uiComponent)
                .build();
    }

    public static MCPToolResult failure(String toolName, String serverName, String error) {
        return builder()
                .success(false)
                .toolName(toolName)
                .serverName(serverName)
                .error(error)
                .build();
    }

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public String getResult() {
        return result;
    }

    public String getError() {
        return error;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Object getUiComponent() {
        return uiComponent;
    }

    public String getToolName() {
        return toolName;
    }

    public String getServerName() {
        return serverName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MCPToolResult that = (MCPToolResult) o;
        return success == that.success &&
               Objects.equals(result, that.result) &&
               Objects.equals(error, that.error) &&
               Objects.equals(data, that.data) &&
               Objects.equals(uiComponent, that.uiComponent) &&
               Objects.equals(toolName, that.toolName) &&
               Objects.equals(serverName, that.serverName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(success, result, error, data, uiComponent, toolName, serverName);
    }

    @Override
    public String toString() {
        return String.format("MCPToolResult{success=%s, toolName='%s', serverName='%s'}",
                           success, toolName, serverName);
    }

    public static class Builder {
        private boolean success;
        private String result;
        private String error;
        private Map<String, Object> data;
        private Object uiComponent;
        private String toolName;
        private String serverName;

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

        public Builder toolName(String toolName) {
            this.toolName = toolName;
            return this;
        }

        public Builder serverName(String serverName) {
            this.serverName = serverName;
            return this;
        }

        public MCPToolResult build() {
            if (toolName == null || toolName.trim().isEmpty()) {
                throw new IllegalArgumentException("Tool name cannot be null or empty");
            }
            if (serverName == null || serverName.trim().isEmpty()) {
                throw new IllegalArgumentException("Server name cannot be null or empty");
            }
            if (success && (result == null || result.trim().isEmpty())) {
                throw new IllegalArgumentException("Successful result must have a non-null result");
            }
            if (!success && (error == null || error.trim().isEmpty())) {
                throw new IllegalArgumentException("Failed result must have a non-null error");
            }
            return new MCPToolResult(this);
        }
    }
}