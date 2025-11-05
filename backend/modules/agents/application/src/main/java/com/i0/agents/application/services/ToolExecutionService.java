package com.i0.agents.application.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.i0.agents.application.dto.output.ChatResponse;
import com.i0.agents.application.services.AIRequestService.ToolCallInfo;
import com.i0.agents.application.services.mcp.MCPProtocolManager;
import com.i0.agents.domain.services.AIConfigurationService;
import com.i0.agents.domain.services.BusinessFunctionRegistry;
import com.i0.agents.domain.services.mcp.MCPToolResult;
import com.i0.agents.domain.valueobjects.BusinessFunction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 工具执行处理服务
 * 职责：执行MCP工具和业务函数，处理结果，协调UI事件发送
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ToolExecutionService {

    private final BusinessFunctionRegistry businessFunctionRegistry;
    private final ObjectMapper objectMapper;
    private final UIEventService uiEventService;
    private final MCPProtocolManager mcpProtocolManager;
    private final AIConfigurationService configurationService;

    /**
     * 执行工具调用并生成响应
     *
     * @param toolCalls 工具调用列表
     * @param sessionId 会话ID
     * @param emitter SSE发射器（可为null）
     * @return 工具执行响应
     */
    public CompletableFuture<ToolExecutionResult> executeTools(List<ToolCallInfo> toolCalls, String sessionId, SseEmitter emitter) {
        return CompletableFuture.supplyAsync(() -> {
            List<ChatResponse> responses = new ArrayList<>();
            boolean hasError = false;

            for (ToolCallInfo toolCall : toolCalls) {
                try {
                    Map<String, Object> arguments = parseArguments(toolCall.getArguments());
                    ChatResponse response = executeSingleTool(toolCall, arguments, sessionId, emitter);
                    if (response != null) {
                        responses.add(response);
                    }
                } catch (Exception e) {
                    hasError = true;
                    String errorText = String.format("❌ %s: 执行异常 - %s", toolCall.getFunctionName(), e.getMessage());
                    responses.add(ChatResponse.fromText(errorText));
                    log.error("Tool execution error: {}", toolCall.getFunctionName(), e);
                }
            }
            return new ToolExecutionResult(responses, hasError);
        });
    }

    /**
     * 执行单个工具
     */
    private ChatResponse executeSingleTool(ToolCallInfo toolCall, Map<String, Object> arguments, String sessionId, SseEmitter emitter) {
        if (configurationService.isEnableMCPProtocol()) {
            return executeMCPTool(toolCall, arguments, sessionId, emitter);
        } else {
            return executeBusinessFunction(toolCall, arguments, sessionId, emitter);
        }
    }

    /**
     * 执行MCP工具
     */
    private ChatResponse executeMCPTool(ToolCallInfo toolCall, Map<String, Object> arguments, String sessionId, SseEmitter emitter) {
        try {
            MCPToolResult mcpResult = mcpProtocolManager.executeTool(toolCall.getFunctionName(), arguments);

            if (mcpResult.isSuccess()) {
                String resultText = String.format("✅ %s: %s", toolCall.getFunctionName(), mcpResult.getResult());

                ChatResponse response = ChatResponse.fromMCPResult(mcpResult)
                        .toBuilder()
                        .textContent(resultText)
                        .build();

                // 立即发送UI Component事件（如果存在）
                if (response.hasUIComponent() && emitter != null) {
                    uiEventService.sendUIComponentEvent(response.getUiComponent(), response.getData(), sessionId, emitter);
                }

                log.info("MCP tool executed successfully: {} from server: {} - {}",
                    toolCall.getFunctionName(), mcpResult.getServerName(), mcpResult.getResult());

                return response;
            } else {
                String errorText = String.format("❌ %s: %s", toolCall.getFunctionName(), mcpResult.getError());
                log.warn("MCP tool execution failed: {} from server: {} - {}",
                    toolCall.getFunctionName(), mcpResult.getServerName(), mcpResult.getError());
                return ChatResponse.fromText(errorText);
            }
        } catch (Exception e) {
            log.error("Error executing MCP tool: {}", toolCall.getFunctionName(), e);
            return ChatResponse.fromText(String.format("❌ %s: 执行失败 - %s", toolCall.getFunctionName(), e.getMessage()));
        }
    }

    /**
     * 执行业务函数
     */
    private ChatResponse executeBusinessFunction(ToolCallInfo toolCall, Map<String, Object> arguments, String sessionId, SseEmitter emitter) {
        try {
            BusinessFunction.FunctionCallResult result = businessFunctionRegistry.executeFunction(toolCall.getFunctionName(), arguments);

            if (result.isSuccess()) {
                String resultText = String.format("✅ %s: %s", toolCall.getFunctionName(), result.getResult());

                ChatResponse response = ChatResponse.fromFunctionResult(result)
                        .toBuilder()
                        .textContent(resultText)
                        .build();

                // 立即发送UI Component事件（如果存在）
                if (response.hasUIComponent() && emitter != null) {
                    uiEventService.sendUIComponentEvent(response.getUiComponent(), response.getData(), sessionId, emitter);
                }

                log.info("Business function executed successfully: {} - {}", toolCall.getFunctionName(), result.getResult());

                return response;
            } else {
                String errorText = String.format("❌ %s: %s", toolCall.getFunctionName(), result.getError());
                log.warn("Business function execution failed: {} - {}", toolCall.getFunctionName(), result.getError());
                return ChatResponse.fromText(errorText);
            }
        } catch (Exception e) {
            log.error("Error executing business function: {}", toolCall.getFunctionName(), e);
            return ChatResponse.fromText(String.format("❌ %s: 执行失败 - %s", toolCall.getFunctionName(), e.getMessage()));
        }
    }

    
    /**
     * 解析工具参数
     */
    private Map<String, Object> parseArguments(String argumentsJson) {
        try {
            if (argumentsJson == null || argumentsJson.trim().isEmpty()) {
                return Collections.emptyMap();
            }
            return objectMapper.readValue(argumentsJson, Map.class);
        } catch (Exception e) {
            log.error("Failed to parse arguments: {}", argumentsJson, e);
            return Collections.emptyMap();
        }
    }

    /**
     * 生成摘要响应
     */
    private String generateSummaryResponse(List<String> toolResults) {
        StringBuilder summary = new StringBuilder();
        summary.append("我已经为您执行了相关操作：\n\n");

        for (String result : toolResults) {
            summary.append(result).append("\n");
        }

        summary.append("\n如需进一步帮助，请继续告诉我。");
        return summary.toString();
    }

    /**
     * 工具执行结果
     */
    public static class ToolExecutionResult {
        private final List<ChatResponse> responses;
        private final boolean hasError;

        public ToolExecutionResult(List<ChatResponse> responses, boolean hasError) {
            this.responses = responses != null ? new ArrayList<>(responses) : new ArrayList<>();
            this.hasError = hasError;
        }

        public List<ChatResponse> getResponses() {
            return new ArrayList<>(responses);
        }

        public boolean hasError() {
            return hasError;
        }

        public String getTextResponse() {
            if (responses.isEmpty()) {
                return "工具执行完成";
            }

            // 合并所有ChatResponse的文本内容
            StringBuilder sb = new StringBuilder();
            for (ChatResponse response : responses) {
                if (response.getTextContent() != null && !response.getTextContent().trim().isEmpty()) {
                    if (sb.length() > 0) {
                        sb.append("\n");
                    }
                    sb.append(response.getTextContent());
                }
            }

            return sb.length() > 0 ? sb.toString() : "工具执行完成";
        }
    }
}