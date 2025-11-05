package com.i0.location.application.usecases;

import com.i0.location.application.dto.output.LocationOutput;
import com.i0.location.domain.entities.Location;
import com.i0.location.domain.repositories.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 根据上级ID获取下级地理位置列表用例
 * 负责根据上级地理位置ID获取全量下级数据的业务逻辑
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GetLocationsByParentUseCase {

    private final LocationRepository locationRepository;

    /**
     * 执行根据上级ID获取下级地理位置列表用例
     * @param parentId 上级地理位置ID
     * @return 下级地理位置列表
     */
    public List<LocationOutput> execute(String parentId) {
        log.info("Getting all locations by parent id: {}", parentId);

        // 根据上级ID获取所有下级地理位置
        List<Location> locations = locationRepository.findByParentId(parentId);

        log.info("Found {} locations for parent id: {}", locations.size(), parentId);

        // 转换为输出DTO
        return locations.stream()
                .map(LocationOutput::from)
                .collect(Collectors.toList());
    }
}