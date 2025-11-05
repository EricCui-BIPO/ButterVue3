package com.i0.agents.gateway.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI服务提供商配置 - Gateway层实现
 * 使用完整注解支持YAML配置动态绑定
 * 支持配置动态刷新
 */
@Slf4j
@Data
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "ai.provider")
public class AIProviderConfig {

    /**
     * 当前使用的AI提供商
     */
    private String active = "siliconflow";

    /**
     * 请求超时时间（秒）
     */
    private int timeoutSeconds = 30;

    /**
     * 是否启用重试机制
     */
    private boolean retryEnabled = true;

    /**
     * 最大重试次数
     */
    private int maxRetries = 3;

    /**
     * MCP配置
     */
    private MCPConfiguration mcp = new MCPConfiguration();

    /**
     * 所有提供商配置，从YAML中动态加载
     */
    private Map<String, ProviderConfig> providers = new HashMap<>();

    // ========== 对外提供的直接属性 - 基于当前活跃厂商 ==========

    /**
     * 获取当前活跃厂商的API URL
     */
    public String getApiUrl() {
        ProviderConfig activeProvider = getActiveProvider();
        return activeProvider != null ? activeProvider.getApiUrl() : null;
    }

    /**
     * 获取当前活跃厂商的API Key
     */
    public String getApiKey() {
        ProviderConfig activeProvider = getActiveProvider();
        return activeProvider != null ? activeProvider.getApiKey() : null;
    }

    /**
     * 获取当前活跃厂商的默认模型
     */
    public String getDefaultModel() {
        ProviderConfig activeProvider = getActiveProvider();
        return activeProvider != null ? activeProvider.getDefaultModel() : null;
    }

    /**
     * 获取当前活跃厂商的任意属性
     */
    public String getProperty(String propertyName) {
        ProviderConfig activeProvider = getActiveProvider();
        if (activeProvider != null) {
            return activeProvider.getProperty(propertyName);
        }
        return null;
    }

    /**
     * 获取当前活跃厂商配置
     */
    public ProviderConfig getActiveProvider() {
        return providers.get(active);
    }

    /**
     * 获取所有提供商配置
     */
    public Map<String, ProviderConfig> getProviders() {
        return providers;
    }

    // ========== MCP配置相关方法 ==========

    /**
     * 获取MCP协议启用状态
     */
    public boolean isEnableMCPProtocol() {
        return mcp.isEnabled();
    }

    /**
     * 获取MCP服务器配置列表
     */
    public List<MCPServerConfig> getMCPServers() {
        return mcp.getServers();
    }

    // ========== 内部配置类 ==========

    /**
     * MCP配置内部类
     */
    @Data
    public static class MCPConfiguration {
        /**
         * 是否启用MCP协议
         */
        private boolean enabled = false;

        /**
         * MCP服务器配置列表
         */
        private List<MCPServerConfig> servers = new ArrayList<>();
    }

    /**
     * MCP服务器配置
     */
    @Data
    public static class MCPServerConfig {
        /**
         * 服务器名称
         */
        private String name;

        /**
         * 服务器类型
         */
        private String type;

        /**
         * 是否启用
         */
        private boolean enabled = true;
    }

    /**
     * AI提供商配置
     */
    @Data
    public static class ProviderConfig {
        /**
         * API URL
         */
        private String apiUrl;

        /**
         * API Key
         */
        private String apiKey;

        /**
         * 默认模型
         */
        private String defaultModel;

        /**
         * 超时时间
         */
        private int timeout = 30;

        /**
         * 最大token数
         */
        private int maxTokens = 4096;

        /**
         * 额外配置
         */
        private Map<String, Object> extra = new HashMap<>();

        /**
         * 获取任意属性
         */
        public String getProperty(String propertyName) {
            if (extra != null && extra.containsKey(propertyName)) {
                Object value = extra.get(propertyName);
                return value != null ? value.toString() : null;
            }
            return null;
        }
    }
}