package com.i0.report.domain.repositories;

import com.i0.report.domain.entities.Indicator;
import com.i0.report.domain.valueobjects.Filter;

import java.util.List;
import java.util.Optional;

/**
 * 指标仓储接口
 * 定义指标相关的持久化操作
 */
public interface IndicatorRepository {

    /**
     * 保存指标
     *
     * @param indicator 指标实体
     * @return 保存后的指标
     */
    Indicator save(Indicator indicator);

    /**
     * 根据ID查找指标
     *
     * @param id 指标ID
     * @return 指标实体
     */
    Optional<Indicator> findById(String id);

    /**
     * 查找所有指标
     *
     * @return 指标列表
     */
    List<Indicator> findAll();

    /**
     * 根据名称查找指标
     *
     * @param name 指标名称
     * @return 指标列表
     */
    List<Indicator> findByName(String name);

    /**
     * 根据数据集ID查找指标
     *
     * @param datasetId 数据集ID
     * @return 指标列表
     */
    List<Indicator> findByDatasetId(String datasetId);

    /**
     * 根据指标类型查找指标
     *
     * @param type 指标类型
     * @return 指标列表
     */
    List<Indicator> findByType(String type);

    /**
     * 查找启用的指标
     *
     * @return 启用的指标列表
     */
    List<Indicator> findEnabled();

    /**
     * 根据过滤条件查找指标
     *
     * @param filters 过滤条件列表
     * @return 符合条件的指标列表
     */
    List<Indicator> findByFilters(List<Filter> filters);

    /**
     * 计算指标数据
     *
     * @param indicator 指标实体
     * @param additionalFilters 额外的过滤条件
     * @return 计算结果
     */
    Object calculateIndicator(Indicator indicator, List<Filter> additionalFilters);

    /**
     * 批量计算指标数据
     *
     * @param indicators 指标列表
     * @param additionalFilters 额外的过滤条件
     * @return 计算结果映射
     */
    java.util.Map<String, Object> calculateIndicators(
        List<Indicator> indicators,
        List<Filter> additionalFilters
    );

    /**
     * 删除指标
     *
     * @param id 指标ID
     */
    void deleteById(String id);

    /**
     * 删除指标
     *
     * @param indicator 指标实体
     */
    void delete(Indicator indicator);

    /**
     * 根据数据集ID删除所有相关指标
     *
     * @param datasetId 数据集ID
     */
    void deleteByDatasetId(String datasetId);

    /**
     * 检查指标是否存在
     *
     * @param id 指标ID
     * @return 是否存在
     */
    boolean existsById(String id);

    /**
     * 根据名称检查指标是否存在
     *
     * @param name 指标名称
     * @return 是否存在
     */
    boolean existsByName(String name);

    /**
     * 统计指标数量
     *
     * @return 指标总数
     */
    long count();

    /**
     * 统计启用的指标数量
     *
     * @return 启用的指标总数
     */
    long countEnabled();

    /**
     * 根据数据集ID统计指标数量
     *
     * @param datasetId 数据集ID
     * @return 指标数量
     */
    long countByDatasetId(String datasetId);

    /**
     * 根据指标类型统计指标数量
     *
     * @param type 指标类型
     * @return 指标数量
     */
    long countByType(String type);

    /**
     * 批量保存指标
     *
     * @param indicators 指标列表
     * @return 保存后的指标列表
     */
    List<Indicator> saveAll(List<Indicator> indicators);

    /**
     * 验证指标计算表达式是否有效
     *
     * @param calculation 计算表达式
     * @param datasetId 数据集ID
     * @return 是否有效
     */
    boolean validateCalculation(String calculation, String datasetId);

    /**
     * 获取指标的可选维度列表
     *
     * @param indicator 指标实体
     * @return 维度列表
     */
    List<String> getAvailableDimensions(Indicator indicator);

    /**
     * 预览指标计算结果
     *
     * @param indicator 指标实体
     * @param additionalFilters 额外的过滤条件
     * @param limit 限制返回条数
     * @return 预览结果
     */
    List<Object> previewIndicator(
        Indicator indicator,
        List<Filter> additionalFilters,
        int limit
    );

    /**
     * 获取指标的性能统计信息
     *
     * @param indicatorId 指标ID
     * @return 性能统计
     */
    java.util.Map<String, Object> getPerformanceStats(String indicatorId);

    /**
     * 缓存指标计算结果
     *
     * @param indicator 指标实体
     * @param filters 过滤条件
     * @param result 计算结果
     * @param ttl 缓存时间（秒）
     */
    void cacheCalculationResult(
        Indicator indicator,
        List<Filter> filters,
        Object result,
        long ttl
    );

    /**
     * 获取缓存的指标计算结果
     *
     * @param indicator 指标实体
     * @param filters 过滤条件
     * @return 缓存的结果
     */
    Optional<Object> getCachedCalculationResult(
        Indicator indicator,
        List<Filter> filters
    );

    /**
     * 清除指标缓存
     *
     * @param indicatorId 指标ID
     */
    void clearCache(String indicatorId);

    /**
     * 清除所有指标缓存
     */
    void clearAllCache();
}