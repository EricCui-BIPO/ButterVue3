package com.i0.app.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.i0.agents.application.dto.output.QuickPromptOutput;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Chat快速提示词集成测试
 * 验证快速提示词API的完整功能
 */
class ChatQuickPromptsIntegrationTest extends BasicIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("获取快速提示词列表 - 成功")
    void should_GetQuickPrompts_When_ApiCalled() throws Exception {
        // When - 调用快速提示词API
        MvcResult result = mockMvc.perform(get("/api/v1/chat/quick-prompts"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andReturn();

        // Then - 验证返回数据结构
        String responseContent = result.getResponse().getContentAsString();
        QuickPromptOutput[] quickPrompts = objectMapper.readValue(
                objectMapper.readTree(responseContent).get("data").toString(),
                QuickPromptOutput[].class
        );

        List<QuickPromptOutput> promptList = Arrays.asList(quickPrompts);

        // 验证至少包含基础的实体相关提示词
        assertThat(promptList).isNotEmpty();

        // 验证包含预期的业务函数ID
        List<String> promptIds = promptList.stream()
                .map(QuickPromptOutput::getId)
                .collect(Collectors.toList());

        assertThat(promptIds).contains("create_entity", "find_entity");

        // 验证提示词基本结构
        promptList.forEach(prompt -> {
            assertThat(prompt.getId()).isNotBlank();
            assertThat(prompt.getTitle()).isNotBlank();
            assertThat(prompt.getContent()).isNotBlank();
            assertThat(prompt.getCategory()).isNotBlank();
            assertThat(prompt.getIcon()).isNotBlank();
            assertThat(prompt.getOrder()).isNotNull();
        });

        // 验证每个提示词都有排序权重
        assertThat(promptList).extracting(QuickPromptOutput::getOrder)
                .allMatch(order -> order != null && order > 0);
    }

    @Test
    @DisplayName("快速提示词响应格式验证")
    void should_ReturnCorrectResponseFormat_When_GetQuickPrompts() throws Exception {
        // When - 调用快速提示词API
        mockMvc.perform(get("/api/v1/chat/quick-prompts"))

                // Then - 验证响应格式符合API规范
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))

                // 验证统一响应包装结构
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.timestamp").exists())

                // 验证数据为数组且不为空
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(4)) // 基于当前注册的4个业务函数

                // 验证每个提示词对象的结构
                .andExpect(jsonPath("$.data[0].id").exists())
                .andExpect(jsonPath("$.data[0].title").exists())
                .andExpect(jsonPath("$.data[0].content").exists())
                .andExpect(jsonPath("$.data[0].description").exists())
                .andExpect(jsonPath("$.data[0].category").exists())
                .andExpect(jsonPath("$.data[0].icon").exists())
                .andExpect(jsonPath("$.data[0].order").exists());
    }

    @Test
    @DisplayName("业务函数注册验证 - 确保提示词基于真实业务函数生成")
    void should_GeneratePromptsBasedOnBusinessFunctions() throws Exception {
        // When - 调用快速提示词API
        MvcResult result = mockMvc.perform(get("/api/v1/chat/quick-prompts"))
                .andExpect(status().isOk())
                .andReturn();

        // Then - 验证提示词内容与业务函数注册情况一致
        String responseContent = result.getResponse().getContentAsString();
        QuickPromptOutput[] quickPrompts = objectMapper.readValue(
                objectMapper.readTree(responseContent).get("data").toString(),
                QuickPromptOutput[].class
        );

        List<String> promptIds = Arrays.stream(quickPrompts)
                .map(QuickPromptOutput::getId)
                .collect(Collectors.toList());

        // 验证包含所有已注册的业务函数ID
        assertThat(promptIds).contains("create_entity", "find_entity", "create_employee", "find_employee");

        // 验证提示词数量与注册的业务函数数量一致
        assertThat(promptIds).hasSize(4);
    }

    @Test
    @DisplayName("快速提示词分类和图标验证")
    void should_HaveCorrectCategoriesAndIcons() throws Exception {
        // When - 调用快速提示词API
        MvcResult result = mockMvc.perform(get("/api/v1/chat/quick-prompts"))
                .andExpect(status().isOk())
                .andReturn();

        // Then - 验证分类和图标的正确性
        String responseContent = result.getResponse().getContentAsString();
        QuickPromptOutput[] quickPrompts = objectMapper.readValue(
                objectMapper.readTree(responseContent).get("data").toString(),
                QuickPromptOutput[].class
        );

        Arrays.stream(quickPrompts).forEach(prompt -> {
            // 验证分类不为空
            assertThat(prompt.getCategory()).isNotBlank();

            // 验证图标不为空
            assertThat(prompt.getIcon()).isNotBlank();

            // 验证排序权重不为空且为正数
            assertThat(prompt.getOrder()).isNotNull();
            assertThat(prompt.getOrder()).isPositive();
        });
    }
}