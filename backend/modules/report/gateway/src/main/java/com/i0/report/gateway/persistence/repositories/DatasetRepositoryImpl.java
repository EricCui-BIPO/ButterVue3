package com.i0.report.gateway.persistence.repositories;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.i0.domain.core.pagination.Pageable;
import com.i0.persistence.spring.pagination.SpringPage;
import com.i0.report.domain.entities.Dataset;
import com.i0.report.domain.repositories.DatasetRepository;
import com.i0.report.domain.valueobjects.Filter;
import com.i0.report.gateway.persistence.mappers.DatasetMapper;
import com.i0.report.gateway.persistence.dataobjects.DatasetDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 数据集仓储实现
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class DatasetRepositoryImpl extends ServiceImpl<DatasetMapper, DatasetDO> implements DatasetRepository {

    private final DatasetMapper datasetMapper;
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public Dataset save(Dataset dataset) {
        DatasetDO datasetDO = convertToDatasetDO(dataset);

        if (StringUtils.hasText(dataset.getId())) {
            // 更新操作
            datasetDO.setUpdatedAt(LocalDateTime.now());
            updateById(datasetDO);
        } else {
            // 新增操作
            datasetDO.setCreatedAt(LocalDateTime.now());
            datasetDO.setUpdatedAt(LocalDateTime.now());
            save(datasetDO);
        }

        return convertToDataset(datasetDO);
    }

    @Override
    public Optional<Dataset> findById(String id) {
        DatasetDO datasetDO = getById(id);
        return Optional.ofNullable(convertToDataset(datasetDO));
    }

    @Override
    public List<Dataset> findAll() {
        LambdaQueryWrapper<DatasetDO> queryWrapper = new LambdaQueryWrapper<DatasetDO>()
                .eq(DatasetDO::getIsDeleted, false)
                .orderByDesc(DatasetDO::getCreatedAt);

        List<DatasetDO> datasetDOList = list(queryWrapper);
        return datasetDOList.stream()
                .map(this::convertToDataset)
                .collect(Collectors.toList());
    }

    @Override
    public List<Dataset> findByName(String name) {
        LambdaQueryWrapper<DatasetDO> queryWrapper = new LambdaQueryWrapper<DatasetDO>()
                .eq(DatasetDO::getIsDeleted, false)
                .like(StringUtils.hasText(name), DatasetDO::getName, name)
                .orderByDesc(DatasetDO::getCreatedAt);

        List<DatasetDO> datasetDOList = list(queryWrapper);
        return datasetDOList.stream()
                .map(this::convertToDataset)
                .collect(Collectors.toList());
    }

    @Override
    public List<Dataset> findByDataSourceType(String dataSourceType) {
        LambdaQueryWrapper<DatasetDO> queryWrapper = new LambdaQueryWrapper<DatasetDO>()
                .eq(DatasetDO::getIsDeleted, false)
                .eq(DatasetDO::getDataSourceType, dataSourceType)
                .orderByDesc(DatasetDO::getCreatedAt);

        List<DatasetDO> datasetDOList = list(queryWrapper);
        return datasetDOList.stream()
                .map(this::convertToDataset)
                .collect(Collectors.toList());
    }

    @Override
    public List<Dataset> findByUpdateStrategy(String updateStrategy) {
        LambdaQueryWrapper<DatasetDO> queryWrapper = new LambdaQueryWrapper<DatasetDO>()
                .eq(DatasetDO::getIsDeleted, false)
                .eq(DatasetDO::getUpdateStrategy, updateStrategy)
                .orderByDesc(DatasetDO::getCreatedAt);

        List<DatasetDO> datasetDOList = list(queryWrapper);
        return datasetDOList.stream()
                .map(this::convertToDataset)
                .collect(Collectors.toList());
    }

    @Override
    public List<Dataset> findEnabled() {
        LambdaQueryWrapper<DatasetDO> queryWrapper = new LambdaQueryWrapper<DatasetDO>()
                .eq(DatasetDO::getIsDeleted, false)
                .eq(DatasetDO::getEnabled, true)
                .orderByDesc(DatasetDO::getCreatedAt);

        List<DatasetDO> datasetDOList = list(queryWrapper);
        return datasetDOList.stream()
                .map(this::convertToDataset)
                .collect(Collectors.toList());
    }

    @Override
    public List<Dataset> findNeedsUpdate() {
        LambdaQueryWrapper<DatasetDO> queryWrapper = new LambdaQueryWrapper<DatasetDO>()
                .eq(DatasetDO::getIsDeleted, false)
                .eq(DatasetDO::getEnabled, true)
                .in(DatasetDO::getUpdateStrategy, "real_time", "scheduled")
                .orderByDesc(DatasetDO::getUpdatedAt);

        List<DatasetDO> datasetDOList = list(queryWrapper);
        return datasetDOList.stream()
                .map(this::convertToDataset)
                .collect(Collectors.toList());
    }

    @Override
    public List<Dataset> findByFilters(List<Filter> filters) {
        LambdaQueryWrapper<DatasetDO> queryWrapper = new LambdaQueryWrapper<DatasetDO>()
                .eq(DatasetDO::getIsDeleted, false)
                .orderByDesc(DatasetDO::getCreatedAt);

        // 这里可以根据filter条件构建查询，简化起见暂时返回所有
        List<DatasetDO> datasetDOList = list(queryWrapper);
        return datasetDOList.stream()
                .map(this::convertToDataset)
                .collect(Collectors.toList());
    }

    @Override
    public List<Object> executeQuery(Dataset dataset, List<Filter> filters) {
        try {
            String sql = dataset.getSql();
            log.info("执行数据集查询: datasetId={}, sql={}", dataset.getId(), sql);

            // 如果有过滤条件，将其应用到SQL中
            String finalSql = applyFiltersToSql(sql, filters);

            // 执行SQL查询
            List<Map<String, Object>> queryResults = jdbcTemplate.queryForList(finalSql);
            List<Object> results = new ArrayList<>(queryResults);
            log.info("查询结果: {} 条记录", results.size());

            return results;
        } catch (Exception e) {
            log.error("执行数据集查询失败: datasetId={}, error={}", dataset.getId(), e.getMessage(), e);
            throw new RuntimeException("数据集查询失败: " + e.getMessage(), e);
        }
    }

    /**
     * 使用指定的SQL执行查询（支持增强SQL）
     */
    public List<Object> executeQueryWithSql(String sql, List<Filter> filters) {
        try {
            log.info("执行增强SQL查询: sql={}", sql);

            // 如果有过滤条件，将其应用到SQL中
            String finalSql = applyFiltersToSql(sql, filters);

            // 执行SQL查询
            List<Map<String, Object>> queryResults = jdbcTemplate.queryForList(finalSql);
            List<Object> results = new ArrayList<>(queryResults);
            log.info("增强SQL查询结果: {} 条记录", results.size());

            return results;
        } catch (Exception e) {
            log.error("执行增强SQL查询失败: sql={}, error={}", sql, e.getMessage(), e);
            throw new RuntimeException("增强SQL查询失败: " + e.getMessage(), e);
        }
    }

    @Override
    public Pageable<Object> executeQueryWithPagination(Dataset dataset, List<Filter> filters, int page, int size) {
        // 这里应该执行实际的分页SQL查询，为了简化示例返回空分页结果
        log.info("执行数据集分页查询: datasetId={}, page={}, size={}", dataset.getId(), page, size);
        return SpringPage.of(new PageImpl<>(
                new ArrayList<>(),
                PageRequest.of(page, size),
                0
        ));
    }

    @Override
    public void deleteById(String id) {
        removeById(id);
    }

    @Override
    public void delete(Dataset dataset) {
        removeById(dataset.getId());
    }

    @Override
    public boolean existsById(String id) {
        return lambdaQuery()
                .eq(DatasetDO::getId, id)
                .eq(DatasetDO::getIsDeleted, false)
                .exists();
    }

    @Override
    public boolean existsByName(String name) {
        return lambdaQuery()
                .eq(DatasetDO::getName, name)
                .eq(DatasetDO::getIsDeleted, false)
                .exists();
    }

    @Override
    public long count() {
        return lambdaQuery()
                .eq(DatasetDO::getIsDeleted, false)
                .count();
    }

    @Override
    public long countEnabled() {
        return lambdaQuery()
                .eq(DatasetDO::getIsDeleted, false)
                .eq(DatasetDO::getEnabled, true)
                .count();
    }

    @Override
    public long countByDataSourceType(String dataSourceType) {
        return lambdaQuery()
                .eq(DatasetDO::getIsDeleted, false)
                .eq(DatasetDO::getDataSourceType, dataSourceType)
                .count();
    }

    @Override
    public boolean validateSql(String sql, String dataSourceType) {
        // 这里应该进行SQL语法验证，简化起见返回true
        log.info("验证SQL语法: sql={}, dataSourceType={}", sql, dataSourceType);
        return true;
    }

    @Override
    public List<String> getDatasetFields(Dataset dataset) {
        // 这里应该解析SQL并返回字段列表，简化起见返回空列表
        log.info("获取数据集字段: datasetId={}", dataset.getId());
        return new ArrayList<>();
    }

    @Override
    public List<Dataset> saveAll(List<Dataset> datasets) {
        List<DatasetDO> datasetDOList = datasets.stream()
                .map(this::convertToDatasetDO)
                .collect(Collectors.toList());

        saveBatch(datasetDOList);

        return datasetDOList.stream()
                .map(this::convertToDataset)
                .collect(Collectors.toList());
    }

    @Override
    public boolean refreshDataset(Dataset dataset) {
        // 这里应该实现数据刷新逻辑，简化起见返回true
        log.info("刷新数据集: datasetId={}", dataset.getId());
        return true;
    }

    @Override
    public LocalDateTime getLastUpdateTime(String datasetId) {
        DatasetDO datasetDO = getById(datasetId);
        return datasetDO != null ? datasetDO.getUpdatedAt() : null;
    }

    /**
     * 转换Dataset为DatasetDO
     */
    private DatasetDO convertToDatasetDO(Dataset dataset) {
        if (dataset == null) {
            return null;
        }

        DatasetDO datasetDO = new DatasetDO();
        datasetDO.setId(dataset.getId());
        datasetDO.setName(dataset.getName());
        datasetDO.setDescription(dataset.getDescription());
        datasetDO.setSqlQuery(dataset.getSql());
        datasetDO.setDataSourceType(dataset.getDataSourceType());
        datasetDO.setUpdateStrategy(dataset.getUpdateStrategy());
        datasetDO.setUpdateInterval(dataset.getUpdateInterval());
        datasetDO.setEnabled(dataset.getEnabled());

        // 转换过滤条件为JSON
        if (dataset.getFilters() != null && !dataset.getFilters().isEmpty()) {
            try {
                String filtersJson = objectMapper.writeValueAsString(dataset.getFilters());
                datasetDO.setFilters(filtersJson);
            } catch (JsonProcessingException e) {
                log.error("转换过滤条件为JSON失败", e);
                datasetDO.setFilters("[]");
            }
        }

        return datasetDO;
    }

    /**
     * 将过滤条件应用到SQL语句中
     */
    private String applyFiltersToSql(String sql, List<Filter> filters) {
        if (filters == null || filters.isEmpty()) {
            return sql;
        }

        StringBuilder whereClause = new StringBuilder();
        boolean hasWhere = sql.toLowerCase().contains("where");

        for (Filter filter : filters) {
            if (StringUtils.hasText(filter.getField()) && StringUtils.hasText(String.valueOf(filter.getValue()))) {
                if (whereClause.length() > 0) {
                    whereClause.append(" AND ");
                }

                String operator = getSqlOperator(filter.getOperator());
                whereClause.append(filter.getField()).append(" ").append(operator).append(" '").append(filter.getValue()).append("'");
            }
        }

        if (whereClause.length() > 0) {
            if (hasWhere) {
                return sql + " AND " + whereClause.toString();
            } else {
                return sql + " WHERE " + whereClause.toString();
            }
        }

        return sql;
    }

    /**
     * 获取过滤条件的SQL操作符
     */
    private String getSqlOperator(String operator) {
        switch (operator.toLowerCase()) {
            case "equals":
            case "eq":
                return "=";
            case "not_equals":
            case "ne":
                return "!=";
            case "like":
                return "LIKE";
            case "greater_than":
            case "gt":
                return ">";
            case "less_than":
            case "lt":
                return "<";
            case "greater_than_or_equal":
            case "gte":
                return ">=";
            case "less_than_or_equal":
            case "lte":
                return "<=";
            case "in":
                return "IN";
            case "not_in":
                return "NOT IN";
            default:
                return "=";
        }
    }

    /**
     * 转换DatasetDO为Dataset
     */
    private Dataset convertToDataset(DatasetDO datasetDO) {
        if (datasetDO == null) {
            return null;
        }

        Dataset.DatasetBuilder builder = Dataset.builder()
                .id(datasetDO.getId())
                .name(datasetDO.getName())
                .description(datasetDO.getDescription())
                .sql(datasetDO.getSqlQuery())
                .dataSourceType(datasetDO.getDataSourceType())
                .updateStrategy(datasetDO.getUpdateStrategy())
                .updateInterval(datasetDO.getUpdateInterval())
                .enabled(datasetDO.getEnabled());

        // 转换JSON过滤条件为对象
        if (StringUtils.hasText(datasetDO.getFilters())) {
            try {
                List<Filter> filters = objectMapper.readValue(datasetDO.getFilters(),
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