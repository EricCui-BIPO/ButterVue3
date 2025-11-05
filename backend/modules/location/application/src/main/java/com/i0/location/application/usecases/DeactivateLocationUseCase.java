package com.i0.location.application.usecases;

import com.i0.location.application.dto.output.LocationOutput;
import com.i0.location.domain.entities.Location;
import com.i0.location.domain.repositories.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 禁用地理位置用例
 * 负责处理地理位置的禁用操作
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DeactivateLocationUseCase {

    private final LocationRepository locationRepository;

    /**
     * 执行禁用地理位置操作
     *
     * @param id 地理位置ID
     * @return 禁用后的地理位置输出
     * @throws IllegalArgumentException 当地理位置不存在时抛出
     */
    public LocationOutput execute(String id) {
        log.info("Deactivating location with id: {}", id);

        // 查找地理位置
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("地理位置不存在: " + id));

        // 禁用地理位置
        location.deactivate();

        // 保存更新
        Location savedLocation = locationRepository.save(location);

        log.info("Successfully deactivated location with id: {}", id);

        // 转换为输出DTO
        return LocationOutput.from(savedLocation);
    }
}