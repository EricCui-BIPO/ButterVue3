package com.i0.agents.application.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.i0.agents.domain.entities.ChatMessage;
import com.i0.agents.domain.services.AIConfigurationService;
import com.i0.agents.domain.services.mcp.MCPTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * AI请求处理服务
 * 职责：构建AI请求，调用AI服务，解析响应
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AIRequestService {

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;
    private final AIConfigurationService configurationService;

    /**
     * 发送AI请求并获取响应
     *
     * @param sessionId      会话ID
     * @param messages       消息历史
     * @param availableTools 可用工具列表
     * @return AI响应结果
     */
    public AIResponseResult sendRequest(String sessionId, List<ChatMessage> messages, List<MCPTool> availableTools) {
        try {
            // 检查测试环境
            String activeProfile = System.getProperty("spring.profiles.active", "");
            if ("test".equals(activeProfile)) {
                log.info("Running in test environment, returning mock response");
                return AIResponseResult.success("这是AI的模拟回复。");
            }

            // 获取当前活跃厂商的配置
            String apiUrl = configurationService.getApiUrl();
            String apiKey = configurationService.getApiKey();

            log.debug("Using AI provider with API URL: {}", apiUrl);

            // 构建请求
            String requestBody = buildRequestBody(messages, availableTools);
            log.debug("Sending AI request for session: {}, requestBody::{}", sessionId, requestBody);

            Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(requestBody, MediaType.get("application/json")))
                .build();


            // 同步调用
            try (Response response = client.newCall(request).execute()) {
                String responseBody = response.body() != null ? response.body().string() : "";

                if (!response.isSuccessful()) {
                    log.warn("AI service returned error for session: {}, status: {}", sessionId, response.code());
                    return AIResponseResult.error("AI服务响应异常，请稍后再试。");
                }

                // 解析响应
                AIResponseResult result = parseResponse(responseBody);
                log.debug("AI response received for session: {}, hasToolCalls: {}", sessionId, result.hasToolCalls());
                return result;

            }

        } catch (Exception e) {
            log.error("Failed to send AI request for session: {}", sessionId, e);
            return AIResponseResult.error("请求创建失败，请稍后再试。");
        }
    }

    /**
     * 异步发送AI请求
     *
     * @param sessionId      会话ID
     * @param messages       消息历史
     * @param availableTools 可用工具列表
     * @return AI响应结果的CompletableFuture
     */
    public CompletableFuture<AIResponseResult> sendRequestAsync(String sessionId, List<ChatMessage> messages, List<MCPTool> availableTools) {
        return CompletableFuture.supplyAsync(() -> sendRequest(sessionId, messages, availableTools));
    }

    /**
     * 构建AI请求体
     */
    private String buildRequestBody(List<ChatMessage> messages, List<MCPTool> availableTools) throws Exception {
        Map<String, Object> requestBody = new HashMap<>();

        // 获取当前活跃厂商的默认模型
        String model = configurationService.getDefaultModel();
        requestBody.put("model", model);
        requestBody.put("stream", false);

        log.debug("Using model: {}", model);

        // 转换消息格式
        List<Map<String, Object>> messageList = new ArrayList<>();
        for (ChatMessage message : messages) {
            // 只包含用户和助手消息，不包含函数消息
            if (message.getRole() != com.i0.agents.domain.enums.MessageRole.FUNCTION) {
                Map<String, Object> msg = new HashMap<>();
                msg.put("role", convertRoleToString(message.getRole()));
                msg.put("content", message.getContent().getContent());
                messageList.add(msg);
            }
        }
        requestBody.put("messages", messageList);

        // 添加工具定义（如果有可用的工具）
        List<Map<String, Object>> tools = buildToolDefinitions(availableTools);
        if (!tools.isEmpty()) {
            requestBody.put("tools", tools);
            requestBody.put("tool_choice", "auto");
        }

        return objectMapper.writeValueAsString(requestBody);
    }

    /**
     * 构建工具定义
     */
    private List<Map<String, Object>> buildToolDefinitions(List<MCPTool> availableTools) {
        List<Map<String, Object>> tools = new ArrayList<>();

        if (availableTools != null && !availableTools.isEmpty()) {
            for (MCPTool mcpTool : availableTools) {
                Map<String, Object> tool = new HashMap<>();
                tool.put("type", "function");

                Map<String, Object> functionDef = new HashMap<>();
                functionDef.put("name", mcpTool.getName());
                functionDef.put("description", mcpTool.getDescription());
                functionDef.put("parameters", mcpTool.getParameters());

                tool.put("function", functionDef);
                tools.add(tool);

                log.debug("Added MCP tool: {} from server: {}", mcpTool.getName(), mcpTool.getSourceServer());
            }
        }

        log.debug("Total tools available: {}", tools.size());
        return tools;
    }

    /**
     * 转换消息角色
     */
    private String convertRoleToString(com.i0.agents.domain.enums.MessageRole role) {
        switch (role) {
            case USER:
                return "user";
            case ASSISTANT:
                return "assistant";
            case SYSTEM:
                return "system";
            default:
                return "user";
        }
    }

    /**
     * 解析AI响应
     */
    private AIResponseResult parseResponse(String responseBody) throws Exception {
        JsonNode jsonResponse = objectMapper.readTree(responseBody);
        JsonNode choices = jsonResponse.get("choices");

        if (choices != null && choices.isArray() && !choices.isEmpty()) {
            JsonNode firstChoice = choices.get(0);
            JsonNode message = firstChoice.get("message");

            if (message != null) {
                String content = message.get("content") != null ? message.get("content").asText() : "";

                // 检查工具调用
                List<ToolCallInfo> toolCalls = new ArrayList<>();
                JsonNode toolCallsNode = message.get("tool_calls");
                if (toolCallsNode != null && toolCallsNode.isArray()) {
                    for (JsonNode toolCall : toolCallsNode) {
                        String functionName = toolCall.path("function").path("name").asText();
                        String arguments = toolCall.path("function").path("arguments").asText();
                        toolCalls.add(new ToolCallInfo(functionName, arguments));
                    }
                }

                return new AIResponseResult(content, toolCalls, true, null);
            }
        }

        return new AIResponseResult("抱歉，我无法理解您的请求。", Collections.emptyList(), true, null);
    }

    /**
     * AI响应结果
     */
    public static class AIResponseResult {
        private final String content;
        private final List<ToolCallInfo> toolCalls;
        private final boolean success;
        private final String error;

        private AIResponseResult(String content, List<ToolCallInfo> toolCalls, boolean success, String error) {
            this.content = content;
            this.toolCalls = toolCalls != null ? toolCalls : Collections.emptyList();
            this.success = success;
            this.error = error;
        }

        public static AIResponseResult success(String content) {
            return new AIResponseResult(content, Collections.emptyList(), true, null);
        }

        public static AIResponseResult success(String content, List<ToolCallInfo> toolCalls) {
            return new AIResponseResult(content, toolCalls, true, null);
        }

        public static AIResponseResult error(String error) {
            return new AIResponseResult(null, Collections.emptyList(), false, error);
        }

        public String getContent() {
            return content;
        }

        public List<ToolCallInfo> getToolCalls() {
            return toolCalls;
        }

        public boolean hasToolCalls() {
            return !toolCalls.isEmpty();
        }

        public boolean isSuccess() {
            return success;
        }

        public String getError() {
            return error;
        }
    }

    /**
     * 工具调用信息
     */
    public static class ToolCallInfo {
        private final String functionName;
        private final String arguments;

        public ToolCallInfo(String functionName, String arguments) {
            this.functionName = functionName;
            this.arguments = arguments;
        }

        public String getFunctionName() {
            return functionName;
        }

        public String getArguments() {
            return arguments;
        }
    }
}