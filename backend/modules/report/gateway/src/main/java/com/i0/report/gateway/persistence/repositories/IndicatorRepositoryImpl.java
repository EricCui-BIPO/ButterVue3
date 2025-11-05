package com.i0.report.gateway.persistence.repositories;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.i0.report.domain.entities.Indicator;
import com.i0.report.domain.repositories.IndicatorRepository;
import com.i0.report.domain.valueobjects.Filter;
import com.i0.report.gateway.persistence.mappers.IndicatorMapper;
import com.i0.report.gateway.persistence.dataobjects.IndicatorDO;
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
 * 指标仓储实现
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class IndicatorRepositoryImpl extends ServiceImpl<IndicatorMapper, IndicatorDO> implements IndicatorRepository {

    private final IndicatorMapper indicatorMapper;
    private final ObjectMapper objectMapper;

    @Override
    public Indicator save(Indicator indicator) {
        IndicatorDO indicatorDO = convertToIndicatorDO(indicator);

        if (StringUtils.hasText(indicator.getId())) {
            // 更新操作
            indicatorDO.setUpdatedAt(LocalDateTime.now());
            updateById(indicatorDO);
        } else {
            // 新增操作
            indicatorDO.setCreatedAt(LocalDateTime.now());
            indicatorDO.setUpdatedAt(LocalDateTime.now());
            save(indicatorDO);
        }

        return convertToIndicator(indicatorDO);
    }

    @Override
    public Optional<Indicator> findById(String id) {
        IndicatorDO indicatorDO = getById(id);
        return Optional.ofNullable(convertToIndicator(indicatorDO));
    }

    @Override
    public List<Indicator> findAll() {
        LambdaQueryWrapper<IndicatorDO> queryWrapper = new LambdaQueryWrapper<IndicatorDO>()
                .eq(IndicatorDO::getIsDeleted, false)
                .orderByDesc(IndicatorDO::getCreatedAt);

        List<IndicatorDO> indicatorDOList = list(queryWrapper);
        return indicatorDOList.stream()
                .map(this::convertToIndicator)
                .collect(Collectors.toList());
    }

    @Override
    public List<Indicator> findByDatasetId(String datasetId) {
        LambdaQueryWrapper<IndicatorDO> queryWrapper = new LambdaQueryWrapper<IndicatorDO>()
                .eq(IndicatorDO::getIsDeleted, false)
                .eq(IndicatorDO::getDatasetId, datasetId)
                .orderByDesc(IndicatorDO::getCreatedAt);

        List<IndicatorDO> indicatorDOList = list(queryWrapper);
        return indicatorDOList.stream()
                .map(this::convertToIndicator)
                .collect(Collectors.toList());
    }

    @Override
    public List<Indicator> findByType(String type) {
        LambdaQueryWrapper<IndicatorDO> queryWrapper = new LambdaQueryWrapper<IndicatorDO>()
                .eq(IndicatorDO::getIsDeleted, false)
                .eq(IndicatorDO::getType, type)
                .orderByDesc(IndicatorDO::getCreatedAt);

        List<IndicatorDO> indicatorDOList = list(queryWrapper);
        return indicatorDOList.stream()
                .map(this::convertToIndicator)
                .collect(Collectors.toList());
    }

    @Override
    public List<Indicator> findEnabled() {
        LambdaQueryWrapper<IndicatorDO> queryWrapper = new LambdaQueryWrapper<IndicatorDO>()
                .eq(IndicatorDO::getIsDeleted, false)
                .eq(IndicatorDO::getEnabled, true)
                .orderByDesc(IndicatorDO::getCreatedAt);

        List<IndicatorDO> indicatorDOList = list(queryWrapper);
        return indicatorDOList.stream()
                .map(this::convertToIndicator)
                .collect(Collectors.toList());
    }

    @Override
    public List<Indicator> findByName(String name) {
        LambdaQueryWrapper<IndicatorDO> queryWrapper = new LambdaQueryWrapper<IndicatorDO>()
                .eq(IndicatorDO::getIsDeleted, false)
                .like(StringUtils.hasText(name), IndicatorDO::getName, name)
                .orderByDesc(IndicatorDO::getCreatedAt);

        List<IndicatorDO> indicatorDOList = list(queryWrapper);
        return indicatorDOList.stream()
                .map(this::convertToIndicator)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        removeById(id);
    }

    @Override
    public void delete(Indicator indicator) {
        removeById(indicator.getId());
    }

    @Override
    public boolean existsById(String id) {
        return lambdaQuery()
                .eq(IndicatorDO::getId, id)
                .eq(IndicatorDO::getIsDeleted, false)
                .exists();
    }

    @Override
    public boolean existsByName(String name) {
        return lambdaQuery()
                .eq(IndicatorDO::getName, name)
                .eq(IndicatorDO::getIsDeleted, false)
                .exists();
    }

    @Override
    public long count() {
        return lambdaQuery()
                .eq(IndicatorDO::getIsDeleted, false)
                .count();
    }

    @Override
    public long countByDatasetId(String datasetId) {
        return lambdaQuery()
                .eq(IndicatorDO::getIsDeleted, false)
                .eq(IndicatorDO::getDatasetId, datasetId)
                .count();
    }

    @Override
    public long countEnabled() {
        return lambdaQuery()
                .eq(IndicatorDO::getIsDeleted, false)
                .eq(IndicatorDO::getEnabled, true)
                .count();
    }

    @Override
    public List<Indicator> saveAll(List<Indicator> indicators) {
        List<IndicatorDO> indicatorDOList = indicators.stream()
                .map(this::convertToIndicatorDO)
                .collect(Collectors.toList());

        saveBatch(indicatorDOList);

        return indicatorDOList.stream()
                .map(this::convertToIndicator)
                .collect(Collectors.toList());
    }

    @Override
    public void clearCache(String indicatorId) {
        // 简化实现，空方法
    }

    @Override
    public void clearAllCache() {
        // 简化实现，空方法
    }

    @Override
    public java.util.Optional<Object> getCachedCalculationResult(Indicator indicator, List<Filter> filters) {
        // 简化实现，返回空Optional
        return java.util.Optional.empty();
    }

    @Override
    public void cacheCalculationResult(Indicator indicator, List<Filter> filters, Object result, long ttl) {
        // 简化实现，只记录日志
        log.info("缓存指标计算结果: indicator={}, ttl={}s", indicator.getName(), ttl);
    }

    // 添加其他缺少的方法
    @Override
    public void deleteByDatasetId(String datasetId) {
        lambdaUpdate()
                .eq(IndicatorDO::getDatasetId, datasetId)
                .set(IndicatorDO::getIsDeleted, true)
                .update();
    }

    @Override
    public long countByType(String type) {
        return lambdaQuery()
                .eq(IndicatorDO::getIsDeleted, false)
                .eq(IndicatorDO::getType, type)
                .count();
    }

    @Override
    public List<Indicator> findByFilters(List<Filter> filters) {
        // 简化实现，返回所有启用指标
        return findEnabled();
    }

    @Override
    public Object calculateIndicator(Indicator indicator, List<Filter> additionalFilters) {
        // 简化实现，返回基本指标信息
        return java.util.Map.of(
                "indicatorId", indicator.getId(),
                "indicatorName", indicator.getName(),
                "calculation", indicator.getCalculation(),
                "timestamp", java.time.LocalDateTime.now()
        );
    }

    @Override
    public java.util.Map<String, Object> calculateIndicators(
            List<Indicator> indicators,
            List<Filter> additionalFilters) {
        // 简化实现，返回空映射
        return new java.util.HashMap<>();
    }

    @Override
    public boolean validateCalculation(String calculation, String datasetId) {
        return true; // 简化实现
    }

    @Override
    public List<String> getAvailableDimensions(Indicator indicator) {
        // 简化实现，返回空列表
        return new ArrayList<>();
    }

    @Override
    public List<Object> previewIndicator(
            Indicator indicator,
            List<Filter> additionalFilters,
            int limit) {
        // 简化实现，返回空列表
        return new ArrayList<>();
    }

    @Override
    public java.util.Map<String, Object> getPerformanceStats(String indicatorId) {
        // 简化实现，返回空映射
        return new java.util.HashMap<>();
    }

    /**
     * 转换Indicator为IndicatorDO
     */
    private IndicatorDO convertToIndicatorDO(Indicator indicator) {
        if (indicator == null) {
            return null;
        }

        IndicatorDO indicatorDO = new IndicatorDO();
        indicatorDO.setId(indicator.getId());
        indicatorDO.setName(indicator.getName());
        indicatorDO.setDescription(indicator.getDescription());
        indicatorDO.setDatasetId(indicator.getDatasetId());
        indicatorDO.setCalculation(indicator.getCalculation());
        indicatorDO.setType(indicator.getType());
        indicatorDO.setUnit(indicator.getUnit());
        indicatorDO.setFormatPattern(indicator.getFormatPattern());
        indicatorDO.setEnabled(indicator.getEnabled());

        // 转换维度字段为JSON
        if (indicator.getDimensions() != null && !indicator.getDimensions().isEmpty()) {
            try {
                String dimensionsJson = objectMapper.writeValueAsString(indicator.getDimensions());
                indicatorDO.setDimensions(dimensionsJson);
            } catch (JsonProcessingException e) {
                log.error("转换维度字段为JSON失败", e);
                indicatorDO.setDimensions("[]");
            }
        }

        // 转换过滤条件为JSON
        if (indicator.getFilters() != null && !indicator.getFilters().isEmpty()) {
            try {
                String filtersJson = objectMapper.writeValueAsString(indicator.getFilters());
                indicatorDO.setFilters(filtersJson);
            } catch (JsonProcessingException e) {
                log.error("转换过滤条件为JSON失败", e);
                indicatorDO.setFilters("[]");
            }
        }

        return indicatorDO;
    }

    /**
     * 转换IndicatorDO为Indicator
     */
    private Indicator convertToIndicator(IndicatorDO indicatorDO) {
        if (indicatorDO == null) {
            return null;
        }

        Indicator.IndicatorBuilder builder = Indicator.builder()
                .id(indicatorDO.getId())
                .name(indicatorDO.getName())
                .description(indicatorDO.getDescription())
                .datasetId(indicatorDO.getDatasetId())
                .calculation(indicatorDO.getCalculation())
                .type(indicatorDO.getType())
                .unit(indicatorDO.getUnit())
                .formatPattern(indicatorDO.getFormatPattern())
                .enabled(indicatorDO.getEnabled());

        // 转换JSON维度字段为对象
        if (StringUtils.hasText(indicatorDO.getDimensions())) {
            try {
                List<String> dimensions = objectMapper.readValue(indicatorDO.getDimensions(),
                        new TypeReference<List<String>>() {});
                builder.dimensions(dimensions);
            } catch (JsonProcessingException e) {
                log.error("转换JSON维度字段为对象失败", e);
                builder.dimensions(new ArrayList<>());
            }
        } else {
            builder.dimensions(new ArrayList<>());
        }

        // 转换JSON过滤条件为对象
        if (StringUtils.hasText(indicatorDO.getFilters())) {
            try {
                List<Filter> filters = objectMapper.readValue(indicatorDO.getFilters(),
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