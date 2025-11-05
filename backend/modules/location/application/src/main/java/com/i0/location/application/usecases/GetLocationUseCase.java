package com.i0.location.application.usecases;

import com.i0.location.application.dto.output.LocationOutput;
import com.i0.location.domain.entities.Location;
import com.i0.location.domain.repositories.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 获取单个地理位置用例
 * 负责根据ID获取地理位置信息
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GetLocationUseCase {

    private final LocationRepository locationRepository;

    /**
     * 执行获取地理位置用例
     * @param id 地理位置ID
     * @return 地理位置响应
     */
    public LocationOutput execute(String id) {
        log.info("Getting location with id: {}", id);

        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("地理位置不存在: " + id));

        log.info("Location found: {}", location.getName());
        return LocationOutput.from(location);
    }
}