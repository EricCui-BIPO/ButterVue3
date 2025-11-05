package com.i0.agents.application.dto.output;

import com.i0.agents.domain.services.mcp.MCPToolResult;
import com.i0.agents.domain.valueobjects.BusinessFunction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI聊天响应DTO
 * 支持包含文本内容、UI组件和附加数据的复杂响应结构
 */
@Slf4j
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {

    /**
     * 文本内容（用于显示的回复文本）
     */
    private String textContent;

    /**
     * UI组件信息（用于前端渲染业务组件）
     */
    private Object uiComponent;

    /**
     * 附加数据（包含其他结构化信息）
     */
    private Map<String, Object> data;

    /**
     * 向后兼容：转换为文本响应
     * 当只需要文本内容时使用此方法
     */
    public String toTextResponse() {
        if (textContent != null && !textContent.trim().isEmpty()) {
            return textContent;
        }
        return generateDefaultText();
    }

    /**
     * 生成默认文本内容
     */
    private String generateDefaultText() {
        if (uiComponent != null) {
            return "我已经为您执行了相关操作，请在界面上查看详细信息。";
        }
        return "处理完成，如需进一步帮助请继续告诉我。";
    }

    /**
     * 从纯文本创建ChatResponse
     */
    public static ChatResponse fromText(String text) {
        return ChatResponse.builder()
                .textContent(text)
                .build();
    }

    /**
     * 从MCP工具结果创建ChatResponse
     */
    public static ChatResponse fromMCPResult(MCPToolResult mcpResult) {
        if (mcpResult == null) {
            return fromText("工具执行结果为空。");
        }

        ChatResponseBuilder builder = ChatResponse.builder()
                .textContent(mcpResult.getResult());

        // 检查是否有UI组件信息
        if (hasUIComponent(mcpResult)) {
            Object uiComponent = extractUIComponent(mcpResult);
            builder.uiComponent(uiComponent);
            log.debug("Extracted UI component from MCP result: {}", mcpResult.getToolName());
        }

        // 包含数据信息
        if (mcpResult.getData() != null && !mcpResult.getData().isEmpty()) {
            Map<String, Object> data = new HashMap<>();
            data.put("toolName", mcpResult.getToolName());
            data.put("serverName", mcpResult.getServerName());
            data.put("toolData", mcpResult.getData());
            builder.data(data);
        }

        return builder.build();
    }

    /**
     * 从业务函数结果创建ChatResponse
     */
    public static ChatResponse fromFunctionResult(BusinessFunction.FunctionCallResult functionResult) {
        if (functionResult == null) {
            return fromText("函数执行结果为空。");
        }

        ChatResponseBuilder builder = ChatResponse.builder()
                .textContent(functionResult.getResult());

        // 检查是否有UI组件信息
        if (hasUIComponent(functionResult)) {
            Object uiComponent = extractUIComponent(functionResult);
            builder.uiComponent(uiComponent);
            log.debug("Extracted UI component from function result");
        }

        // 包含数据信息
        if (functionResult.getData() != null && !functionResult.getData().isEmpty()) {
            Map<String, Object> data = new HashMap<>();
            data.put("functionData", functionResult.getData());
            builder.data(data);
        }

        return builder.build();
    }

    /**
     * 合并多个ChatResponse
     */
    public static ChatResponse merge(List<ChatResponse> responses) {
        if (responses == null || responses.isEmpty()) {
            return fromText("没有可用的响应结果。");
        }

        if (responses.size() == 1) {
            return responses.get(0);
        }

        List<String> textContents = new ArrayList<>();
        List<Object> uiComponents = new ArrayList<>();
        Map<String, Object> allData = new HashMap<>();

        for (ChatResponse response : responses) {
            if (response.getTextContent() != null && !response.getTextContent().trim().isEmpty()) {
                textContents.add(response.getTextContent());
            }
            if (response.getUiComponent() != null) {
                uiComponents.add(response.getUiComponent());
            }
            if (response.getData() != null) {
                allData.putAll(response.getData());
            }
        }

        return ChatResponse.builder()
                .textContent(String.join("\n", textContents))
                .uiComponent(uiComponents.isEmpty() ? null : uiComponents)
                .data(allData.isEmpty() ? null : allData)
                .build();
    }

    /**
     * 检查MCP工具结果是否包含UI组件信息
     */
    private static boolean hasUIComponent(MCPToolResult mcpResult) {
        return mcpResult != null && mcpResult.getUiComponent() != null;
    }

    /**
     * 检查业务函数结果是否包含UI组件信息
     */
    private static boolean hasUIComponent(BusinessFunction.FunctionCallResult functionResult) {
        return functionResult != null && functionResult.getUiComponent() != null;
    }

    /**
     * 从MCP工具结果中提取UI组件信息
     */
    private static Object extractUIComponent(MCPToolResult mcpResult) {
        return mcpResult != null ? mcpResult.getUiComponent() : null;
    }

    /**
     * 从业务函数结果中提取UI组件信息
     */
    private static Object extractUIComponent(BusinessFunction.FunctionCallResult functionResult) {
        return functionResult != null ? functionResult.getUiComponent() : null;
    }

    /**
     * 检查响应是否包含UI组件
     */
    public boolean hasUIComponent() {
        return uiComponent != null;
    }

    /**
     * 检查响应是否包含附加数据
     */
    public boolean hasData() {
        return data != null && !data.isEmpty();
    }
}