package com.i0.report.gateway.controllers;

import com.i0.report.application.dto.input.CreateDatasetInput;
import com.i0.report.application.dto.input.ReportDataGenerationInput;
import com.i0.report.application.dto.output.DatasetOutput;
import com.i0.report.application.dto.output.ReportDataOutput;
import com.i0.report.application.dto.output.ReportOutput;
import com.i0.report.application.usecases.CreateDatasetUseCase;
import com.i0.report.application.usecases.FindAllReportsUseCase;
import com.i0.report.application.usecases.GenerateChartDataUseCase;
import com.i0.report.application.usecases.GenerateReportDataUseCase;
import com.i0.report.application.usecases.GetReportByIdUseCase;
import com.i0.report.domain.valueobjects.Filter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Collections;

/**
 * 报表控制器
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ReportController {

    private final CreateDatasetUseCase createDatasetUseCase;
    private final FindAllReportsUseCase findAllReportsUseCase;
    private final GetReportByIdUseCase getReportByIdUseCase;
    private final GenerateReportDataUseCase generateReportDataUseCase;
    private final GenerateChartDataUseCase generateChartDataUseCase;

    /**
     * 创建数据集
     */
    @PostMapping("/datasets")
    public DatasetOutput createDataset(@Valid @RequestBody CreateDatasetInput input) {
        log.info("创建数据集请求: {}", input.getName());
        return createDatasetUseCase.execute(input);
    }

    /**
     * 获取报表配置信息
     */
    @GetMapping("/{reportId}/info")
    public Object getReportInfo(@PathVariable String reportId) {
        log.info("获取报表配置请求: {}", reportId);
        return getReportByIdUseCase.execute(reportId);
    }

    /**
     * 获取报表数据
     */
    @PostMapping("/{reportId}/data")
    public ReportDataOutput getReportData(@PathVariable String reportId,
                                        @RequestBody(required = false) List<Filter> filters) {
        log.info("获取报表数据请求: {}", reportId);

        ReportDataGenerationInput input = ReportDataGenerationInput.builder()
                .reportId(reportId)
                .additionalFilters(filters != null ? filters : Collections.emptyList())
                .build();

        return generateReportDataUseCase.execute(input);
    }

    /**
     * 获取单个图表数据
     */
    @GetMapping("/charts/{chartId}/data")
    public Object getChartData(@PathVariable String chartId,
                             @RequestBody(required = false) List<Filter> filters) {
        log.info("获取图表数据请求: {}", chartId);
        return generateChartDataUseCase.execute(chartId, filters);
    }

    /**
     * 获取所有报表列表
     */
    @GetMapping
    public List<ReportOutput> getAllReports() {
        log.info("获取所有报表列表请求");

        return findAllReportsUseCase.execute();
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public String health() {
        return "Report service is running";
    }
}