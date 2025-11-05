package com.i0.entity.gateway.web.controllers;

import com.i0.entity.application.dto.input.CreateEntityInput;
import com.i0.entity.application.dto.input.UpdateEntityInput;
import com.i0.entity.application.dto.input.EntityPageInput;
import com.i0.entity.application.dto.output.EntityOutput;
import com.i0.entity.application.dto.output.EntityPageOutput;
import com.i0.entity.application.usecases.*;
import com.i0.domain.core.pagination.Pageable;
import com.i0.entity.domain.valueobjects.EntityType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * Entity REST控制器
 * 提供Entity相关的CRUD API
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/entities")
@RequiredArgsConstructor
@Validated
public class EntityController {

    private final CreateEntityUseCase createEntityUseCase;
    private final GetEntityUseCase getEntityUseCase;
    private final UpdateEntityUseCase updateEntityUseCase;
    private final ActivateEntityUseCase activateEntityUseCase;
    private final DeactivateEntityUseCase deactivateEntityUseCase;
    private final DeleteEntityUseCase deleteEntityUseCase;
    private final SearchEntitiesUseCase searchEntitiesUseCase;
    private final GetActiveEntitiesUseCase getActiveEntitiesUseCase;

    /**
     * 创建实体
     *
     * @param request 创建请求
     * @return 创建的实体响应
     */
    @PostMapping
    public EntityOutput createEntity(@Valid @RequestBody CreateEntityInput request) {
        log.info("Creating entity with name: {}", request.getName());
        return createEntityUseCase.execute(request);
    }

    /**
     * 根据ID获取实体
     *
     * @param id 实体ID
     * @return 实体响应
     */
    @GetMapping("/{id}")
    public EntityOutput getEntity(@PathVariable @NotBlank String id) {
        log.debug("Getting entity by id: {}", id);
        return getEntityUseCase.execute(id);
    }

    /**
     * 更新实体
     *
     * @param id      实体ID
     * @param request 更新请求
     * @return 更新后的实体响应
     */
    @PutMapping("/{id}")
    public EntityOutput updateEntity(
            @PathVariable @NotBlank String id,
            @Valid @RequestBody UpdateEntityInput request) {
        log.info("Updating entity with id: {}", id);
        return updateEntityUseCase.execute(id, request);
    }

    /**
     * 激活实体
     *
     * @param id 实体ID
     * @return 更新后的实体响应
     */
    @PutMapping("/{id}/activate")
    public EntityOutput activateEntity(@PathVariable @NotBlank String id) {
        log.info("Activating entity with id: {}", id);
        return activateEntityUseCase.execute(id);
    }

    /**
     * 停用实体
     *
     * @param id 实体ID
     * @return 更新后的实体响应
     */
    @PutMapping("/{id}/deactivate")
    public EntityOutput deactivateEntity(@PathVariable @NotBlank String id) {
        log.info("Deactivating entity with id: {}", id);
        return deactivateEntityUseCase.execute(id);
    }

    /**
     * 删除实体
     *
     * @param id 实体ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public void deleteEntity(@PathVariable @NotBlank String id) {
        log.info("Deleting entity with id: {}", id);
        deleteEntityUseCase.execute(id);
    }

    /**
     * 分页查询实体
     *
     * @param page        页码（从0开始）
     * @param size        每页大小
     * @param entityType  实体类型过滤（可选）
     * @param nameKeyword 名称关键字搜索（可选）
     * @param activeOnly  是否只查询激活的实体（可选，默认false）
     * @return 分页响应
     */
    @GetMapping
    public Pageable<EntityPageOutput> getEntities(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) EntityType entityType,
            @RequestParam(required = false) String nameKeyword,
            @RequestParam(required = false) Boolean activeOnly) {

        log.debug("Getting entities with page: {}, size: {}, entityType: {}, nameKeyword: {}, activeOnly: {}",
                page, size, entityType, nameKeyword, activeOnly);

        EntityPageInput request = EntityPageInput.builder()
                .page(page)
                .size(size)
                .entityType(entityType)
                .nameKeyword(nameKeyword)
                .activeOnly(activeOnly)
                .build();

        return searchEntitiesUseCase.execute(request);
    }

    /**
     * 根据实体类型获取所有激活的实体
     *
     * @param entityType 实体类型
     * @return 实体列表
     */
    @GetMapping("/active")
    public List<EntityOutput> getActiveEntitiesByType(
        @RequestParam(required = false) EntityType entityType) {
        log.debug("Getting active entities by type: {}", entityType);
        return getActiveEntitiesUseCase.getByTypeOrNull(entityType);
    }

    /**
     * 获取所有实体类型
     *
     * @return 实体类型列表
     */
    @GetMapping("/types")
    public EntityType[] getEntityTypes() {
        log.debug("Getting all entity types");
        return EntityType.values();
    }
}