package com.i0.report.application.usecases;

import com.i0.report.application.dto.output.ReportOutput;
import com.i0.report.domain.repositories.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 查找所有报表用例
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FindAllReportsUseCase {

    private final ReportRepository reportRepository;

    /**
     * 执行查找所有报表操作
     *
     * @return 报表列表
     */
    public List<ReportOutput> execute() {
        log.info("开始查找所有报表列表");

        // 查找所有报表
        List<com.i0.report.domain.entities.Report> reports = reportRepository.findAll();

        // 转换为输出DTO
        List<ReportOutput> reportOutputs = ReportOutput.from(reports);

        log.info("成功查找报表列表，共{}个报表", reportOutputs.size());

        return reportOutputs;
    }
}