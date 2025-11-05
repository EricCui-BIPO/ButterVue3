package com.i0.agents.application.services.mcp;

import com.i0.agents.domain.services.BusinessFunctionRegistry;
import com.i0.agents.domain.services.mcp.MCPServer;
import com.i0.agents.domain.services.mcp.MCPServerType;
import com.i0.agents.domain.services.mcp.MCPTool;
import com.i0.agents.domain.services.mcp.MCPToolResult;
import com.i0.agents.domain.valueobjects.BusinessFunction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 业务函数MCP适配器
 * 将现有的BusinessFunctionRegistry适配为MCP服务器
 */
@Slf4j
@Component
public class BusinessFunctionMCPAdapter implements MCPServer {

    private final BusinessFunctionRegistry businessFunctionRegistry;
    private static final String SERVER_NAME = "business-functions";
    private static final String SERVER_VERSION = "1.0.0";

    public BusinessFunctionMCPAdapter(BusinessFunctionRegistry businessFunctionRegistry) {
        this.businessFunctionRegistry = businessFunctionRegistry;
    }

    @Override
    public String getName() {
        return SERVER_NAME;
    }

    @Override
    public String getVersion() {
        return SERVER_VERSION;
    }

    @Override
    public List<MCPTool> getAvailableTools() {
        if (businessFunctionRegistry == null) {
            return Collections.emptyList();
        }

        return businessFunctionRegistry.getAllFunctions().stream()
            .map(this::convertToMCPTool)
            .collect(Collectors.toList());
    }

    @Override
    public MCPToolResult executeTool(String toolName, Map<String, Object> arguments) {
        if (businessFunctionRegistry == null) {
            return MCPToolResult.failure(toolName, getName(), "Business function registry is not available");
        }

        try {
            log.debug("Executing business function '{}' via MCP adapter", toolName);
            BusinessFunction.FunctionCallResult result = businessFunctionRegistry.executeFunction(toolName, arguments);

            if (result.isSuccess()) {
                log.debug("Business function execution successful: {} - {} - data:{} - uiComponent:{}", toolName, result.getResult(), result.getData(), result.getUiComponent());
                return MCPToolResult.success(toolName, getName(), result.getResult(), result.getData(), result.getUiComponent());
            } else {
                log.warn("Business function execution failed: {} - {}", toolName, result.getError());
                return MCPToolResult.failure(toolName, getName(), result.getError());
            }

        } catch (Exception e) {
            log.error("Error executing business function '{}' via MCP adapter", toolName, e);
            return MCPToolResult.failure(toolName, getName(),
                String.format("Execution failed: %s", e.getMessage()));
        }
    }

    @Override
    public boolean isAvailable() {
        return businessFunctionRegistry != null && businessFunctionRegistry.count() > 0;
    }

    @Override
    public MCPServerType getType() {
        return MCPServerType.BUSINESS_FUNCTIONS;
    }

    /**
     * 将BusinessFunction转换为MCPTool
     */
    private MCPTool convertToMCPTool(BusinessFunction businessFunction) {
        Map<String, Object> parameters = convertParameters(businessFunction.getParameters(), businessFunction.getRequired());

        return MCPTool.builder()
            .name(businessFunction.getName())
            .description(businessFunction.getDescription())
            .parameters(parameters)
            .sourceServer(getName())
            .build();
    }

    /**
     * 转换参数格式
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> convertParameters(Map<String, BusinessFunction.PropertyDefinition> parameters, List<String> requiredList) {
        if (parameters == null || parameters.isEmpty()) {
            return Map.of(
                "type", "object",
                "properties", Collections.emptyMap(),
                "required", Collections.emptyList()
            );
        }

        Map<String, Object> properties = new HashMap<>();
        List<String> required = new ArrayList<>();

        for (Map.Entry<String, BusinessFunction.PropertyDefinition> entry : parameters.entrySet()) {
            String paramName = entry.getKey();
            BusinessFunction.PropertyDefinition propertyDef = entry.getValue();

            properties.put(paramName, propertyDef.toMap());

            if (requiredList.contains(paramName)) {
                required.add(paramName);
            }
        }

        return Map.of(
            "type", "object",
            "properties", properties,
            "required", required
        );
    }

    /**
     * 获取已注册的业务函数数量
     */
    public int getRegisteredFunctionCount() {
        return businessFunctionRegistry != null ? businessFunctionRegistry.count() : 0;
    }

    /**
     * 检查指定的业务函数是否存在
     */
    public boolean hasFunction(String functionName) {
        return businessFunctionRegistry != null && businessFunctionRegistry.isRegistered(functionName);
    }

    /**
     * 获取所有已注册的函数名称
     */
    public Set<String> getFunctionNames() {
        return businessFunctionRegistry != null ? businessFunctionRegistry.getFunctionNames() : Collections.emptySet();
    }
}