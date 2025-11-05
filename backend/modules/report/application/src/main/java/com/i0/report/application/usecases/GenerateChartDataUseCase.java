package com.i0.report.application.usecases;

import com.i0.report.domain.entities.Chart;
import com.i0.report.domain.repositories.ChartRepository;
import com.i0.report.domain.valueobjects.Filter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 生成图表数据用例
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GenerateChartDataUseCase {

    private final ChartRepository chartRepository;

    /**
     * 执行生成图表数据
     *
     * @param chartId 图表ID
     * @param filters 过滤条件
     * @return 图表数据
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> execute(String chartId, List<Filter> filters) {
        log.info("开始生成图表数据: chartId={}", chartId);

        // 1. 获取图表配置
        Chart chart = chartRepository.findById(chartId)
                .orElseThrow(() -> new RuntimeException("图表不存在: " + chartId));

        if (!chart.getEnabled()) {
            throw new RuntimeException("图表已禁用: " + chartId);
        }

        // 2. 通过Repository生成图表数据
        Object chartData = chartRepository.generateChartData(chart, filters);

        // 3. 处理返回值，确保返回Map类型
        if (chartData instanceof Map) {
            return (Map<String, Object>) chartData;
        } else {
            // 如果返回的不是Map，包装成Map格式
            Map<String, Object> result = new HashMap<>();
            result.put("chartId", chartId);
            result.put("chartName", chart.getName());
            result.put("chartType", chart.getType());
            result.put("chartTitle", chart.getTitle());
            result.put("data", chartData);
            return result;
        }
    }
}