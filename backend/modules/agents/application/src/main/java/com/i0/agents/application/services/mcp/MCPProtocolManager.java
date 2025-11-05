package com.i0.agents.application.services.mcp;

import com.i0.agents.domain.services.mcp.MCPServer;
import com.i0.agents.domain.services.mcp.MCPTool;
import com.i0.agents.domain.services.mcp.MCPToolResult;
import com.i0.agents.domain.valueobjects.mcp.MCPServerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * MCP协议管理器
 * 负责管理所有MCP服务器和工具的生命周期
 */
@Slf4j
@Component
public class MCPProtocolManager {

    private final Map<String, MCPServer> mcpServers = new ConcurrentHashMap<>();
    private final List<MCPServerConfig> serverConfigs = new ArrayList<>();

    /**
     * 注册MCP服务器
     */
    public void registerServer(MCPServer server) {
        if (server == null) {
            throw new IllegalArgumentException("MCP server cannot be null");
        }

        String serverName = server.getName();
        if (serverName == null || serverName.trim().isEmpty()) {
            throw new IllegalArgumentException("MCP server name cannot be null or empty");
        }

        if (mcpServers.containsKey(serverName)) {
            log.warn("MCP server '{}' is already registered, it will be overwritten", serverName);
        }

        mcpServers.put(serverName, server);
        log.info("Registered MCP server: {} (type: {})", serverName, server.getType());
    }

    /**
     * 注销MCP服务器
     */
    public boolean unregisterServer(String serverName) {
        MCPServer removed = mcpServers.remove(serverName);
        if (removed != null) {
            log.info("Unregistered MCP server: {}", serverName);
            return true;
        }
        return false;
    }

    /**
     * 获取指定名称的MCP服务器
     */
    public Optional<MCPServer> getServer(String serverName) {
        return Optional.ofNullable(mcpServers.get(serverName));
    }

    /**
     * 获取所有已注册的MCP服务器
     */
    public Collection<MCPServer> getAllServers() {
        return new ArrayList<>(mcpServers.values());
    }

    /**
     * 获取所有可用的工具
     */
    public List<MCPTool> getAllAvailableTools() {
        return mcpServers.values().stream()
                .filter(MCPServer::isAvailable)
                .flatMap(server -> server.getAvailableTools().stream())
                .collect(Collectors.toList());
    }

    /**
     * 根据工具名称查找工具
     */
    public Optional<MCPTool> findToolByName(String toolName) {
        return mcpServers.values().stream()
                .filter(MCPServer::isAvailable)
                .flatMap(server -> server.getAvailableTools().stream())
                .filter(tool -> tool.getName().equals(toolName))
                .findFirst();
    }

    /**
     * 根据工具名称查找对应的服务器
     */
    public Optional<MCPServer> findServerByToolName(String toolName) {
        return mcpServers.values().stream()
                .filter(MCPServer::isAvailable)
                .filter(server -> server.getAvailableTools().stream()
                        .anyMatch(tool -> tool.getName().equals(toolName)))
                .findFirst();
    }

    /**
     * 执行指定的工具
     */
    public MCPToolResult executeTool(String toolName, Map<String, Object> arguments) {
        Optional<MCPServer> serverOpt = findServerByToolName(toolName);
        if (!serverOpt.isPresent()) {
            return MCPToolResult.failure(toolName, "unknown",
                    String.format("Tool '%s' not found in any available MCP server", toolName));
        }

        MCPServer server = serverOpt.get();
        try {
            log.debug("Executing tool '{}' on server '{}'", toolName, server.getName());
            MCPToolResult result = server.executeTool(toolName, arguments);
            log.debug("Tool execution result: {} - {}", result.isSuccess() ? "SUCCESS" : "FAILURE",
                     result.isSuccess() ? result.getResult() : result.getError());
            return result;
        } catch (Exception e) {
            log.error("Error executing tool '{}' on server '{}'", toolName, server.getName(), e);
            return MCPToolResult.failure(toolName, server.getName(),
                    String.format("Tool execution failed: %s", e.getMessage()));
        }
    }

    /**
     * 检查是否有任何MCP服务器可用
     */
    public boolean hasAvailableServers() {
        return mcpServers.values().stream().anyMatch(MCPServer::isAvailable);
    }

    /**
     * 获取可用服务器的数量
     */
    public int getAvailableServerCount() {
        return (int) mcpServers.values().stream().filter(MCPServer::isAvailable).count();
    }

    /**
     * 获取已注册服务器的数量
     */
    public int getRegisteredServerCount() {
        return mcpServers.size();
    }

    /**
     * 清空所有注册的服务器
     */
    public void clear() {
        int count = mcpServers.size();
        mcpServers.clear();
        log.info("Cleared {} registered MCP servers", count);
    }

    /**
     * 添加服务器配置
     */
    public void addServerConfig(MCPServerConfig config) {
        if (config != null) {
            serverConfigs.add(config);
        }
    }

    /**
     * 获取所有服务器配置
     */
    public List<MCPServerConfig> getServerConfigs() {
        return new ArrayList<>(serverConfigs);
    }

    /**
     * 刷新服务器状态
     */
    public void refreshServerStatus() {
        mcpServers.values().forEach(server -> {
            boolean isAvailable = server.isAvailable();
            log.debug("Server '{}' availability: {}", server.getName(), isAvailable);
        });
    }
}