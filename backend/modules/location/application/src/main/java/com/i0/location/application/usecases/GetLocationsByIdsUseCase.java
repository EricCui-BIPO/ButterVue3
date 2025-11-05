package com.i0.location.application.usecases;

import com.i0.location.application.dto.output.LocationOutput;
import com.i0.location.domain.entities.Location;
import com.i0.location.domain.repositories.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * 批量获取地理位置用例
 * 负责根据多个ID批量获取地理位置信息的业务逻辑
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GetLocationsByIdsUseCase {

    private final LocationRepository locationRepository;

    /**
     * 执行批量获取地理位置用例
     * @param locationIds 地理位置ID集合
     * @return 地理位置列表
     */
    public List<LocationOutput> execute(Iterable<String> locationIds) {
        if (locationIds == null) {
            return Collections.emptyList();
        }

        // 去重并过滤无效ID
        Set<String> uniqueLocationIds = StreamSupport.stream(locationIds.spliterator(), false)
                .filter(id -> id != null && !id.trim().isEmpty())
                .collect(Collectors.toSet());

        if (uniqueLocationIds.isEmpty()) {
            return Collections.emptyList();
        }

        log.debug("Getting locations by IDs: {}", uniqueLocationIds);

        // 批量查询位置信息
        List<Location> locations = locationRepository.findAllById(uniqueLocationIds);

        // 转换为LocationOutput列表
        List<LocationOutput> locationOutputs = locations.stream()
                .filter(java.util.Objects::nonNull)
                .map(LocationOutput::from)
                .collect(Collectors.toList());

        log.info("Batch loaded {} locations from {} requested",
                locationOutputs.size(), uniqueLocationIds.size());

        return locationOutputs;
    }
}