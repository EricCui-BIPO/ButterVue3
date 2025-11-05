package com.i0.location.gateway.web.controllers;

import com.i0.location.application.dto.input.CreateLocationInput;
import com.i0.location.application.dto.input.LocationPageInput;
import com.i0.location.application.dto.input.UpdateLocationInput;
import com.i0.location.application.dto.output.LocationOutput;
import com.i0.location.application.usecases.*;
import com.i0.location.application.usecases.GetAllLocationsTreeUseCase;
import java.util.List;
import com.i0.domain.core.pagination.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * Location REST控制器
 * 提供Location相关的CRUD API
 */
@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
@Validated
public class LocationController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LocationController.class);
    private final CreateLocationUseCase createLocationUseCase;
    private final GetLocationUseCase getLocationUseCase;
    private final UpdateLocationUseCase updateLocationUseCase;
    private final DeleteLocationUseCase deleteLocationUseCase;
    private final SearchLocationsUseCase searchLocationsUseCase;
    private final GetLocationsByTypeUseCase getLocationsByTypeUseCase;
    private final GetLocationsByParentUseCase getLocationsByParentUseCase;
    private final DeactivateLocationUseCase deactivateLocationUseCase;
    private final ActivateLocationUseCase activateLocationUseCase;
    private final GetAllLocationsTreeUseCase getAllLocationsTreeUseCase;

    /**
     * 创建地理位置
     *
     * @param input 创建请求
     * @return 创建的地理位置响应
     */
    @PostMapping
    public LocationOutput createLocation(@Valid @RequestBody CreateLocationInput input) {
        log.info("Creating location with name: {}", input.getName());
        return createLocationUseCase.execute(input);
    }

    /**
     * 根据ID获取地理位置
     *
     * @param id 地理位置ID
     * @return 地理位置响应
     */
    @GetMapping("/{id}")
    public LocationOutput getLocation(@PathVariable @NotBlank(message = "ID不能为空") String id) {
        log.info("Getting location with id: {}", id);
        return getLocationUseCase.execute(id);
    }

    /**
     * 更新地理位置
     *
     * @param id    地理位置ID
     * @param input 更新请求
     * @return 更新后的地理位置响应
     */
    @PutMapping("/{id}")
    public LocationOutput updateLocation(
            @PathVariable @NotBlank(message = "ID不能为空") String id,
            @Valid @RequestBody UpdateLocationInput input) {
        log.info("Updating location with id: {}", id);
        return updateLocationUseCase.execute(id, input);
    }

    /**
     * 删除地理位置
     *
     * @param id 地理位置ID
     * @return 被删除的地理位置响应
     */
    @DeleteMapping("/{id}")
    public LocationOutput deleteLocation(@PathVariable @NotBlank(message = "ID不能为空") String id) {
        log.info("Deleting location with id: {}", id);
        return deleteLocationUseCase.execute(id);
    }

    /**
     * 分页查询地理位置
     *
     * @param page  页码（从0开始）
     * @param size  每页大小
     * @param name  名称关键字（可选）
     * @param type  地理位置类型（可选）
     * @param parentId 上级地理位置ID（可选）
     * @param activeOnly 是否只查询激活状态（可选，null表示不限制）
     * @return 分页查询结果
     */
    @GetMapping
    public Pageable<LocationOutput> searchLocations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String parentId,
            @RequestParam(required = false) Boolean activeOnly) {

        log.info("Searching locations with page: {}, size: {}, name: {}, type: {}, parentId: {}, activeOnly: {}",
                page, size, name, type, parentId, activeOnly);

        LocationPageInput input = LocationPageInput.builder()
                .page(page)
                .size(size)
                .name(name)
                .locationType(type != null ? com.i0.location.domain.valueobjects.LocationType.valueOf(type) : null)
                .parentId(parentId)
                .activeOnly(activeOnly)
                .build();

        return searchLocationsUseCase.execute(input);
    }

    /**
     * 根据上级ID获取下级地理位置列表
     *
     * @param parentId 上级地理位置ID
     * @return 下级地理位置列表
     */
    @GetMapping("/by-parent/{parentId}")
    public List<LocationOutput> getLocationsByParent(
            @PathVariable @NotBlank(message = "上级ID不能为空") String parentId) {

        log.info("Getting all locations by parent id: {}", parentId);

        return getLocationsByParentUseCase.execute(parentId);
    }

    /**
     * 根据类型获取地理位置列表
     *
     * @param type 地理位置类型
     * @return 地理位置列表
     */
    @GetMapping("/by-type/{type}")
    public List<LocationOutput> getLocationsByType(
            @PathVariable @NotBlank(message = "类型不能为空") String type) {

        log.info("Getting all locations by type: {}", type);

        com.i0.location.domain.valueobjects.LocationType locationType = 
                com.i0.location.domain.valueobjects.LocationType.valueOf(type);

        return getLocationsByTypeUseCase.execute(locationType);
    }

    /**
     * 禁用地理位置
     *
     * @param id 地理位置ID
     * @return 禁用后的地理位置响应
     */
    @PatchMapping("/{id}/deactivate")
    public LocationOutput deactivateLocation(@PathVariable @NotBlank(message = "ID不能为空") String id) {
        log.info("Deactivating location with id: {}", id);
        return deactivateLocationUseCase.execute(id);
    }

    /**
     * 激活地理位置
     *
     * @param id 地理位置ID
     * @return 激活后的地理位置响应
     */
    @PatchMapping("/{id}/activate")
    public LocationOutput activateLocation(@PathVariable @NotBlank(message = "ID不能为空") String id) {
        log.info("Activating location with id: {}", id);
        return activateLocationUseCase.execute(id);
    }

    /**
     * 获取全量地理位置树形结构
     *
     * @return 树形结构的地理位置列表
     */
    @GetMapping("/tree")
    public List<LocationOutput> getAllLocationsTree() {
        log.info("Getting all locations in tree structure");
        return getAllLocationsTreeUseCase.execute();
    }
}