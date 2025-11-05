package com.i0.agents.domain.valueobjects.mcp;

import com.i0.agents.domain.services.mcp.MCPServerType;
import java.util.Map;
import java.util.Objects;

/**
 * MCP服务器配置值对象
 * 定义MCP服务器的配置信息
 */
public class MCPServerConfig {

    private final String name;
    private final MCPServerType type;
    private final Map<String, Object> connectionConfig;
    private final boolean enabled;

    private MCPServerConfig(Builder builder) {
        this.name = builder.name;
        this.type = builder.type;
        this.connectionConfig = builder.connectionConfig;
        this.enabled = builder.enabled;

        validate();
    }

    public static Builder builder() {
        return new Builder();
    }

    private void validate() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("MCP server name cannot be null or empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("MCP server type cannot be null");
        }
    }

    // Getters
    public String getName() {
        return name;
    }

    public MCPServerType getType() {
        return type;
    }

    public Map<String, Object> getConnectionConfig() {
        return connectionConfig;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MCPServerConfig that = (MCPServerConfig) o;
        return enabled == that.enabled &&
               Objects.equals(name, that.name) &&
               type == that.type &&
               Objects.equals(connectionConfig, that.connectionConfig);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, connectionConfig, enabled);
    }

    @Override
    public String toString() {
        return String.format("MCPServerConfig{name='%s', type=%s, enabled=%s}",
                           name, type, enabled);
    }

    public static class Builder {
        private String name;
        private MCPServerType type;
        private Map<String, Object> connectionConfig;
        private boolean enabled = true;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder type(MCPServerType type) {
            this.type = type;
            return this;
        }

        public Builder type(String typeCode) {
            this.type = MCPServerType.fromCode(typeCode);
            return this;
        }

        public Builder connectionConfig(Map<String, Object> connectionConfig) {
            this.connectionConfig = connectionConfig;
            return this;
        }

        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public MCPServerConfig build() {
            return new MCPServerConfig(this);
        }
    }
}