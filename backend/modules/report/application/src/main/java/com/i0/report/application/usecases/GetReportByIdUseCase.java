package com.i0.report.application.usecases;

import com.i0.report.application.dto.output.DatasetOutput;
import com.i0.report.domain.entities.Report;
import com.i0.report.domain.repositories.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 根据ID获取报表用例
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GetReportByIdUseCase {

    private final ReportRepository reportRepository;

    /**
     * 执行获取报表操作
     *
     * @param reportId 报表ID
     * @return 报表数据
     */
    public Object execute(String reportId) {
        log.info("开始获取报表: {}", reportId);

        // 查找报表
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("报表不存在: " + reportId));

        // 验证报表状态
        if (!report.getEnabled()) {
            throw new IllegalStateException("报表已禁用: " + reportId);
        }

        log.info("成功获取报表: ID={}, 名称={}", report.getId(), report.getName());

        // 生成报表数据
        return reportRepository.generateReportData(report, null);
    }

    /**
     * 执行获取报表操作（带过滤条件）
     *
     * @param reportId 报表ID
     * @param filters 额外的过滤条件
     * @return 报表数据
     */
    public Object execute(String reportId, List<com.i0.report.domain.valueobjects.Filter> filters) {
        log.info("开始获取报表: {}, 带过滤条件", reportId);

        // 查找报表
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("报表不存在: " + reportId));

        // 验证报表状态
        if (!report.getEnabled()) {
            throw new IllegalStateException("报表已禁用: " + reportId);
        }

        log.info("成功获取报表: ID={}, 名称={}", report.getId(), report.getName());

        // 生成报表数据
        return reportRepository.generateReportData(report, filters);
    }
}