package com.i0.report.application.usecases;

import com.i0.report.application.dto.input.CreateDatasetInput;
import com.i0.report.application.dto.output.DatasetOutput;
import com.i0.report.domain.entities.Dataset;
import com.i0.report.domain.repositories.DatasetRepository;
import com.i0.report.domain.valueobjects.Filter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 创建数据集用例
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CreateDatasetUseCase {

    private final DatasetRepository datasetRepository;

    /**
     * 执行创建数据集操作
     *
     * @param input 创建数据集输入
     * @return 创建后的数据集输出
     */
    @Transactional
    public DatasetOutput execute(CreateDatasetInput input) {
        log.info("开始创建数据集: {}", input.getName());

        // 验证数据集名称是否已存在
        if (datasetRepository.existsByName(input.getName())) {
            throw new IllegalArgumentException("数据集名称已存在: " + input.getName());
        }

        // 转换过滤条件
        java.util.List<Filter> filters = null;
        if (input.getFilters() != null && !input.getFilters().isEmpty()) {
            filters = input.getFilters().stream()
                    .map(this::convertToFilter)
                    .collect(Collectors.toList());
        }

        // 创建数据集实体
        Dataset dataset = Dataset.builder()
                .id(UUID.randomUUID().toString())
                .name(input.getName())
                .description(input.getDescription())
                .sql(input.getSql())
                .filters(filters)
                .dataSourceType(input.getDataSourceType())
                .updateStrategy(input.getUpdateStrategy())
                .updateInterval(input.getUpdateInterval())
                .enabled(input.getEnabled())
                .build();

        // 验证数据集配置
        if (!dataset.isValid()) {
            throw new IllegalArgumentException("数据集配置无效");
        }

        // 验证SQL语句
        if (!datasetRepository.validateSql(dataset.getSql(), dataset.getDataSourceType())) {
            throw new IllegalArgumentException("SQL语句语法错误或不适用于指定的数据源类型");
        }

        // 设置更新策略
        dataset.setUpdateStrategy(input.getUpdateStrategy(), input.getUpdateInterval());

        // 保存数据集
        Dataset savedDataset = datasetRepository.save(dataset);

        log.info("成功创建数据集: ID={}, 名称={}", savedDataset.getId(), savedDataset.getName());

        return DatasetOutput.from(savedDataset);
    }

    /**
     * 转换过滤条件输入为Domain值对象
     */
    private Filter convertToFilter(com.i0.report.application.dto.input.FilterInput filterInput) {
        return Filter.builder()
                .field(filterInput.getField())
                .operator(filterInput.getOperator())
                .value(filterInput.getValue())
                .mandatory(filterInput.getMandatory())
                .build();
    }
}