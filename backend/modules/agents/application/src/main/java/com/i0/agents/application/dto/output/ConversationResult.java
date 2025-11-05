package com.i0.agents.application.dto.output;

import com.i0.agents.application.services.ToolExecutionService;
import com.i0.agents.domain.valueobjects.UIComponentReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * AI对话结果DTO
 * 包含AI响应文本和UI组件信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationResult {

    /**
     * AI响应文本内容
     */
    private String response;

    /**
     * UI组件引用列表
     */
    private List<UIComponentReference> uiComponents;

    /**
     * 从简单文本创建ConversationResult
     */
    public static ConversationResult fromText(String response) {
        return ConversationResult.builder()
            .response(response)
            .uiComponents(new ArrayList<>())
            .build();
    }

    /**
     * 从ToolExecutionService.ToolExecutionResult创建ConversationResult
     */
    public static ConversationResult fromToolExecutionResult(ToolExecutionService.ToolExecutionResult toolResult) {
        if (toolResult == null) {
            return ConversationResult.fromText("工具执行结果为空");
        }

        List<UIComponentReference> uiComponents = new ArrayList<>();

        // 从所有ChatResponse中提取UI组件信息
        for (ChatResponse chatResponse : toolResult.getResponses()) {
            if (chatResponse != null && chatResponse.hasUIComponent()) {
                UIComponentReference componentRef = UIComponentReference.of(
                    extractComponentType(chatResponse),
                    chatResponse.getData()
                );
                if (componentRef != null && componentRef.validStatus()) {
                    uiComponents.add(componentRef);
                }
            }
        }

        return ConversationResult.builder()
            .response(toolResult.getTextResponse())
            .uiComponents(uiComponents)
            .build();
    }

    /**
     * 创建错误结果
     */
    public static ConversationResult error(String errorMessage) {
        return ConversationResult.builder()
            .response("抱歉，处理您的请求时遇到了问题。")
            .uiComponents(Collections.emptyList())
            .build();
    }

    /**
     * 检查是否有UI组件
     */
    public boolean hasUIComponents() {
        return uiComponents != null && !uiComponents.isEmpty();
    }

    /**
     * 获取UI组件数量
     */
    public int getUIComponentCount() {
        return uiComponents != null ? uiComponents.size() : 0;
    }

    /**
     * 从ChatResponse中提取组件类型
     */
    private static String extractComponentType(ChatResponse chatResponse) {
        if (chatResponse.getUiComponent() == null) {
            return "unknown";
        }

        // 如果uiComponent是Map类型，尝试提取type字段
        if (chatResponse.getUiComponent() instanceof java.util.Map) {
            java.util.Map<?, ?> componentMap = (java.util.Map<?, ?>) chatResponse.getUiComponent();
            Object typeValue = componentMap.get("type");
            if (typeValue != null) {
                return typeValue.toString();
            }
        } else if (chatResponse.getUiComponent() instanceof String) {
            return chatResponse.getUiComponent().toString();
        }

        return "custom";
    }
}