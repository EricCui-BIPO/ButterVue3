package com.i0.agents.domain.services.mcp;

import java.util.Map;
import java.util.Objects;

/**
 * MCP工具定义
 * 描述MCP协议中的工具信息
 */
public class MCPTool {

    private final String name;
    private final String description;
    private final Map<String, Object> parameters;
    private final String sourceServer;

    private MCPTool(Builder builder) {
        this.name = builder.name;
        this.description = builder.description;
        this.parameters = builder.parameters;
        this.sourceServer = builder.sourceServer;
    }

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

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public String getSourceServer() {
        return sourceServer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MCPTool mcpTool = (MCPTool) o;
        return Objects.equals(name, mcpTool.name) &&
               Objects.equals(sourceServer, mcpTool.sourceServer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, sourceServer);
    }

    @Override
    public String toString() {
        return String.format("MCPTool{name='%s', sourceServer='%s'}", name, sourceServer);
    }

    public static class Builder {
        private String name;
        private String description;
        private Map<String, Object> parameters;
        private String sourceServer;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder parameters(Map<String, Object> parameters) {
            this.parameters = parameters;
            return this;
        }

        public Builder sourceServer(String sourceServer) {
            this.sourceServer = sourceServer;
            return this;
        }

        public MCPTool build() {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Tool name cannot be null or empty");
            }
            if (description == null || description.trim().isEmpty()) {
                throw new IllegalArgumentException("Tool description cannot be null or empty");
            }
            return new MCPTool(this);
        }
    }
}