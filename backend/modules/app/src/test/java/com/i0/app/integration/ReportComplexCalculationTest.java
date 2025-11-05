package com.i0.app.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 报表复杂计算集成测试
 * 验证复杂SQL表达式的指标计算功能
 */
@SpringBootTest
@Transactional
@ActiveProfiles("test")
class ReportComplexCalculationTest extends BasicIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("验证复杂SQL表达式计算功能")
    void should_ComplexSqlCalculation_Work() throws Exception {
        // When - 调用报表数据生成接口
        mockMvc.perform(post("/api/reports/report-employee-overview/data")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.reportId").value("report-employee-overview"))
                .andExpect(jsonPath("$.data.chartData").isArray())
                .andExpect(jsonPath("$.data.chartData").isNotEmpty())
                // 验证图表数据包含计算结果
                .andExpect(jsonPath("$.data.chartData[*].data").exists())
                .andExpect(jsonPath("$.data.chartData[*].data.type").exists())
                // 验证指标计算字段存在
                .andExpect(jsonPath("$.data.chartData[*].indicatorCalculation").exists());
    }

    @Test
    @DisplayName("验证复杂SQL表达式返回正确格式的数据")
    void should_ComplexSqlCalculation_ReturnCorrectFormat() throws Exception {
        // When - 调用报表数据生成接口
        mockMvc.perform(post("/api/reports/report-employee-overview/data")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.chartData").isArray())
                // 验证返回的数据结构符合直接SQL计算结果的格式
                .andExpect(jsonPath("$.data.chartData[*].data.type").exists())
                .andExpect(jsonPath("$.data.chartData[*].data.data").exists());
    }

    @Test
    @DisplayName("验证本月新入职指标计算")
    void should_MonthlyNewHiresCalculation_Work() throws Exception {
        // When - 调用报表数据生成接口
        mockMvc.perform(post("/api/reports/report-employee-overview/data")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                // 验证包含本月新入职相关指标的图表数据
                .andExpect(jsonPath("$.data.chartData[*].indicatorCalculation").exists());
    }
}