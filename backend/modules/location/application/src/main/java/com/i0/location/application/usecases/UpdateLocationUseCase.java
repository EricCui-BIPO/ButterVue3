package com.i0.location.application.usecases;

import com.i0.location.application.dto.input.UpdateLocationInput;
import com.i0.location.application.dto.output.LocationOutput;
import com.i0.location.domain.entities.Location;
import com.i0.location.domain.repositories.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 更新地理位置用例
 * 负责地理位置的更新业务逻辑
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateLocationUseCase {

    private final LocationRepository locationRepository;

    /**
     * 执行更新地理位置用例
     * @param id 地理位置ID
     * @param input 更新请求
     * @return 更新后的地理位置响应
     */
    public LocationOutput execute(String id, UpdateLocationInput input) {
        log.info("Updating location with id: {}", id);

        // 查找地理位置
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("地理位置不存在: " + id));

        // 验证地理位置名称唯一性（排除自己）
        if (locationRepository.existsByNameAndIdNot(input.getName(), id)) {
            throw new IllegalArgumentException("地理位置名称已存在: " + input.getName());
        }

        // 如果指定了上级ID，验证上级地理位置存在且激活
        if (input.getParentId() != null && !input.getParentId().trim().isEmpty()) {
            // 防止循环引用
            if (input.getParentId().equals(id)) {
                throw new IllegalArgumentException("不能将自己设为上级地理位置");
            }

            locationRepository.findById(input.getParentId())
                    .filter(Location::isActive)
                    .orElseThrow(() -> new IllegalArgumentException("上级地理位置不存在或未激活: " + input.getParentId()));
        }

        // 更新地理位置信息
        location.update(input.getName(), input.getDescription(), input.getSortOrder());

        // 更新上级地理位置
        if (input.getParentId() != null) {
            location.updateParent(input.getParentId());
        }

        // 保存地理位置
        Location updatedLocation = locationRepository.save(location);

        log.info("Location updated successfully with id: {}", updatedLocation.getId());
        return LocationOutput.from(updatedLocation);
    }
}