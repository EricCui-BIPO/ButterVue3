package com.i0.agents.application.services.mcp;

import com.i0.agents.domain.services.AIConfigurationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * MCP初始化服务
 * 负责在应用启动时初始化MCP服务器和配置
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MCPInitializationService implements InitializingBean {

    private final MCPProtocolManager mcpProtocolManager;
    private final AIConfigurationService configurationService;
    private final BusinessFunctionMCPAdapter businessFunctionMCPAdapter;

    /**
     * 在应用启动后初始化MCP配置
     */
    @Override
    public void afterPropertiesSet() {
        if (!configurationService.isEnableMCPProtocol()) {
            log.info("MCP protocol is disabled, skipping initialization");
            return;
        }

        log.info("Initializing MCP protocol support...");

        try {
            // 注册业务函数适配器
            registerBusinessFunctionAdapter();

            // 注册其他MCP服务器配置
            registerConfiguredMCPServers();

            // 刷新服务器状态
            mcpProtocolManager.refreshServerStatus();

            log.info("MCP protocol initialization completed. Available servers: {}, Available tools: {}",
                mcpProtocolManager.getAvailableServerCount(),
                mcpProtocolManager.getAllAvailableTools().size());

        } catch (Exception e) {
            log.error("Failed to initialize MCP protocol", e);
        }
    }

    /**
     * 注册业务函数适配器
     */
    private void registerBusinessFunctionAdapter() {
        if (businessFunctionMCPAdapter.isAvailable()) {
            mcpProtocolManager.registerServer(businessFunctionMCPAdapter);
            log.info("Registered business function MCP adapter with {} functions",
                businessFunctionMCPAdapter.getRegisteredFunctionCount());
        } else {
            log.warn("Business function MCP adapter is not available (no functions registered)");
        }
    }

    /**
     * 注册配置的MCP服务器
     */
    private void registerConfiguredMCPServers() {
        for (com.i0.agents.domain.valueobjects.mcp.MCPServerConfig config : configurationService.getMCPServerConfigs()) {
            if (!config.isEnabled()) {
                log.debug("Skipping disabled MCP server: {}", config.getName());
                continue;
            }

            try {
                // 根据服务器类型创建相应的服务器实例
                // 目前只支持业务函数类型，未来可以扩展其他类型
                if (config.getType().getCode().equals("business-functions")) {
                    log.debug("Business functions server already registered, skipping: {}", config.getName());
                    continue;
                }

                log.warn("Unsupported MCP server type: {} for server: {}",
                    config.getType().getCode(), config.getName());

            } catch (Exception e) {
                log.error("Failed to register MCP server: {}", config.getName(), e);
            }
        }
    }
}