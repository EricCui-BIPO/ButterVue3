package com.i0.report.domain.repositories;

import com.i0.report.domain.entities.Chart;
import com.i0.report.domain.valueobjects.Filter;

import java.util.List;
import java.util.Optional;

/**
 * 图表仓储接口
 * 定义图表相关的持久化操作
 */
public interface ChartRepository {

    /**
     * 保存图表
     *
     * @param chart 图表实体
     * @return 保存后的图表
     */
    Chart save(Chart chart);

    /**
     * 根据ID查找图表
     *
     * @param id 图表ID
     * @return 图表实体
     */
    Optional<Chart> findById(String id);

    /**
     * 查找所有图表
     *
     * @return 图表列表
     */
    List<Chart> findAll();

    /**
     * 根据名称查找图表
     *
     * @param name 图表名称
     * @return 图表列表
     */
    List<Chart> findByName(String name);

    /**
     * 根据指标ID查找图表
     *
     * @param indicatorId 指标ID
     * @return 图表列表
     */
    List<Chart> findByIndicatorId(String indicatorId);

    /**
     * 根据图表类型查找图表
     *
     * @param type 图表类型
     * @return 图表列表
     */
    List<Chart> findByType(String type);

    /**
     * 查找启用的图表
     *
     * @return 启用的图表列表
     */
    List<Chart> findEnabled();

    /**
     * 根据过滤条件查找图表
     *
     * @param filters 过滤条件列表
     * @return 符合条件的图表列表
     */
    List<Chart> findByFilters(List<Filter> filters);

    /**
     * 生成图表数据
     *
     * @param chart 图表实体
     * @param additionalFilters 额外的过滤条件
     * @return 图表数据
     */
    Object generateChartData(Chart chart, List<Filter> additionalFilters);

    /**
     * 批量生成图表数据
     *
     * @param charts 图表列表
     * @param additionalFilters 额外的过滤条件
     * @return 图表数据映射
     */
    java.util.Map<String, Object> generateChartDataBatch(
        List<Chart> charts,
        List<Filter> additionalFilters
    );

    /**
     * 删除图表
     *
     * @param id 图表ID
     */
    void deleteById(String id);

    /**
     * 删除图表
     *
     * @param chart 图表实体
     */
    void delete(Chart chart);

    /**
     * 根据指标ID删除所有相关图表
     *
     * @param indicatorId 指标ID
     */
    void deleteByIndicatorId(String indicatorId);

    /**
     * 检查图表是否存在
     *
     * @param id 图表ID
     * @return 是否存在
     */
    boolean existsById(String id);

    /**
     * 根据名称检查图表是否存在
     *
     * @param name 图表名称
     * @return 是否存在
     */
    boolean existsByName(String name);

    /**
     * 统计图表数量
     *
     * @return 图表总数
     */
    long count();

    /**
     * 统计启用的图表数量
     *
     * @return 启用的图表总数
     */
    long countEnabled();

    /**
     * 根据指标ID统计图表数量
     *
     * @param indicatorId 指标ID
     * @return 图表数量
     */
    long countByIndicatorId(String indicatorId);

    /**
     * 根据图表类型统计图表数量
     *
     * @param type 图表类型
     * @return 图表数量
     */
    long countByType(String type);

    /**
     * 根据报表ID查找图表
     *
     * @param reportId 报表ID
     * @return 图表列表
     */
    List<Chart> findByReportId(String reportId);

    /**
     * 批量保存图表
     *
     * @param charts 图表列表
     * @return 保存后的图表列表
     */
    List<Chart> saveAll(List<Chart> charts);

    /**
     * 验证图表配置是否有效
     *
     * @param chart 图表实体
     * @return 是否有效
     */
    boolean validateChartConfig(Chart chart);

    /**
     * 获取图表的样式建议
     *
     * @param chart 图表实体
     * @return 样式建议
     */
    java.util.Map<String, Object> getStyleRecommendations(Chart chart);

    /**
     * 预览图表数据
     *
     * @param chart 图表实体
     * @param additionalFilters 额外的过滤条件
     * @param limit 限制返回条数
     * @return 预览结果
     */
    List<Object> previewChartData(
        Chart chart,
        List<Filter> additionalFilters,
        int limit
    );

    /**
     * 获取图表的性能统计信息
     *
     * @param chartId 图表ID
     * @return 性能统计
     */
    java.util.Map<String, Object> getPerformanceStats(String chartId);

    /**
     * 缓存图表数据
     *
     * @param chart 图表实体
     * @param filters 过滤条件
     * @param data 图表数据
     * @param ttl 缓存时间（秒）
     */
    void cacheChartData(
        Chart chart,
        List<Filter> filters,
        Object data,
        long ttl
    );

    /**
     * 获取缓存的图表数据
     *
     * @param chart 图表实体
     * @param filters 过滤条件
     * @return 缓存的数据
     */
    Optional<Object> getCachedChartData(
        Chart chart,
        List<Filter> filters
    );

    /**
     * 清除图表缓存
     *
     * @param chartId 图表ID
     */
    void clearCache(String chartId);

    /**
     * 清除所有图表缓存
     */
    void clearAllCache();

    /**
     * 导出图表配置
     *
     * @param chartId 图表ID
     * @return 图表配置JSON
     */
    String exportChartConfig(String chartId);

    /**
     * 导入图表配置
     *
     * @param configJson 图表配置JSON
     * @return 导入后的图表
     */
    Chart importChartConfig(String configJson);

    /**
     * 复制图表
     *
     * @param chartId 源图表ID
     * @param newName 新图表名称
     * @return 复制后的图表
     */
    Chart duplicateChart(String chartId, String newName);
}