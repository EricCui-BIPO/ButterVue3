package com.i0.location.application.usecases;

import com.i0.location.application.dto.input.CreateLocationInput;
import com.i0.location.application.dto.output.LocationOutput;
import com.i0.location.domain.entities.Location;
import com.i0.location.domain.repositories.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 创建地理位置用例
 * 负责地理位置的创建业务逻辑
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CreateLocationUseCase {

    private final LocationRepository locationRepository;

    /**
     * 执行创建地理位置用例
     * @param input 创建请求
     * @return 创建的地理位置响应
     */
    public LocationOutput execute(CreateLocationInput input) {
        log.info("Creating location with name: {}, type: {}", input.getName(), input.getLocationType());

        // 验证地理位置名称唯一性
        if (locationRepository.existsByName(input.getName())) {
            throw new IllegalArgumentException("地理位置名称已存在: " + input.getName());
        }

        // 验证ISO代码唯一性（如果提供了ISO代码）
        if (input.getIsoCode() != null && !input.getIsoCode().trim().isEmpty()) {
            if (locationRepository.existsByIsoCode(input.getIsoCode())) {
                throw new IllegalArgumentException("ISO代码已存在: " + input.getIsoCode());
            }
        }

        // 如果指定了上级ID，验证上级地理位置存在且激活
        if (input.getParentId() != null && !input.getParentId().trim().isEmpty()) {
            locationRepository.findById(input.getParentId())
                    .filter(Location::isActive)
                    .orElseThrow(() -> new IllegalArgumentException("上级地理位置不存在或未激活: " + input.getParentId()));
        }

        // 创建地理位置
        Location location = Location.create(
                input.getName(),
                input.getLocationType(),
                input.getIsoCode(),
                input.getDescription(),
                input.getParentId()
        );

        // 设置排序序号
        if (input.getSortOrder() != null) {
            location.setSortOrder(input.getSortOrder());
        }

        // 生成ID
        location.setId(UUID.randomUUID().toString());

        // 保存地理位置
        Location savedLocation = locationRepository.save(location);

        log.info("Location created successfully with id: {}", savedLocation.getId());
        return LocationOutput.from(savedLocation);
    }
}