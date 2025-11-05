package com.i0.report.application.usecases;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.i0.report.application.dto.input.ReportDataGenerationInput;
import com.i0.report.application.dto.output.ReportDataOutput;
import com.i0.report.domain.entities.Chart;
import com.i0.report.domain.entities.Report;
import com.i0.report.domain.repositories.ChartRepository;
import com.i0.report.domain.repositories.ReportRepository;
import com.i0.report.domain.services.FilterMergingService;
import com.i0.report.domain.services.ReportDataCalculationService;
import com.i0.report.domain.valueobjects.Filter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * GenerateReportDataUseCase 测试类
 * 重构后的测试专注于验证业务流程编排
 */
@ExtendWith(MockitoExtension.class)
class GenerateReportDataUseCaseTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private ChartRepository chartRepository;

    @Mock
    private ReportDataCalculationService reportDataCalculationService;

    @Mock
    private FilterMergingService filterMergingService;

    private GenerateReportDataUseCase generateReportDataUseCase;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        generateReportDataUseCase = new GenerateReportDataUseCase(
            reportRepository, chartRepository, reportDataCalculationService, filterMergingService
        );
    }

    @Test
    void testExecute_ShouldGenerateReportDataSuccessfully() {
        // Given - 准备测试数据
        String reportId = "test-report-123";

        // 模拟报表
        Report mockReport = Report.builder()
                .id(reportId)
                .name("测试报表")
                .status("published")
                .enabled(true)
                .refreshInterval(30)
                .theme("light")
                .layout("{}")
                .build();

        // 模拟图表
        Chart mockChart = Chart.builder()
                .id("chart-1")
                .name("测试图表")
                .type("bar")
                .indicatorId("indicator-1")
                .dimension("department")
                .enabled(true)
                .build();

        // 模拟图表数据
        Map<String, Object> mockChartData = new HashMap<>();
        mockChartData.put("chartId", "chart-1");
        mockChartData.put("chartName", "测试图表");
        mockChartData.put("data", Map.of(
                "type", "xy",
                "xAxis", List.of("Engineering", "Design", "HR"),
                "yAxis", List.of(17, 5, 3)
        ));

        // 设置Mock返回值
        when(reportRepository.findById(reportId)).thenReturn(java.util.Optional.of(mockReport));
        when(chartRepository.findByReportId(reportId)).thenReturn(List.of(mockChart));
        when(reportDataCalculationService.generateChartData(any(Chart.class), any()))
                .thenReturn(mockChartData);

        ReportDataGenerationInput input = ReportDataGenerationInput.builder()
                .reportId(reportId)
                .additionalFilters(Collections.emptyList())
                .build();

        // When - 执行测试
        ReportDataOutput result = generateReportDataUseCase.execute(input);

        // Then - 验证结果
        assertNotNull(result);
        assertEquals(reportId, result.getReportId());
        assertEquals("测试报表", result.getReportName());
        assertEquals("published", result.getReportStatus());
        assertEquals(Integer.valueOf(30), result.getRefreshInterval());
        assertEquals("light", result.getTheme());
        assertEquals("{}", result.getLayout());
        assertNotNull(result.getChartData());
        assertEquals(1, result.getChartData().size());
        assertTrue(result.getTimestamp() > 0);

        // 验证图表数据
        Map<String, Object> chartData = result.getChartData().get(0);
        assertEquals("chart-1", chartData.get("chartId"));
        assertEquals("测试图表", chartData.get("chartName"));
        assertNotNull(chartData.get("data"));

        System.out.println("生成的报表数据：");
        try {
            System.out.println(objectMapper.writeValueAsString(result));
        } catch (Exception e) {
            System.out.println("JSON序列化失败: " + e.getMessage());
        }
    }

    @Test
    void testExecute_ShouldHandleEmptyChartsList() {
        // Given - 准备没有图表的报表
        String reportId = "empty-report-123";

        Report mockReport = Report.builder()
                .id(reportId)
                .name("空报表")
                .status("published")
                .enabled(true)
                .build();

        when(reportRepository.findById(reportId)).thenReturn(java.util.Optional.of(mockReport));
        when(chartRepository.findByReportId(reportId)).thenReturn(Collections.emptyList());

        ReportDataGenerationInput input = ReportDataGenerationInput.builder()
                .reportId(reportId)
                .additionalFilters(Collections.emptyList())
                .build();

        // When
        ReportDataOutput result = generateReportDataUseCase.execute(input);

        // Then
        assertNotNull(result);
        assertEquals(reportId, result.getReportId());
        assertEquals("空报表", result.getReportName());
        assertNotNull(result.getChartData());
        assertTrue(result.getChartData().isEmpty());
    }

    @Test
    void testExecute_ShouldIncludeAdditionalFilters() {
        // Given
        String reportId = "test-report-456";

        Report mockReport = Report.builder()
                .id(reportId)
                .name("过滤器测试报表")
                .enabled(true)
                .build();

        Chart mockChart = Chart.builder()
                .id("chart-2")
                .enabled(true)
                .build();

        Map<String, Object> mockChartData = new HashMap<>();
        mockChartData.put("chartId", "chart-2");

        Filter additionalFilter = Filter.builder()
                .field("status")
                .value("active")
                .operator("eq")
                .build();
        List<Filter> additionalFilters = List.of(additionalFilter);

        when(reportRepository.findById(reportId)).thenReturn(java.util.Optional.of(mockReport));
        when(chartRepository.findByReportId(reportId)).thenReturn(List.of(mockChart));
        when(reportDataCalculationService.generateChartData(any(Chart.class), any()))
                .thenReturn(mockChartData);

        ReportDataGenerationInput input = ReportDataGenerationInput.builder()
                .reportId(reportId)
                .additionalFilters(additionalFilters)
                .build();

        // When
        ReportDataOutput result = generateReportDataUseCase.execute(input);

        // Then
        assertNotNull(result);
        assertEquals(reportId, result.getReportId());

        // 验证服务被调用时传入了正确的过滤条件
        // 这里可以验证FilterMergingService是否被正确调用
    }
}