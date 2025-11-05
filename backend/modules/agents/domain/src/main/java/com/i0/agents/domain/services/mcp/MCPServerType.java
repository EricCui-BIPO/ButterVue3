package com.i0.agents.domain.services.mcp;

/**
 * MCP服务器类型枚举
 */
public enum MCPServerType {

    /**
     * 业务函数适配器类型
     */
    BUSINESS_FUNCTIONS("business-functions", "业务函数适配器"),

    /**
     * 外部MCP服务器类型
     */
    EXTERNAL_MCP("external-mcp", "外部MCP服务器"),

    /**
     * 自定义类型
     */
    CUSTOM("custom", "自定义服务器");

    private final String code;
    private final String description;

    MCPServerType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据代码获取类型
     */
    public static MCPServerType fromCode(String code) {
        for (MCPServerType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown MCP server type code: " + code);
    }

    /**
     * 检查是否为有效的服务器类型
     */
    public static boolean isValidCode(String code) {
        for (MCPServerType type : values()) {
            if (type.code.equals(code)) {
                return true;
            }
        }
        return false;
    }
}