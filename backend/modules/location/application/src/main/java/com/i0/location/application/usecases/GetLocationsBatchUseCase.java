package com.i0.location.application.usecases;

import com.i0.location.application.dto.output.LocationOutput;
import com.i0.location.domain.entities.Location;
import com.i0.location.domain.repositories.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 批量获取地理位置用例
 *
 * 解决N+1查询问题的核心组件，通过批量查询大幅减少数据库调用次数
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GetLocationsBatchUseCase {

    private final LocationRepository locationRepository;

    /**
     * 批量执行地理位置查询
     *
     * @param ids 地理位置ID集合
     * @return ID到地理位置响应的映射
     */
    public Map<String, LocationOutput> execute(Collection<String> ids) {
        if (ids == null || ids.isEmpty()) {
            log.debug("Empty location IDs collection provided, returning empty map");
            return Map.of();
        }

        // 过滤掉空值并去重
        List<String> validIds = ids.stream()
                .filter(id -> id != null && !id.trim().isEmpty())
                .distinct()
                .collect(Collectors.toList());

        if (validIds.isEmpty()) {
            log.debug("No valid location IDs after filtering, returning empty map");
            return Map.of();
        }

        log.info("Batch fetching {} locations", validIds.size());

        try {
            // 批量查询地理位置
            List<Location> locations = locationRepository.findAllById(validIds);

            // 转换为输出DTO并构建ID映射
            Map<String, LocationOutput> resultMap = locations.stream()
                    .collect(Collectors.toMap(
                            Location::getId,
                            LocationOutput::from,
                            (existing, replacement) -> existing // 处理重复ID，保留第一个
                    ));

            log.info("Successfully fetched {} locations", resultMap.size());
            return resultMap;

        } catch (Exception e) {
            log.error("Error fetching locations batch for IDs: {}", validIds, e);
            throw new RuntimeException("批量获取地理位置失败: " + e.getMessage(), e);
        }
    }

    /**
     * 批量获取地理位置（支持List输入）
     *
     * @param ids 地理位置ID列表
     * @return ID到地理位置响应的映射
     */
    public Map<String, LocationOutput> execute(List<String> ids) {
        return execute((Collection<String>) ids);
    }

    /**
     * 批量获取地理位置，返回列表格式
     *
     * @param ids 地理位置ID集合
     * @return 地理位置响应列表
     */
    public List<LocationOutput> executeAsList(Collection<String> ids) {
        return ids.stream()
                .filter(id -> id != null && !id.trim().isEmpty())
                .distinct()
                .map(this::executeSingle)
                .collect(Collectors.toList());
    }

    /**
     * 单个查询（用于fallback）
     *
     * @param id 地理位置ID
     * @return 地理位置响应
     */
    public LocationOutput executeSingle(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }

        try {
            Location location = locationRepository.findById(id).orElse(null);
            return location != null ? LocationOutput.from(location) : null;
        } catch (Exception e) {
            log.warn("Error fetching single location for ID: {}, error: {}", id, e.getMessage());
            return null;
        }
    }
}