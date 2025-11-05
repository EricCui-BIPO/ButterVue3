package com.i0.location.application.usecases;

import com.i0.location.application.dto.output.LocationOutput;
import com.i0.location.domain.entities.Location;
import com.i0.location.domain.repositories.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 删除地理位置用例
 * 负责地理位置的删除业务逻辑
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteLocationUseCase {

    private final LocationRepository locationRepository;

    /**
     * 执行删除地理位置用例
     * @param id 地理位置ID
     * @return 被删除的地理位置响应
     */
    public LocationOutput execute(String id) {
        log.info("Deleting location with id: {}", id);

        // 查找地理位置
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("地理位置不存在: " + id));

        // 检查是否有下级地理位置
        long childrenCount = locationRepository.countByParentId(id);
        if (childrenCount > 0) {
            throw new IllegalStateException("该地理位置下还有" + childrenCount + "个下级地理位置，无法删除");
        }

        // 保存删除前的信息用于返回
        LocationOutput deletedOutput = LocationOutput.from(location);

        // 删除地理位置
        locationRepository.delete(location);

        log.info("Location deleted successfully with id: {}", id);
        return deletedOutput;
    }
}