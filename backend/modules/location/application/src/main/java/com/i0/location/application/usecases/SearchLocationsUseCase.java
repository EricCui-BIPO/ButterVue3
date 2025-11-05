package com.i0.location.application.usecases;

import com.i0.domain.core.pagination.Pageable;
import com.i0.location.application.dto.input.LocationPageInput;
import com.i0.location.application.dto.output.LocationOutput;
import com.i0.location.domain.entities.Location;
import com.i0.location.domain.repositories.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 分页查询地理位置用例
 * 负责地理位置的分页查询业务逻辑
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SearchLocationsUseCase {

    private final LocationRepository locationRepository;

    /**
     * 执行分页查询地理位置用例
     * @param input 分页查询请求
     * @return 分页查询结果
     */
    public Pageable<LocationOutput> execute(LocationPageInput input) {
        log.info("Searching locations with criteria: name={}, type={}, parentId={}, activeOnly={}, page={}, size={}",
                input.getName(), input.getLocationType(), input.getParentId(), input.getActiveOnly(), input.getPage(), input.getSize());

        // 执行分页查询
        Pageable<Location> locations = locationRepository.searchLocations(
                input.getName(),
                input.getLocationType(),
                input.getParentId(),
                input.getActiveOnly(),
                input.getPage(),
                input.getSize()
        );

        log.info("Found {} locations (page {} of {}, total {})",
                locations.getNumberOfElements(),
                locations.getPage() + 1,
                locations.getTotalPages(),
                locations.getTotal());

        // 转换为输出DTO
        return locations.map(LocationOutput::from);
    }
}