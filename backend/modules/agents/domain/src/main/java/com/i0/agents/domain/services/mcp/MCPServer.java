package com.i0.agents.domain.services.mcp;

import java.util.List;
import java.util.Map;

/**
 * MCP服务器接口
 * 定义MCP服务器的基本契约
 */
public interface MCPServer {

    /**
     * 获取服务器名称
     */
    String getName();

    /**
     * 获取服务器版本
     */
    String getVersion();

    /**
     * 获取可用的工具列表
     */
    List<MCPTool> getAvailableTools();

    /**
     * 执行指定的工具
     */
    MCPToolResult executeTool(String toolName, Map<String, Object> arguments);

    /**
     * 检查服务器是否可用
     */
    boolean isAvailable();

    /**
     * 获取服务器类型
     */
    MCPServerType getType();
}