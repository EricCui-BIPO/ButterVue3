package com.i0.report.domain.services;

import com.i0.report.domain.entities.Chart;
import com.i0.report.domain.entities.Dataset;
import com.i0.report.domain.entities.Indicator;
import com.i0.report.domain.exceptions.DatasetNotFoundException;
import com.i0.report.domain.exceptions.IndicatorNotFoundException;
import com.i0.report.domain.repositories.DatasetRepository;
import com.i0.report.domain.repositories.IndicatorRepository;
import com.i0.report.domain.valueobjects.Filter;
import com.i0.report.domain.valueobjects.ChartType;
import com.i0.report.domain.valueobjects.IndicatorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 报表数据计算服务
 * 负责处理单个图表的数据生成逻辑
 */
@RequiredArgsConstructor
@Slf4j
public class ReportDataCalculationService {

    private final IndicatorRepository indicatorRepository;
    private final DatasetRepository datasetRepository;
    private final ChartDataFormattingService chartDataFormattingService;
    private final FilterMergingService filterMergingService;

    /**
     * 生成单个图表数据
     *
     * @param chart 图表配置
     * @param globalFilters 全局过滤条件
     * @return 图表数据响应
     */
    public Map<String, Object> generateChartData(Chart chart, List<Filter> globalFilters) {
        // 输入验证
        if (!chart.isValid()) {
            throw new IllegalArgumentException("图表配置无效");
        }

        log.info("生成图表数据: chartId={}, chartType={}", chart.getId(), chart.getType());

        // 1. 获取并验证图表绑定的指标
        Indicator indicator = getAndValidateIndicator(chart.getIndicatorId());

        // 2. 获取并验证指标绑定的数据集
        Dataset dataset = getAndValidateDataset(indicator.getDatasetId());

        // 3. 验证图表和指标的兼容性
        validateChartIndicatorCompatibility(chart, indicator);

        // 4. 构建包含指标计算的完整SQL
        String enhancedSql = buildEnhancedSql(dataset, indicator, chart);

        // 5. 合并所有过滤条件（数据集过滤 + 指标过滤 + 图表过滤 + 全局过滤）
        List<Filter> allFilters = filterMergingService.mergeAllFilters(
                dataset.getFilters(),
                indicator.getFilters(),
                chart.getFilters(),
                globalFilters
        );

        // 6. 执行增强的数据查询（已包含指标计算）
        List<Object> calculatedData = datasetRepository.executeQueryWithSql(enhancedSql, allFilters);

        // 7. 根据图表类型格式化数据
        Map<String, Object> formattedData = chartDataFormattingService.formatDataByChartType(chart, indicator, calculatedData);

        // 8. 构建完整的图表数据响应
        return buildChartResponse(chart, indicator, formattedData);
    }

    /**
     * 获取并验证指标
     */
    private Indicator getAndValidateIndicator(String indicatorId) {
        if (indicatorId == null || indicatorId.trim().isEmpty()) {
            throw new IllegalArgumentException("图表指标ID不能为空");
        }

        Indicator indicator = indicatorRepository.findById(indicatorId)
                .orElseThrow(() -> new IndicatorNotFoundException(indicatorId));

        if (!indicator.isValid()) {
            throw new IllegalStateException("指标配置无效: " + indicatorId);
        }

        return indicator;
    }

    /**
     * 获取并验证数据集
     */
    private Dataset getAndValidateDataset(String datasetId) {
        if (datasetId == null || datasetId.trim().isEmpty()) {
            throw new IllegalArgumentException("指标数据集ID不能为空");
        }

        Dataset dataset = datasetRepository.findById(datasetId)
                .orElseThrow(() -> new DatasetNotFoundException(datasetId));

        if (!dataset.isValid()) {
            throw new IllegalStateException("数据集配置无效: " + datasetId);
        }

        return dataset;
    }

    /**
     * 验证图表和指标的兼容性
     */
    private void validateChartIndicatorCompatibility(Chart chart, Indicator indicator) {
        // 验证图表类型和指标类型的兼容性
        ChartType chartType = ChartType.fromCode(chart.getType());

        // 某些图表类型需要特定的指标类型
        if (chartType.needsDimension() && (chart.getDimension() == null || chart.getDimension().trim().isEmpty())) {
            throw new IllegalArgumentException("图表类型 " + chartType.getDescription() + " 需要指定维度字段");
        }

        // 验证指标是否适用于图表类型
        if (chartType.isMetric() && !indicator.isAggregateIndicator() && !indicator.isCountIndicator()) {
            throw new IllegalArgumentException("指标卡图表需要聚合或计数类型的指标");
        }
    }

    /**
     * 构建图表数据响应
     */
    private Map<String, Object> buildChartResponse(Chart chart, Indicator indicator, Map<String, Object> formattedData) {
        Map<String, Object> response = new HashMap<>();
        response.put("chartId", chart.getId());
        response.put("chartName", chart.getName());
        response.put("chartType", chart.getType());
        response.put("chartTitle", chart.getTitle());
        response.put("dimension", chart.getDimension());
        response.put("indicatorName", indicator.getName());
        response.put("indicatorCalculation", indicator.getCalculation());
        response.put("config", parseConfig(chart.getConfig()));
        response.put("data", formattedData);

        // 添加图表类型特定的元数据
        ChartType chartType = ChartType.fromCode(chart.getType());
        response.put("needsXAxis", chartType.needsXAxis());
        response.put("needsYAxis", chartType.needsYAxis());
        response.put("supportsMultiSeries", chartType.supportsMultiSeries());

        return response;
    }

    /**
     * 构建包含指标计算的增强SQL
     */
    private String buildEnhancedSql(Dataset dataset, Indicator indicator, Chart chart) {
        String baseSql = dataset.getSql();
        String indicatorCalculation = indicator.getCalculation();

        // 如果指标的calculation已经是完整的SQL表达式，则将其包装为子查询
        if (isComplexSqlExpression(indicatorCalculation)) {
            // 检查基础SQL是否已经包含维度字段的计算
            if (datasetSqlContainsDimension(baseSql, chart.getDimension())) {
                // 如果数据集SQL已经包含了维度字段的计算，构建更简单的SQL
                String enhancedSql = String.format(
                    "SELECT %s as calculated_value, %s as dimension_field FROM (%s) base_data",
                    indicatorCalculation,
                    chart.getDimension() != null ? chart.getDimension() : "'N/A'",
                    baseSql
                );

                log.debug("构建增强SQL（已包含维度）: {}", enhancedSql);
                return enhancedSql;
            } else {
                // 构建包含指标计算的SQL，但不要给基础SQL添加不存在的字段
                String enhancedSql = String.format(
                    "SELECT %s as calculated_value, %s as dimension_field FROM (%s) base_data %s",
                    indicatorCalculation,
                    chart.getDimension() != null ? chart.getDimension() : "'N/A'",
                    baseSql,
                    chart.getDimension() != null && !baseSql.toUpperCase().contains("GROUP BY") ?
                        "GROUP BY " + chart.getDimension() : ""
                );

                log.debug("构建增强SQL（需要GROUP BY）: {}", enhancedSql);
                return enhancedSql;
            }
        } else {
            // 对于简单计算类型，使用原始SQL
            log.debug("使用原始SQL: {}", baseSql);
            return baseSql;
        }
    }

    /**
     * 检查数据集SQL是否包含指定的维度字段
     */
    private boolean datasetSqlContainsDimension(String sql, String dimension) {
        if (dimension == null || dimension.trim().isEmpty()) {
            return false;
        }

        String upperSql = sql.toUpperCase();
        String upperDimension = dimension.toUpperCase();

        // 检查SQL中是否SELECT了该维度字段
        // 支持以下情况：
        // 1. SELECT field_name ...
        // 2. SELECT field_name AS alias ...
        // 3. SELECT calculation AS field_name ...

        return upperSql.contains("SELECT " + upperDimension) ||
               upperSql.contains("SELECT " + upperDimension + " AS") ||
               upperSql.contains(" AS " + upperDimension) ||
               upperSql.contains(", " + upperDimension) ||
               upperSql.contains("," + upperDimension + " AS");
    }

    /**
     * 判断是否为复杂SQL表达式
     */
    private boolean isComplexSqlExpression(String calculation) {
        if (calculation == null || calculation.trim().isEmpty()) {
            return false;
        }

        // 如果是标准指标类型，则认为是简单表达式
        if (IndicatorType.isValid(calculation)) {
            IndicatorType indicatorType = IndicatorType.fromCode(calculation);
            return indicatorType.isAggregateIndicator() || indicatorType.isStatisticalIndicator();
        }

        String upperCalc = calculation.toUpperCase();
        // 检查是否包含SQL函数或复杂表达式
        return upperCalc.contains("CASE WHEN") ||
               upperCalc.contains("COUNT(") ||
               upperCalc.contains("SUM(") ||
               upperCalc.contains("AVG(") ||
               upperCalc.contains("MAX(") ||
               upperCalc.contains("MIN(") ||
               upperCalc.contains("DATE_FORMAT") ||
               upperCalc.contains("DATE_SUB") ||
               upperCalc.contains("GROUP BY") ||
               calculation.contains("(") && calculation.contains(")");
    }

    /**
     * 解析配置JSON
     */
    private Map<String, Object> parseConfig(String configJson) {
        if (configJson == null || configJson.trim().isEmpty()) {
            return new HashMap<>();
        }

        try {
            // 简单的JSON解析，实际项目中应该使用ObjectMapper
            // 这里为了保持简洁，返回空Map
            return new HashMap<>();
        } catch (Exception e) {
            log.error("解析图表配置失败: {}", e.getMessage());
            return new HashMap<>();
        }
    }
}