package com.i0.agents.application.usecases;

import com.i0.agents.application.dto.output.QuickPromptOutput;
import com.i0.agents.domain.services.BusinessFunctionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 获取快速提示词UseCase
 * 基于已注册的业务函数生成用户友好的快速提示词
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GetQuickPromptsUseCase {

    private final BusinessFunctionRegistry businessFunctionRegistry;

    /**
     * 获取快速提示词列表
     *
     * @return 快速提示词列表
     */
    public List<QuickPromptOutput> execute() {
        log.debug("Getting quick prompts based on registered business functions");

        return businessFunctionRegistry.getAllFunctions().stream()
                .map(this::convertToQuickPrompt)
                .collect(Collectors.toList());
    }

    /**
     * 将业务函数转换为快速提示词
     *
     * @param function 业务函数
     * @return 快速提示词
     */
    private QuickPromptOutput convertToQuickPrompt(com.i0.agents.domain.valueobjects.BusinessFunction function) {
        return QuickPromptOutput.builder()
                .id(function.getName())
                .title(generateTitle(function.getName(), function.getDescription()))
                .content(generatePromptContent(function))
                .description(function.getDescription())
                .category(determineCategory(function.getName()))
                .icon(determineIcon(function.getName()))
                .order(determineOrder(function.getName()))
                .build();
    }

    /**
     * 生成提示词标题
     */
    private String generateTitle(String functionName, String description) {
        switch (functionName) {
            case "create_entity":
                return "创建业务实体";
            case "find_entity":
                return "查询实体信息";
            case "create_employee":
                return "创建员工";
            case "find_employee":
                return "查询员工信息";
            default:
                return description.length() > 20 ? description.substring(0, 17) + "..." : description;
        }
    }

    /**
     * 生成提示词内容
     */
    private String generatePromptContent(com.i0.agents.domain.valueobjects.BusinessFunction function) {
        switch (function.getName()) {
            case "create_entity":
                return "我想创建一个新的业务实体（如：内部实体、客户实体、供应商实体），请协助我完成实体信息的录入。";
            case "find_entity":
                return "我需要查询某个业务实体的详细信息";
            case "create_employee":
                return "我要为新员工办理入职，需要创建员工档案，请协助我填写员工的基本信息、工作地点等信息。注意：工号和邮箱必须全局唯一。";
            case "find_employee":
                return "我需要查询某位员工的详细信息，包括入职情况、员工状态等，请根据员工的姓名、工号或邮箱帮我查询。";
            default:
                return String.format("请帮我%s。", function.getDescription());
        }
    }

    /**
     * 确定提示词分类
     */
    private String determineCategory(String functionName) {
        if (functionName.contains("create") || functionName.contains("add")) {
            return "entity_management";
        } else if (functionName.contains("find") || functionName.contains("search") || functionName.contains("query")) {
            return "data_query";
        } else if (functionName.contains("update") || functionName.contains("modify")) {
            return "entity_update";
        } else if (functionName.contains("delete") || functionName.contains("remove")) {
            return "entity_delete";
        }
        return "general";
    }

    /**
     * 确定图标
     */
    private String determineIcon(String functionName) {
        switch (functionName) {
            case "create_entity":
                return "plus-circle";
            case "find_entity":
                return "search";
            case "create_employee":
                return "user-plus";
            case "find_employee":
                return "user-search";
            default:
                return "help-circle";
        }
    }

    /**
     * 确定排序权重
     */
    private Integer determineOrder(String functionName) {
        switch (functionName) {
            case "create_entity":
                return 1;
            case "find_entity":
                return 2;
            case "create_employee":
                return 3;
            case "find_employee":
                return 4;
            default:
                return 100;
        }
    }
}