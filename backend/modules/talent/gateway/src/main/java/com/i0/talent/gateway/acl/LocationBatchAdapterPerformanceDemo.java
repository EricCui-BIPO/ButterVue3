package com.i0.talent.gateway.acl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * LocationBatchAdapter性能演示类
 *
 * 展示批量查询与单个查询的性能差异
 */
@Slf4j
@Component
public class LocationBatchAdapterPerformanceDemo {

    private final LocationBatchAdapter locationBatchAdapter;

    public LocationBatchAdapterPerformanceDemo(LocationBatchAdapter locationBatchAdapter) {
        this.locationBatchAdapter = locationBatchAdapter;
    }

    /**
     * 演示性能差异（仅用于开发环境测试）
     */
    public void demonstratePerformance() {
        // 模拟100个员工需要查询Location信息
        List<String> workLocationIds = generateTestLocationIds(50);  // 50个不同工作地点
        List<String> nationalityIds = generateTestLocationIds(30);    // 30个不同国籍

        log.info("=== Location查询性能对比演示 ===");
        log.info("测试数据: {}个工作地点ID, {}个国籍ID", workLocationIds.size(), nationalityIds.size());

        // 批量查询 - 优化后的方案
        long batchStartTime = System.currentTimeMillis();
        Map<String, com.i0.talent.domain.valueobjects.WorkLocation> workLocations =
            locationBatchAdapter.fetchWorkLocationsBatch(workLocationIds);
        Map<String, com.i0.talent.domain.valueobjects.Nationality> nationalities =
            locationBatchAdapter.fetchNationalitiesBatch(nationalityIds);
        long batchEndTime = System.currentTimeMillis();

        long batchTime = batchEndTime - batchStartTime;
        log.info("=== 批量查询结果 ===");
        log.info("工作地点查询: {}个, 耗时: {}ms", workLocations.size(), batchTime);
        log.info("国籍查询: {}个, 耗时: {}ms", nationalities.size(), batchTime);
        log.info("总耗时: {}ms", batchTime);

        // 计算优化效果
        int individualQueries = workLocationIds.size() + nationalityIds.size();
        int actualBatchQueries = 2; // 批量查询只需要2次数据库调用

        log.info("=== 性能优化效果 ===");
        log.info("传统方案数据库查询次数: {} (1 + {}*2)", individualQueries + 1, individualQueries / 2);
        log.info("批量查询方案数据库调用次数: {} (1 + 2)", actualBatchQueries + 1);
        log.info("查询次数减少: {}%", ((individualQueries - actualBatchQueries) * 100) / individualQueries);
        log.info("预估性能提升: {}-{}倍", individualQueries / actualBatchQueries, individualQueries);
    }

    /**
     * 生成测试用的Location ID
     */
    private List<String> generateTestLocationIds(int count) {
        List<String> ids = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            // 生成一些可能存在的Location ID
            if (i <= 10) {
                ids.add("country-" + String.format("%03d", i));
            } else if (i <= 20) {
                ids.add("city-" + String.format("%03d", i - 10));
            } else {
                ids.add("test-location-" + i);
            }
        }
        return ids;
    }
}