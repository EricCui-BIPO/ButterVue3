package com.i0.client.gateway.web.controllers;

import com.i0.client.application.dto.input.ClientPageInput;
import com.i0.client.application.dto.input.CreateClientInput;
import com.i0.client.application.dto.input.UpdateClientInput;
import com.i0.client.application.dto.output.ClientDetailOutput;
import com.i0.client.application.dto.output.ClientLocationOutput;
import com.i0.client.application.dto.output.ClientOutput;
import com.i0.client.application.usecases.*;
import com.i0.domain.core.pagination.Pageable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * Client REST控制器
 * 提供Client相关的CRUD API
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
@Validated
public class ClientController {

    private final CreateClientUseCase createClientUseCase;
    private final GetClientUseCase getClientUseCase;
    private final UpdateClientUseCase updateClientUseCase;
    private final DeleteClientUseCase deleteClientUseCase;
    private final ActivateClientUseCase activateClientUseCase;
    private final DeactivateClientUseCase deactivateClientUseCase;
    private final SearchClientsUseCase searchClientsUseCase;
    private final GetActiveClientsUseCase getActiveClientsUseCase;

    /**
     * 创建客户
     *
     * @param request 创建请求
     * @return 创建的客户响应
     */
    @PostMapping
    public ClientOutput createClient(@Valid @RequestBody CreateClientInput request) {
        log.info("Creating client with name: {}", request.getName());
        return createClientUseCase.execute(request);
    }

    /**
     * 根据ID获取客户
     *
     * @param id 客户ID
     * @return 客户详情响应
     */
    @GetMapping("/{id}")
    public ClientDetailOutput getClient(@PathVariable @NotBlank String id) {
        log.debug("Getting client by id: {}", id);
        return getClientUseCase.getById(id);
    }

    /**
     * 根据名称获取客户
     *
     * @param name 客户名称
     * @return 客户详情响应
     */
    @GetMapping("/by-name/{name}")
    public ClientDetailOutput getClientByName(@PathVariable @NotBlank String name) {
        log.debug("Getting client by name: {}", name);
        return getClientUseCase.getByName(name);
    }

    /**
     * 根据代码获取客户
     *
     * @param code 客户代码
     * @return 客户详情响应
     */
    @GetMapping("/by-code/{code}")
    public ClientDetailOutput getClientByCode(@PathVariable @NotBlank String code) {
        log.debug("Getting client by code: {}", code);
        return getClientUseCase.getByCode(code);
    }

    /**
     * 更新客户
     *
     * @param id      客户ID
     * @param request 更新请求
     * @return 更新后的客户响应
     */
    @PutMapping("/{id}")
    public ClientOutput updateClient(
        @PathVariable @NotBlank String id,
        @Valid @RequestBody UpdateClientInput request) {
        log.info("Updating client with id: {}", id);
        return updateClientUseCase.execute(id, request);
    }

    /**
     * 激活客户
     *
     * @param id 客户ID
     * @return 更新后的客户响应
     */
    @PutMapping("/{id}/activate")
    public ClientOutput activateClient(@PathVariable @NotBlank String id) {
        log.info("Activating client with id: {}", id);
        return activateClientUseCase.execute(id);
    }

    /**
     * 停用客户
     *
     * @param id 客户ID
     * @return 更新后的客户响应
     */
    @PutMapping("/{id}/deactivate")
    public ClientOutput deactivateClient(@PathVariable @NotBlank String id) {
        log.info("Deactivating client with id: {}", id);
        return deactivateClientUseCase.execute(id);
    }

    /**
     * 删除客户
     *
     * @param id 客户ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public void deleteClient(@PathVariable @NotBlank String id) {
        log.info("Deleting client with id: {}", id);
        deleteClientUseCase.execute(id);
    }


    /**
     * 分页查询客户
     *
     * @param page        页码（从0开始）
     * @param size        每页大小
     * @param q           搜索关键字（用于搜索名称、代码、别名）
     * @param locationId  位置ID过滤（可选）
     * @param activeOnly   是否只查询激活的客户（可选，默认null）
     * @param sortBy      排序字段（可选）
     * @param sortOrder   排序方向（可选）
     * @return 分页响应
     */
    @GetMapping
    public Pageable<ClientDetailOutput> getClients(
        @RequestParam(defaultValue = "0") Integer page,
        @RequestParam(defaultValue = "20") Integer size,
        @RequestParam(required = false) String q,
        @RequestParam(required = false) String locationId,
        @RequestParam(required = false) Boolean activeOnly,
        @RequestParam(required = false) String sortBy,
        @RequestParam(required = false) String sortOrder) {

        log.debug("Getting clients with page: {}, size: {}, q: {}, locationId: {}, activeOnly: {}, sortBy: {}, sortOrder: {}",
            page, size, q, locationId, activeOnly, sortBy, sortOrder);

        ClientPageInput request = ClientPageInput.builder()
            .page(page)
            .size(size)
            .keyword(q)
            .locationId(locationId)
            .activeOnly(activeOnly)
            .sortBy(sortBy)
            .sortDirection(sortOrder)
            .build();

        return searchClientsUseCase.execute(request);
    }

    /**
     * 获取所有激活的客户
     *
     * @return 客户列表
     */
    @GetMapping("/active")
    public List<ClientDetailOutput> getActiveClients() {
        log.debug("Getting all active clients");
        return getActiveClientsUseCase.execute();
    }

    /**
     * 根据位置ID获取激活的客户
     *
     * @param locationId 位置ID
     * @return 客户列表
     */
    @GetMapping("/active/by-location/{locationId}")
    public List<ClientDetailOutput> getActiveClientsByLocation(@PathVariable @NotBlank String locationId) {
        log.debug("Getting active clients by location: {}", locationId);
        return getActiveClientsUseCase.executeByLocationId(locationId);
    }

    /**
     * 获取可用于客户选择的位置列表
     *
     * @return 位置列表
     */
    @GetMapping("/locations")
    public List<ClientLocationOutput> getClientLocations() {
        log.debug("Getting available locations for client selection");
        return getActiveClientsUseCase.getClientLocations();
    }
}