package com.i0.location.application.usecases;

import com.i0.location.application.dto.output.LocationOutput;
import com.i0.location.domain.entities.Location;
import com.i0.location.domain.repositories.LocationRepository;
import com.i0.location.domain.valueobjects.LocationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 根据类型获取地理位置列表用例
 * 负责根据地理位置类型获取全量数据的业务逻辑
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GetLocationsByTypeUseCase {

    private final LocationRepository locationRepository;

    /**
     * 执行根据类型获取地理位置列表用例
     * @param locationType 地理位置类型
     * @return 地理位置列表
     */
    public List<LocationOutput> execute(LocationType locationType) {
        log.info("Getting all locations by type: {}", locationType);

        // 根据类型获取所有地理位置
        List<Location> locations = locationRepository.findByLocationType(locationType);

        log.info("Found {} locations for type: {}", locations.size(), locationType);

        // 转换为输出DTO
        return locations.stream()
                .map(LocationOutput::from)
                .collect(Collectors.toList());
    }
}