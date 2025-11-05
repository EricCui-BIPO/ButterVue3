package com.i0.report.application.usecases;

import com.i0.report.application.dto.input.ReportDataGenerationInput;
import com.i0.report.application.dto.output.ReportDataOutput;
import com.i0.report.domain.entities.Chart;
import com.i0.report.domain.entities.Report;
import com.i0.report.domain.exceptions.ReportDisabledException;
import com.i0.report.domain.exceptions.ReportNotFoundException;
import com.i0.report.domain.repositories.ChartRepository;
import com.i0.report.domain.repositories.ReportRepository;
import com.i0.report.domain.services.FilterMergingService;
import com.i0.report.domain.services.ReportDataCalculationService;
import com.i0.report.domain.valueobjects.Filter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 生成报表数据用例
 *
 * 重构后的UseCase专注于业务流程编排，遵循单一职责原则
 * 所有具体的业务逻辑和数据处理都已提取到Domain层服务中
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GenerateReportDataUseCase {

    private final ReportRepository reportRepository;
    private final ChartRepository chartRepository;
    private final ReportDataCalculationService reportDataCalculationService;
    private final FilterMergingService filterMergingService;

    /**
     * 执行生成报表数据
     *
     * @param input 报表数据生成输入参数
     * @return 报表数据输出
     */
    @Transactional(readOnly = true)
    public ReportDataOutput execute(ReportDataGenerationInput input) {
        log.info("开始生成报表数据: reportId={}", input.getReportId());

        // 1. 获取并验证报表配置
        Report report = getAndValidateReport(input.getReportId());

        // 2. 获取报表包含的图表
        List<Chart> charts = getReportCharts(report);

        // 3. 执行每个图表的数据查询
        List<Map<String, Object>> chartDataList = processCharts(charts, input.getAdditionalFilters());

        // 4. 构建并返回报表数据
        return buildReportDataOutput(report, chartDataList);
    }

    /**
     * 获取并验证报表配置
     */
    private Report getAndValidateReport(String reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ReportNotFoundException(reportId));

        if (!Boolean.TRUE.equals(report.getEnabled())) {
            throw new ReportDisabledException(reportId);
        }

        return report;
    }

    /**
     * 获取报表包含的图表
     */
    private List<Chart> getReportCharts(Report report) {
        return chartRepository.findByReportId(report.getId());
    }

    /**
     * 处理所有图表数据生成
     */
    private List<Map<String, Object>> processCharts(List<Chart> charts, List<Filter> additionalFilters) {
        List<Map<String, Object>> chartDataList = new ArrayList<>();

        for (Chart chart : charts) {
            if (!Boolean.TRUE.equals(chart.getEnabled())) {
                continue;
            }
            try {
                Map<String, Object> chartData = reportDataCalculationService.generateChartData(chart, additionalFilters);
                chartDataList.add(chartData);
            } catch (Exception e) {
                log.error("执行图表查询失败: chartId={}, error={}", chart.getId(), e.getMessage(), e);
                // 继续处理其他图表
            }
        }

        return chartDataList;
    }

    /**
     * 构建报表数据输出
     */
    private ReportDataOutput buildReportDataOutput(Report report, List<Map<String, Object>> chartDataList) {
        return ReportDataOutput.builder()
                .reportId(report.getId())
                .reportName(report.getName())
                .reportStatus(report.getStatus())
                .chartData(chartDataList)
                .refreshInterval(report.getRefreshInterval())
                .theme(report.getTheme())
                .layout(report.getLayout())
                .timestamp(System.currentTimeMillis())
                .build();
    }
}