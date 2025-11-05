package com.i0.location.application.usecases;

import com.i0.location.application.dto.output.LocationOutput;
import com.i0.location.domain.entities.Location;
import com.i0.location.domain.repositories.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 激活地理位置用例
 * 负责处理地理位置的激活操作
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ActivateLocationUseCase {

    private final LocationRepository locationRepository;

    /**
     * 执行激活地理位置操作
     *
     * @param id 地理位置ID
     * @return 激活后的地理位置输出
     * @throws IllegalArgumentException 当地理位置不存在时抛出
     */
    public LocationOutput execute(String id) {
        log.info("Activating location with id: {}", id);

        // 查找地理位置
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("地理位置不存在: " + id));

        // 激活地理位置
        location.activate();

        // 保存更新
        Location savedLocation = locationRepository.save(location);

        log.info("Successfully activated location with id: {}", id);

        // 转换为输出DTO
        return LocationOutput.from(savedLocation);
    }
}