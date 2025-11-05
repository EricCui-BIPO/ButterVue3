package com.i0.report.application.config;

import com.i0.report.domain.services.ChartDataFormattingService;
import com.i0.report.domain.services.FilterMergingService;
import com.i0.report.domain.services.ReportDataCalculationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 报表领域服务配置
 * 在Application层配置Domain服务为Spring Bean
 */
@Configuration
public class ReportDomainServiceConfig {

    @Bean
    public FilterMergingService filterMergingService() {
        return new FilterMergingService();
    }

    @Bean
    public ChartDataFormattingService chartDataFormattingService() {
        return new ChartDataFormattingService();
    }

    @Bean
    public ReportDataCalculationService reportDataCalculationService(
            com.i0.report.domain.repositories.IndicatorRepository indicatorRepository,
            com.i0.report.domain.repositories.DatasetRepository datasetRepository,
            ChartDataFormattingService chartDataFormattingService,
            FilterMergingService filterMergingService) {
        return new ReportDataCalculationService(
                indicatorRepository,
                datasetRepository,
                chartDataFormattingService,
                filterMergingService
        );
    }
}