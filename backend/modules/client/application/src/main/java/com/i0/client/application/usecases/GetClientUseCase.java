package com.i0.client.application.usecases;

import com.i0.client.application.dto.output.ClientOutput;
import com.i0.client.application.dto.output.ClientDetailOutput;
import com.i0.client.domain.entities.Client;
import com.i0.client.domain.repositories.ClientRepository;
import com.i0.client.domain.exceptions.ClientNotFoundException;
import com.i0.client.domain.services.LocationQueryService;
import com.i0.client.domain.valueobjects.LocationInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 获取客户用例
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GetClientUseCase {

    private final ClientRepository clientRepository;
    private final LocationQueryService locationQueryService;
    
    /**
     * 根据ID获取客户
     * @param id 客户ID
     * @return 客户详情输出
     */
    @Transactional(readOnly = true)
    public ClientDetailOutput getById(String id) {
        log.debug("Getting client with ID: {}", id);

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> ClientNotFoundException.byId(id));

        log.debug("Client found: {}", client.getName());

        return enrichClientWithLocationInfo(client);
    }
    
    /**
     * 根据名称获取客户
     * @param name 客户名称
     * @return 客户详情输出
     */
    @Transactional(readOnly = true)
    public ClientDetailOutput getByName(String name) {
        log.debug("Getting client with name: {}", name);

        Client client = clientRepository.findByName(name)
                .orElseThrow(() -> ClientNotFoundException.byField("name", name));

        log.debug("Client found: {}", client.getName());

        return enrichClientWithLocationInfo(client);
    }
    
    /**
     * 根据代码获取客户
     * @param code 客户代码
     * @return 客户详情输出
     */
    @Transactional(readOnly = true)
    public ClientDetailOutput getByCode(String code) {
        log.debug("Getting client with code: {}", code);

        Client client = clientRepository.findByCode(code)
                .orElseThrow(() -> ClientNotFoundException.byField("code", code));

        log.debug("Client found: {}", client.getName());

        return enrichClientWithLocationInfo(client);
    }

    /**
     * 为客户添加位置信息
     * @param client 客户实体
     * @return 包含位置信息的客户详情输出DTO
     */
    private ClientDetailOutput enrichClientWithLocationInfo(Client client) {
        String locationId = client.getLocationId();
        if (locationId == null || locationId.isEmpty()) {
            return ClientDetailOutput.from(client);
        }

        try {
            LocationInfo locationInfo = locationQueryService.getLocation(locationId);
            String locationName = locationInfo != null ? locationInfo.getDisplayName() : null;
            String locationCode = locationInfo != null ? locationInfo.getCode() : null;
            return ClientDetailOutput.from(client, locationName, locationCode);
        } catch (Exception e) {
            log.warn("Failed to get location info for location ID: {}", locationId, e);
            return ClientDetailOutput.from(client);
        }
    }
}