package com.i0.report.gateway.persistence.repositories;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.i0.report.domain.entities.Chart;
import com.i0.report.domain.repositories.ChartRepository;
import com.i0.report.domain.valueobjects.Filter;
import com.i0.report.gateway.persistence.mappers.ChartMapper;
import com.i0.report.gateway.persistence.mappers.ReportChartMapper;
import com.i0.report.gateway.persistence.dataobjects.ChartDO;
import com.i0.report.gateway.persistence.dataobjects.ReportChartDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 图表仓储实现
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class ChartRepositoryImpl extends ServiceImpl<ChartMapper, ChartDO> implements ChartRepository {

    private final ChartMapper chartMapper;
    private final ReportChartMapper reportChartMapper;
    private final ObjectMapper objectMapper;

    @Override
    public Chart save(Chart chart) {
        ChartDO chartDO = convertToChartDO(chart);

        if (StringUtils.hasText(chart.getId())) {
            // 更新操作
            chartDO.setUpdatedAt(LocalDateTime.now());
            updateById(chartDO);
        } else {
            // 新增操作
            chartDO.setCreatedAt(LocalDateTime.now());
            chartDO.setUpdatedAt(LocalDateTime.now());
            save(chartDO);
        }

        return convertToChart(chartDO);
    }

    @Override
    public Optional<Chart> findById(String id) {
        ChartDO chartDO = getById(id);
        return Optional.ofNullable(convertToChart(chartDO));
    }

    @Override
    public List<Chart> findAll() {
        LambdaQueryWrapper<ChartDO> queryWrapper = new LambdaQueryWrapper<ChartDO>()
                .eq(ChartDO::getIsDeleted, false)
                .orderByDesc(ChartDO::getCreatedAt);

        List<ChartDO> chartDOList = list(queryWrapper);
        return chartDOList.stream()
                .map(this::convertToChart)
                .collect(Collectors.toList());
    }

    @Override
    public List<Chart> findByIndicatorId(String indicatorId) {
        LambdaQueryWrapper<ChartDO> queryWrapper = new LambdaQueryWrapper<ChartDO>()
                .eq(ChartDO::getIsDeleted, false)
                .eq(ChartDO::getIndicatorId, indicatorId)
                .orderByDesc(ChartDO::getCreatedAt);

        List<ChartDO> chartDOList = list(queryWrapper);
        return chartDOList.stream()
                .map(this::convertToChart)
                .collect(Collectors.toList());
    }

    @Override
    public List<Chart> findByType(String type) {
        LambdaQueryWrapper<ChartDO> queryWrapper = new LambdaQueryWrapper<ChartDO>()
                .eq(ChartDO::getIsDeleted, false)
                .eq(ChartDO::getType, type)
                .orderByDesc(ChartDO::getCreatedAt);

        List<ChartDO> chartDOList = list(queryWrapper);
        return chartDOList.stream()
                .map(this::convertToChart)
                .collect(Collectors.toList());
    }

    @Override
    public List<Chart> findEnabled() {
        LambdaQueryWrapper<ChartDO> queryWrapper = new LambdaQueryWrapper<ChartDO>()
                .eq(ChartDO::getIsDeleted, false)
                .eq(ChartDO::getEnabled, true)
                .orderByDesc(ChartDO::getCreatedAt);

        List<ChartDO> chartDOList = list(queryWrapper);
        return chartDOList.stream()
                .map(this::convertToChart)
                .collect(Collectors.toList());
    }

    @Override
    public List<Chart> findByName(String name) {
        LambdaQueryWrapper<ChartDO> queryWrapper = new LambdaQueryWrapper<ChartDO>()
                .eq(ChartDO::getIsDeleted, false)
                .like(StringUtils.hasText(name), ChartDO::getName, name)
                .orderByDesc(ChartDO::getCreatedAt);

        List<ChartDO> chartDOList = list(queryWrapper);
        return chartDOList.stream()
                .map(this::convertToChart)
                .collect(Collectors.toList());
    }

  
    @Override
    public List<Chart> findByFilters(List<Filter> filters) {
        // 简化实现，返回所有启用的图表
        return findEnabled();
    }

    @Override
    public Object generateChartData(Chart chart, List<Filter> additionalFilters) {
        // 简化实现，返回空Map
        return new java.util.HashMap<>();
    }

    @Override
    public java.util.Map<String, Object> generateChartDataBatch(List<Chart> charts, List<Filter> additionalFilters) {
        // 简化实现，返回空Map
        return new java.util.HashMap<>();
    }

    @Override
    public void deleteByIndicatorId(String indicatorId) {
        LambdaQueryWrapper<ChartDO> queryWrapper = new LambdaQueryWrapper<ChartDO>()
                .eq(ChartDO::getIndicatorId, indicatorId);
        remove(queryWrapper);
    }

    @Override
    public boolean validateChartConfig(Chart chart) {
        // 简化实现，返回true
        return true;
    }

    @Override
    public java.util.Map<String, Object> getStyleRecommendations(Chart chart) {
        // 简化实现，返回空Map
        return new java.util.HashMap<>();
    }

    @Override
    public List<Object> previewChartData(Chart chart, List<Filter> additionalFilters, int limit) {
        // 简化实现，返回空List
        return new ArrayList<>();
    }

    @Override
    public java.util.Map<String, Object> getPerformanceStats(String chartId) {
        // 简化实现，返回空Map
        return new java.util.HashMap<>();
    }

    @Override
    public void cacheChartData(Chart chart, List<Filter> filters, Object data, long ttl) {
        // 简化实现，空方法
    }

    @Override
    public java.util.Optional<Object> getCachedChartData(Chart chart, List<Filter> filters) {
        // 简化实现，返回空Optional
        return java.util.Optional.empty();
    }

    @Override
    public void clearCache(String chartId) {
        // 简化实现，空方法
    }

    @Override
    public void clearAllCache() {
        // 简化实现，空方法
    }

    @Override
    public String exportChartConfig(String chartId) {
        // 简化实现，返回空字符串
        return "";
    }

    @Override
    public Chart importChartConfig(String configJson) {
        // 简化实现，返回null
        return null;
    }

    @Override
    public Chart duplicateChart(String chartId, String newName) {
        // 简化实现，返回null
        return null;
    }

    @Override
    public void deleteById(String id) {
        removeById(id);
    }

    @Override
    public void delete(Chart chart) {
        removeById(chart.getId());
    }

    @Override
    public boolean existsById(String id) {
        return lambdaQuery()
                .eq(ChartDO::getId, id)
                .eq(ChartDO::getIsDeleted, false)
                .exists();
    }

    @Override
    public boolean existsByName(String name) {
        return lambdaQuery()
                .eq(ChartDO::getName, name)
                .eq(ChartDO::getIsDeleted, false)
                .exists();
    }

    @Override
    public long count() {
        return lambdaQuery()
                .eq(ChartDO::getIsDeleted, false)
                .count();
    }

    @Override
    public long countByIndicatorId(String indicatorId) {
        return lambdaQuery()
                .eq(ChartDO::getIsDeleted, false)
                .eq(ChartDO::getIndicatorId, indicatorId)
                .count();
    }

    @Override
    public long countByType(String type) {
        return lambdaQuery()
                .eq(ChartDO::getIsDeleted, false)
                .eq(ChartDO::getType, type)
                .count();
    }

    @Override
    public List<Chart> findByReportId(String reportId) {
        // 首先从report_charts表中获取chart_id列表
        LambdaQueryWrapper<ReportChartDO> reportChartQuery = new LambdaQueryWrapper<ReportChartDO>()
                .eq(ReportChartDO::getReportId, reportId)
                .eq(ReportChartDO::getIsDeleted, false);

        List<ReportChartDO> reportChartDOs = reportChartMapper.selectList(reportChartQuery);

        if (reportChartDOs.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取所有chart_id
        List<String> chartIds = reportChartDOs.stream()
                .map(ReportChartDO::getChartId)
                .collect(Collectors.toList());

        // 从charts表中获取对应的图表信息
        LambdaQueryWrapper<ChartDO> chartQuery = new LambdaQueryWrapper<ChartDO>()
                .in(ChartDO::getId, chartIds)
                .eq(ChartDO::getIsDeleted, false)
                .eq(ChartDO::getEnabled, true)
                .orderByDesc(ChartDO::getCreatedAt);

        List<ChartDO> chartDOs = list(chartQuery);

        return chartDOs.stream()
                .map(this::convertToChart)
                .collect(Collectors.toList());
    }

    @Override
    public long countEnabled() {
        return lambdaQuery()
                .eq(ChartDO::getIsDeleted, false)
                .eq(ChartDO::getEnabled, true)
                .count();
    }

    @Override
    public List<Chart> saveAll(List<Chart> charts) {
        List<ChartDO> chartDOList = charts.stream()
                .map(this::convertToChartDO)
                .collect(Collectors.toList());

        saveBatch(chartDOList);

        return chartDOList.stream()
                .map(this::convertToChart)
                .collect(Collectors.toList());
    }

    /**
     * 转换Chart为ChartDO
     */
    private ChartDO convertToChartDO(Chart chart) {
        if (chart == null) {
            return null;
        }

        ChartDO chartDO = new ChartDO();
        chartDO.setId(chart.getId());
        chartDO.setName(chart.getName());
        chartDO.setDescription(chart.getDescription());
        chartDO.setType(chart.getType());
        chartDO.setIndicatorId(chart.getIndicatorId());
        chartDO.setDimension(chart.getDimension());
        chartDO.setTitle(chart.getTitle());
        chartDO.setXAxisLabel(chart.getXAxisLabel());
        chartDO.setYAxisLabel(chart.getYAxisLabel());
        chartDO.setWidth(chart.getWidth());
        chartDO.setHeight(chart.getHeight());
        chartDO.setEnabled(chart.getEnabled());

        // 转换过滤条件为JSON
        if (chart.getFilters() != null && !chart.getFilters().isEmpty()) {
            try {
                String filtersJson = objectMapper.writeValueAsString(chart.getFilters());
                chartDO.setFilters(filtersJson);
            } catch (JsonProcessingException e) {
                log.error("转换过滤条件为JSON失败", e);
                chartDO.setFilters("[]");
            }
        }

        // 转换配置为JSON
        if (StringUtils.hasText(chart.getConfig())) {
            chartDO.setConfig(chart.getConfig());
        }

        return chartDO;
    }

    /**
     * 转换ChartDO为Chart
     */
    private Chart convertToChart(ChartDO chartDO) {
        if (chartDO == null) {
            return null;
        }

        Chart.ChartBuilder builder = Chart.builder()
                .id(chartDO.getId())
                .name(chartDO.getName())
                .description(chartDO.getDescription())
                .type(chartDO.getType())
                .indicatorId(chartDO.getIndicatorId())
                .dimension(chartDO.getDimension())
                .title(chartDO.getTitle())
                .xAxisLabel(chartDO.getXAxisLabel())
                .yAxisLabel(chartDO.getYAxisLabel())
                .width(chartDO.getWidth())
                .height(chartDO.getHeight())
                .config(chartDO.getConfig())
                .enabled(chartDO.getEnabled());

        // 转换JSON过滤条件为对象
        if (StringUtils.hasText(chartDO.getFilters())) {
            try {
                List<Filter> filters = objectMapper.readValue(chartDO.getFilters(),
                        new TypeReference<List<Filter>>() {});
                builder.filters(filters);
            } catch (JsonProcessingException e) {
                log.error("转换JSON过滤条件为对象失败", e);
                builder.filters(new ArrayList<>());
            }
        } else {
            builder.filters(new ArrayList<>());
        }

        return builder.build();
    }
}