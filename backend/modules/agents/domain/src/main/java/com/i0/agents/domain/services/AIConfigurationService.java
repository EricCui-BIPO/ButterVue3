package com.i0.agents.domain.services;

import com.i0.agents.domain.valueobjects.mcp.MCPServerConfig;

import java.util.List;
import java.util.Map;

/**
 * AI配置服务接口
 * 定义AI配置相关的核心业务操作
 */
public interface AIConfigurationService {

    /**
     * 获取当前活跃的AI提供商
     */
    String getActiveProvider();

    /**
     * 获取当前活跃厂商的API URL
     */
    String getApiUrl();

    /**
     * 获取当前活跃厂商的API Key
     */
    String getApiKey();

    /**
     * 获取当前活跃厂商的默认模型
     */
    String getDefaultModel();

    /**
     * 获取当前活跃厂商的指定属性
     */
    String getProperty(String propertyName);

    /**
     * 获取请求超时时间
     */
    int getTimeoutSeconds();

    /**
     * 是否启用重试机制
     */
    boolean isRetryEnabled();

    /**
     * 获取最大重试次数
     */
    int getMaxRetries();

    /**
     * 是否启用MCP协议
     */
    boolean isEnableMCPProtocol();

    /**
     * 获取MCP服务器配置列表
     */
    List<MCPServerConfig> getMCPServerConfigs();

    /**
     * 获取所有提供商配置
     */
    Map<String, Object> getAllProviders();
}