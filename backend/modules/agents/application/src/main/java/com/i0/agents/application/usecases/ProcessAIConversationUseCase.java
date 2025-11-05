package com.i0.agents.application.usecases;

import com.i0.agents.application.dto.output.ConversationResult;
import com.i0.agents.application.services.AIRequestService;
import com.i0.agents.application.services.ToolExecutionService;
import com.i0.agents.application.services.UIEventService;
import com.i0.agents.application.services.mcp.MCPProtocolManager;
import com.i0.agents.domain.entities.ChatMessage;
import com.i0.agents.domain.services.AIConfigurationService;
import com.i0.agents.domain.services.mcp.MCPTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * AI对话处理UseCase
 * 职责：处理AI对话业务流程，协调各个服务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProcessAIConversationUseCase {

    private final AIRequestService aiRequestService;
    private final ToolExecutionService toolExecutionService;
    private final UIEventService uiEventService;
    private final AIConfigurationService configurationService;
    private final MCPProtocolManager mcpProtocolManager;

    /**
     * 执行AI对话处理
     *
     * @param input 对话输入参数
     * @return AI对话结果
     */
    public ConversationResult execute(ProcessAIConversationInput input) {
        return execute(input, null);
    }

    /**
     * 执行AI对话处理（支持SSE事件推送）
     *
     * @param input   对话输入参数
     * @param emitter SSE发射器，用于推送UI组件事件（可为null）
     * @return AI对话结果
     */
    public ConversationResult execute(ProcessAIConversationInput input, SseEmitter emitter) {
        try {
            // 1. 获取可用工具列表
            List<MCPTool> availableTools = getAvailableTools();

            // 2. 发送AI请求
            AIRequestService.AIResponseResult aiResult = aiRequestService.sendRequest(input.getSessionId(), input.getMessages(), availableTools);

            if (!aiResult.isSuccess()) {
                log.warn("AI request failed for session: {}, error: {}", input.getSessionId(), aiResult.getError());
                return ConversationResult.error(aiResult.getError());
            }

            // 3. 检查是否有工具调用
            if (aiResult.hasToolCalls()) {
                // 4. 执行工具调用
                ToolExecutionService.ToolExecutionResult executionResult = toolExecutionService
                    .executeTools(aiResult.getToolCalls(), input.getSessionId(), emitter)
                    .join();

                if (executionResult.hasError()) {
                    log.warn("Tool execution had errors for session: {}", input.getSessionId());
                }

                // 5. 返回工具执行结果（转换为ConversationResult）
                ConversationResult result = ConversationResult.fromToolExecutionResult(executionResult);
                log.debug("Tool execution completed for session: {}, UI components: {}", input.getSessionId(), result.getUIComponentCount());
                return result;
            } else {
                // 6. 返回AI文本响应
                log.debug("AI response received for session: {}, content: {}",
                    input.getSessionId(), aiResult.getContent().length() > 100 ?
                        aiResult.getContent().substring(0, 100) + "..." : aiResult.getContent());
                return ConversationResult.fromText(aiResult.getContent());
            }
        } catch (Exception e) {
            log.error("Error processing AI conversation for session: {}", input.getSessionId(), e);
            return ConversationResult.error("对话处理失败，请稍后再试。");
        }
    }

    /**
     * 异步执行AI对话处理（支持SSE事件推送）
     *
     * @param input   对话输入参数
     * @param emitter SSE发射器，用于推送UI组件事件（可为null）
     * @return AI对话结果的CompletableFuture
     */
    public CompletableFuture<ConversationResult> executeAsync(ProcessAIConversationInput input, SseEmitter emitter) {
        return CompletableFuture.supplyAsync(() -> execute(input, emitter));
    }

    /**
     * 获取可用工具列表
     */
    private List<MCPTool> getAvailableTools() {
        try {
            // 检查是否启用MCP协议
            if (!configurationService.isEnableMCPProtocol()) {
                log.debug("MCP protocol is disabled, no tools available");
                return List.of();
            }

            // 从MCP协议管理器获取所有可用工具
            List<MCPTool> availableTools = mcpProtocolManager.getAllAvailableTools();

            log.debug("Retrieved {} available tools from MCP servers", availableTools.size());

            // 记录可用工具的详细信息（调试级别）
            if (log.isDebugEnabled()) {
                availableTools.forEach(tool ->
                    log.debug("Available tool: {} from server: {}",
                        tool.getName(), tool.getSourceServer())
                );
            }

            return availableTools;

        } catch (Exception e) {
            log.error("Failed to retrieve available tools from MCP servers", e);
            return List.of();
        }
    }

    /**
     * 获取备用响应
     */
    private String getFallbackResponse(String defaultMessage) {
        return String.format("🤖 %s\n\n💡 如需帮助，请尝试重新提问或稍后再试。", defaultMessage);
    }

    /**
     * AI对话输入参数
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ProcessAIConversationInput {
        /**
         * 会话ID
         */
        private String sessionId;

        /**
         * 消息历史
         */
        private List<ChatMessage> messages;
    }
}