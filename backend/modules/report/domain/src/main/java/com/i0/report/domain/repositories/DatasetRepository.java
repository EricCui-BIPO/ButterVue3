package com.i0.report.domain.repositories;

import com.i0.report.domain.entities.Dataset;
import com.i0.report.domain.valueobjects.Filter;

import java.util.List;
import java.util.Optional;

/**
 * 数据集仓储接口
 * 定义数据集相关的持久化操作
 */
public interface DatasetRepository {

    /**
     * 保存数据集
     *
     * @param dataset 数据集实体
     * @return 保存后的数据集
     */
    Dataset save(Dataset dataset);

    /**
     * 根据ID查找数据集
     *
     * @param id 数据集ID
     * @return 数据集实体
     */
    Optional<Dataset> findById(String id);

    /**
     * 查找所有数据集
     *
     * @return 数据集列表
     */
    List<Dataset> findAll();

    /**
     * 根据名称查找数据集
     *
     * @param name 数据集名称
     * @return 数据集列表
     */
    List<Dataset> findByName(String name);

    /**
     * 根据数据源类型查找数据集
     *
     * @param dataSourceType 数据源类型
     * @return 数据集列表
     */
    List<Dataset> findByDataSourceType(String dataSourceType);

    /**
     * 根据更新策略查找数据集
     *
     * @param updateStrategy 更新策略
     * @return 数据集列表
     */
    List<Dataset> findByUpdateStrategy(String updateStrategy);

     /**
     * 查找启用的数据集
     *
     * @return 启用的数据集列表
     */
    List<Dataset> findEnabled();

    /**
     * 查找需要更新的数据集
     *
     * @return 需要更新的数据集列表
     */
    List<Dataset> findNeedsUpdate();

    /**
     * 根据过滤条件查找数据集
     *
     * @param filters 过滤条件列表
     * @return 符合条件的数据集列表
     */
    List<Dataset> findByFilters(List<Filter> filters);

    /**
     * 执行数据集SQL查询
     *
     * @param dataset 数据集实体
     * @param filters 额外的过滤条件
     * @return 查询结果
     */
    List<Object> executeQuery(Dataset dataset, List<Filter> filters);

    /**
     * 使用指定SQL执行查询（支持增强SQL）
     *
     * @param sql 完整的SQL语句
     * @param filters 额外的过滤条件
     * @return 查询结果
     */
    List<Object> executeQueryWithSql(String sql, List<Filter> filters);

    /**
     * 执行数据集SQL查询（带分页）
     *
     * @param dataset 数据集实体
     * @param filters 额外的过滤条件
     * @param page 页码（从0开始）
     * @param size 页面大小
     * @return 分页查询结果
     */
    com.i0.domain.core.pagination.Pageable<Object> executeQueryWithPagination(
        Dataset dataset,
        List<Filter> filters,
        int page,
        int size
    );

    /**
     * 删除数据集
     *
     * @param id 数据集ID
     */
    void deleteById(String id);

    /**
     * 删除数据集
     *
     * @param dataset 数据集实体
     */
    void delete(Dataset dataset);

    /**
     * 检查数据集是否存在
     *
     * @param id 数据集ID
     * @return 是否存在
     */
    boolean existsById(String id);

    /**
     * 根据名称检查数据集是否存在
     *
     * @param name 数据集名称
     * @return 是否存在
     */
    boolean existsByName(String name);

    /**
     * 统计数据集数量
     *
     * @return 数据集总数
     */
    long count();

    /**
     * 统计启用的数据集数量
     *
     * @return 启用的数据集总数
     */
    long countEnabled();

    /**
     * 根据数据源类型统计数据集数量
     *
     * @param dataSourceType 数据源类型
     * @return 数据集数量
     */
    long countByDataSourceType(String dataSourceType);

    /**
     * 验证数据集SQL是否有效
     *
     * @param sql SQL语句
     * @param dataSourceType 数据源类型
     * @return 是否有效
     */
    boolean validateSql(String sql, String dataSourceType);

    /**
     * 获取数据集的字段信息
     *
     * @param dataset 数据集实体
     * @return 字段信息列表
     */
    List<String> getDatasetFields(Dataset dataset);

    /**
     * 批量保存数据集
     *
     * @param datasets 数据集列表
     * @return 保存后的数据集列表
     */
    List<Dataset> saveAll(List<Dataset> datasets);

    /**
     * 刷新数据集数据
     *
     * @param dataset 数据集实体
     * @return 刷新结果
     */
    boolean refreshDataset(Dataset dataset);

    /**
     * 获取数据集最后更新时间
     *
     * @param datasetId 数据集ID
     * @return 最后更新时间
     */
    java.time.LocalDateTime getLastUpdateTime(String datasetId);
}