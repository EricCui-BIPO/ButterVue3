package com.i0.agents.application.dto.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 快速提示词输出DTO
 * 用于在聊天界面提供给用户的预设提示词选项
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuickPromptOutput {
    /**
     * 提示词ID
     */
    private String id;

    /**
     * 提示词标题（显示给用户的简短描述）
     */
    private String title;

    /**
     * 提示词内容（用户点击后发送的实际内容）
     */
    private String content;

    /**
     * 提示词描述（更详细的说明，可选）
     */
    private String description;

    /**
     * 提示词分类（如：entity_management, data_query等）
     */
    private String category;

    /**
     * 图标（可选，用于UI展示）
     */
    private String icon;

    /**
     * 排序权重（数值越小越靠前）
     */
    private Integer order;
}