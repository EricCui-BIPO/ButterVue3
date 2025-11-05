package com.i0.report.domain.repositories;

import com.i0.report.domain.entities.Report;
import com.i0.report.domain.valueobjects.Filter;

import java.util.List;
import java.util.Optional;

/**
 * 报表仓储接口
 * 定义报表相关的持久化操作
 */
public interface ReportRepository {

    /**
     * 保存报表
     *
     * @param report 报表实体
     * @return 保存后的报表
     */
    Report save(Report report);

    /**
     * 根据ID查找报表
     *
     * @param id 报表ID
     * @return 报表实体
     */
    Optional<Report> findById(String id);

    /**
     * 查找所有报表
     *
     * @return 报表列表
     */
    List<Report> findAll();

    /**
     * 根据名称查找报表
     *
     * @param name 报表名称
     * @return 报表列表
     */
    List<Report> findByName(String name);

    /**
     * 根据状态查找报表
     *
     * @param status 报表状态
     * @return 报表列表
     */
    List<Report> findByStatus(String status);

    /**
     * 根据分类查找报表
     *
     * @param category 报表分类
     * @return 报表列表
     */
    List<Report> findByCategory(String category);

    /**
     * 根据创建者查找报表
     *
     * @param creator 创建者
     * @return 报表列表
     */
    List<Report> findByCreator(String creator);

    /**
     * 查找启用的报表
     *
     * @return 启用的报表列表
     */
    List<Report> findEnabled();

    /**
     * 查找公开的报表
     *
     * @return 公开的报表列表
     */
    List<Report> findPublic();

    /**
     * 根据过滤条件查找报表
     *
     * @param filters 过滤条件列表
     * @return 符合条件的报表列表
     */
    List<Report> findByFilters(List<Filter> filters);

    /**
     * 分页查找报表
     *
     * @param filters 过滤条件列表
     * @param page 页码（从0开始）
     * @param size 页面大小
     * @return 分页报表列表
     */
    com.i0.domain.core.pagination.Pageable<Report> findWithPagination(
        List<Filter> filters,
        int page,
        int size
    );

    /**
     * 生成完整报表数据
     *
     * @param report 报表实体
     * @param additionalFilters 额外的过滤条件
     * @return 完整报表数据
     */
    Object generateReportData(Report report, List<Filter> additionalFilters);

    /**
     * 生成报表数据（仅包含图表数据）
     *
     * @param report 报表实体
     * @param additionalFilters 额外的过滤条件
     * @return 图表数据映射
     */
    java.util.Map<String, Object> generateReportCharts(
        Report report,
        List<Filter> additionalFilters
    );

    /**
     * 删除报表
     *
     * @param id 报表ID
     */
    void deleteById(String id);

    /**
     * 删除报表
     *
     * @param report 报表实体
     */
    void delete(Report report);

    /**
     * 检查报表是否存在
     *
     * @param id 报表ID
     * @return 是否存在
     */
    boolean existsById(String id);

    /**
     * 根据名称检查报表是否存在
     *
     * @param name 报表名称
     * @return 是否存在
     */
    boolean existsByName(String name);

    /**
     * 统计报表数量
     *
     * @return 报表总数
     */
    long count();

    /**
     * 统计启用的报表数量
     *
     * @return 启用的报表总数
     */
    long countEnabled();

    /**
     * 根据状态统计报表数量
     *
     * @param status 报表状态
     * @return 报表数量
     */
    long countByStatus(String status);

    /**
     * 根据分类统计报表数量
     *
     * @param category 报表分类
     * @return 报表数量
     */
    long countByCategory(String category);

    /**
     * 根据创建者统计报表数量
     *
     * @param creator 创建者
     * @return 报表数量
     */
    long countByCreator(String creator);

    /**
     * 批量保存报表
     *
     * @param reports 报表列表
     * @return 保存后的报表列表
     */
    List<Report> saveAll(List<Report> reports);

    /**
     * 验证报表配置是否有效
     *
     * @param report 报表实体
     * @return 是否有效
     */
    boolean validateReportConfig(Report report);

    /**
     * 发布报表
     *
     * @param reportId 报表ID
     * @return 发布后的报表
     */
    Report publishReport(String reportId);

    /**
     * 归档报表
     *
     * @param reportId 报表ID
     * @return 归档后的报表
     */
    Report archiveReport(String reportId);

    /**
     * 复制报表
     *
     * @param reportId 源报表ID
     * @param newName 新报表名称
     * @param newCreator 新创建者
     * @return 复制后的报表
     */
    Report duplicateReport(String reportId, String newName, String newCreator);

    /**
     * 获取报表的性能统计信息
     *
     * @param reportId 报表ID
     * @return 性能统计
     */
    java.util.Map<String, Object> getPerformanceStats(String reportId);

    /**
     * 缓存报表数据
     *
     * @param report 报表实体
     * @param filters 过滤条件
     * @param data 报表数据
     * @param ttl 缓存时间（秒）
     */
    void cacheReportData(
        Report report,
        List<Filter> filters,
        Object data,
        long ttl
    );

    /**
     * 获取缓存的报表数据
     *
     * @param report 报表实体
     * @param filters 过滤条件
     * @return 缓存的数据
     */
    Optional<Object> getCachedReportData(
        Report report,
        List<Filter> filters
    );

    /**
     * 清除报表缓存
     *
     * @param reportId 报表ID
     */
    void clearCache(String reportId);

    /**
     * 清除所有报表缓存
     */
    void clearAllCache();

    /**
     * 导出报表配置
     *
     * @param reportId 报表ID
     * @return 报表配置JSON
     */
    String exportReportConfig(String reportId);

    /**
     * 导入报表配置
     *
     * @param configJson 报表配置JSON
     * @param newCreator 新创建者
     * @return 导入后的报表
     */
    Report importReportConfig(String configJson, String newCreator);

    /**
     * 获取报表的访问统计
     *
     * @param reportId 报表ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 访问统计
     */
    java.util.Map<String, Object> getAccessStats(
        String reportId,
        java.time.LocalDateTime startDate,
        java.time.LocalDateTime endDate
    );

    /**
     * 记录报表访问
     *
     * @param reportId 报表ID
     * @param userId 用户ID
     * @param accessType 访问类型
     */
    void recordAccess(String reportId, String userId, String accessType);

    /**
     * 获取热门报表列表
     *
     * @param limit 限制数量
     * @return 热门报表列表
     */
    List<Report> getPopularReports(int limit);

    /**
     * 获取最近更新的报表列表
     *
     * @param limit 限制数量
     * @return 最近更新的报表列表
     */
    List<Report> getRecentlyUpdatedReports(int limit);

    /**
     * 搜索报表
     *
     * @param keyword 关键词
     * @param filters 额外的过滤条件
     * @param page 页码
     * @param size 页面大小
     * @return 搜索结果
     */
    com.i0.domain.core.pagination.Pageable<Report> searchReports(
        String keyword,
        List<Filter> filters,
        int page,
        int size
    );
}