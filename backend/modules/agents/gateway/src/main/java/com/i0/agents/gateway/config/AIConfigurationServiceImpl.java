package com.i0.agents.gateway.config;

import com.i0.agents.domain.services.AIConfigurationService;
import com.i0.agents.domain.valueobjects.mcp.MCPServerConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI配置服务实现
 * 位于Gateway层，负责从配置文件中读取配置信息
 * 组合AIProviderConfig来提供配置服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AIConfigurationServiceImpl implements AIConfigurationService {

    /**
     * AI提供商配置类
     */
    private final AIProviderConfig aiProviderConfig;

    @Override
    public String getActiveProvider() {
        return aiProviderConfig.getActive();
    }

    @Override
    public String getApiUrl() {
        return aiProviderConfig.getApiUrl();
    }

    @Override
    public String getApiKey() {
        return aiProviderConfig.getApiKey();
    }

    @Override
    public String getDefaultModel() {
        return aiProviderConfig.getDefaultModel();
    }

    @Override
    public String getProperty(String propertyName) {
        return aiProviderConfig.getProperty(propertyName);
    }

    @Override
    public int getTimeoutSeconds() {
        return aiProviderConfig.getTimeoutSeconds();
    }

    @Override
    public boolean isRetryEnabled() {
        return aiProviderConfig.isRetryEnabled();
    }

    @Override
    public int getMaxRetries() {
        return aiProviderConfig.getMaxRetries();
    }

    @Override
    public boolean isEnableMCPProtocol() {
        return aiProviderConfig.isEnableMCPProtocol();
    }

    @Override
    public List<MCPServerConfig> getMCPServerConfigs() {
        List<MCPServerConfig> configs = new ArrayList<>();
        for (AIProviderConfig.MCPServerConfig serverConfig : aiProviderConfig.getMCPServers()) {
            try {
                MCPServerConfig domainConfig = convertToDomainConfig(serverConfig);
                if (domainConfig != null) {
                    configs.add(domainConfig);
                }
            } catch (Exception e) {
                log.error("Failed to convert MCP server configuration: {}", serverConfig, e);
            }
        }
        return configs;
    }

    @Override
    public Map<String, Object> getAllProviders() {
        Map<String, AIProviderConfig.ProviderConfig> providers = aiProviderConfig.getProviders();
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, AIProviderConfig.ProviderConfig> entry : providers.entrySet()) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    /**
     * 转换为Domain层的MCPServerConfig
     */
    private MCPServerConfig convertToDomainConfig(AIProviderConfig.MCPServerConfig config) {
        if (config == null) {
            return null;
        }

        String name = config.getName();
        String typeCode = config.getType();
        boolean enabled = config.isEnabled();

        if (name == null || typeCode == null) {
            log.warn("MCP server configuration missing required fields: {}", config);
            return null;
        }

        return MCPServerConfig.builder()
                .name(name)
                .type(typeCode)
                .connectionConfig(new HashMap<>())
                .enabled(enabled)
                .build();
    }
}