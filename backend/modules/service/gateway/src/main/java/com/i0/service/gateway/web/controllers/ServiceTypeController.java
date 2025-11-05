package com.i0.service.gateway.web.controllers;

import com.i0.domain.core.pagination.Pageable;
import com.i0.service.application.dto.input.CreateServiceTypeInput;
import com.i0.service.application.dto.input.ServiceTypePageInput;
import com.i0.service.application.dto.input.UpdateServiceTypeInput;
import com.i0.service.application.dto.output.ServiceTypeOutput;
import com.i0.service.application.dto.output.ServiceTypePageOutput;
import com.i0.service.application.usecases.*;
import com.i0.service.domain.valueobjects.ServiceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * 服务类型管理控制器
 * 提供服务类型的CRUD REST API
 */
@RestController
@RequestMapping("/api/v1/service-types")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ServiceTypeController {

    private final CreateServiceTypeUseCase createServiceTypeUseCase;
    private final GetServiceTypeUseCase getServiceTypeUseCase;
    private final UpdateServiceTypeUseCase updateServiceTypeUseCase;
    private final DeleteServiceTypeUseCase deleteServiceTypeUseCase;
    private final SearchServiceTypesUseCase searchServiceTypesUseCase;
    private final ActivateServiceTypeUseCase activateServiceTypeUseCase;
    private final DeactivateServiceTypeUseCase deactivateServiceTypeUseCase;

    /**
     * 创建服务类型
     *
     * @param request 创建服务类型请求
     * @return 创建的服务类型
     */
    @PostMapping
    public ServiceTypeOutput createServiceType(@Valid @RequestBody CreateServiceTypeInput request) {
        log.info("Creating service type with name: {}", request.getName());
        return createServiceTypeUseCase.execute(request);
    }

    /**
     * 根据ID获取服务类型
     *
     * @param id 服务类型ID
     * @return 服务类型信息
     */
    @GetMapping("/{id}")
    public ServiceTypeOutput getServiceType(@PathVariable @NotBlank String id) {
        log.debug("Getting service type by id: {}", id);
        return getServiceTypeUseCase.execute(id);
    }

    /**
     * 更新服务类型
     *
     * @param id      服务类型ID
     * @param request 更新请求
     * @return 更新后的服务类型
     */
    @PutMapping("/{id}")
    public ServiceTypeOutput updateServiceType(
        @PathVariable @NotBlank String id,
        @Valid @RequestBody UpdateServiceTypeInput request) {
        log.info("Updating service type with id: {}", id);
        return updateServiceTypeUseCase.execute(id, request);
    }

    /**
     * 删除服务类型
     *
     * @param id 服务类型ID
     */
    @DeleteMapping("/{id}")
    public void deleteServiceType(@PathVariable @NotBlank String id) {
        log.info("Deleting service type with id: {}", id);
        deleteServiceTypeUseCase.execute(id);
    }

    /**
     * 激活服务类型
     *
     * @param id 服务类型ID
     * @return 激活后的服务类型信息
     */
    @PutMapping("/{id}/activate")
    public ServiceTypeOutput activateServiceType(@PathVariable @NotBlank String id) {
        log.info("Activating service type with id: {}", id);
        return activateServiceTypeUseCase.execute(id);
    }

    /**
     * 停用服务类型
     *
     * @param id 服务类型ID
     * @return 停用后的服务类型信息
     */
    @PutMapping("/{id}/deactivate")
    public ServiceTypeOutput deactivateServiceType(@PathVariable @NotBlank String id) {
        log.info("Deactivating service type with id: {}", id);
        return deactivateServiceTypeUseCase.execute(id);
    }

    /**
     * 分页查询服务类型
     *
     * @param page        页码（从0开始）
     * @param size        页面大小
     * @param serviceType 服务类型过滤
     * @param nameKeyword 名称关键词
     * @param activeOnly  是否只查询活跃的
     * @return 分页查询结果
     */
    @GetMapping
    public Pageable<ServiceTypePageOutput> getServiceTypes(
        @RequestParam(defaultValue = "0")  Integer page,
        @RequestParam(defaultValue = "20") Integer size,
        @RequestParam(required = false) ServiceType serviceType,
        @RequestParam(required = false) String nameKeyword,
        @RequestParam(required = false) Boolean activeOnly) {

        log.debug("Getting service types with filters: page={}, size={}, serviceType={}, nameKeyword={}, activeOnly={}",
            page, size, serviceType, nameKeyword, activeOnly);

        ServiceTypePageInput request = ServiceTypePageInput.builder()
            .page(page)
            .size(size)
            .serviceType(serviceType)
            .nameKeyword(nameKeyword)
            .activeOnly(activeOnly)
            .build();

        return searchServiceTypesUseCase.execute(request);
    }

    /**
     * 获取所有活跃的服务类型
     *
     * @return 活跃的服务类型列表
     */
    @GetMapping("/active")
    public Pageable<ServiceTypePageOutput> getActiveServiceTypes(
        @RequestParam(defaultValue = "0") Integer page,
        @RequestParam(defaultValue = "20") Integer size) {

        log.debug("Getting active service types: page={}, size={}", page, size);

        ServiceTypePageInput request = ServiceTypePageInput.builder()
            .page(page)
            .size(size)
            .activeOnly(true)
            .build();

        return searchServiceTypesUseCase.execute(request);
    }
}