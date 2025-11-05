package com.i0.client.application.usecases;

import com.i0.client.application.dto.output.ClientOutput;
import com.i0.client.application.dto.output.ClientDetailOutput;
import com.i0.client.application.dto.output.ClientLocationOutput;
import com.i0.client.domain.entities.Client;
import com.i0.client.domain.repositories.ClientRepository;
import com.i0.client.domain.services.LocationQueryService;
import com.i0.client.domain.valueobjects.LocationInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 获取激活客户用例
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GetActiveClientsUseCase {

    private final ClientRepository clientRepository;
    private final LocationQueryService locationQueryService;
    
    /**
     * 获取所有激活的客户
     * @return 激活的客户列表
     */
    @Transactional(readOnly = true)
    public List<ClientDetailOutput> execute() {
        log.debug("Getting all active clients");

        List<Client> activeClients = clientRepository.findAllActive();

        log.debug("Found {} active clients", activeClients.size());

        return enrichClientsWithLocationInfo(activeClients);
    }
    
    /**
     * 根据位置ID获取激活的客户
     * @param locationId 位置ID
     * @return 激活的客户列表
     */
    @Transactional(readOnly = true)
    public List<ClientDetailOutput> executeByLocationId(String locationId) {
        log.debug("Getting active clients for location ID: {}", locationId);

        List<Client> activeClients = clientRepository.findActiveByLocationId(locationId);

        log.debug("Found {} active clients for location ID: {}", activeClients.size(), locationId);

        return enrichClientsWithLocationInfo(activeClients);
    }

  /**
     * 为客户列表添加位置信息
     * @param clients 客户列表
     * @return 包含位置信息的客户详情输出DTO列表
     */
    private List<ClientDetailOutput> enrichClientsWithLocationInfo(List<Client> clients) {
        if (clients.isEmpty()) {
            return List.of();
        }

        // 收集所有需要查询的位置ID
        Set<String> locationIds = clients.stream()
                .map(Client::getLocationId)
                .filter(locationId -> locationId != null && !locationId.isEmpty())
                .collect(Collectors.toSet());

        // 批量获取位置信息
        List<LocationInfo> locationInfos = locationQueryService.getLocations(locationIds);

        // 转换为Map便于查找
        Map<String, LocationInfo> locationInfoMap = locationInfos.stream()
                .collect(Collectors.toMap(
                        LocationInfo::getId,
                        locationInfo -> locationInfo,
                        (existing, replacement) -> existing));

        // 转换为输出DTO并添加位置信息
        return clients.stream()
                .map(client -> {
                    LocationInfo locationInfo = locationInfoMap.get(client.getLocationId());
                    String locationName = locationInfo != null ? locationInfo.getDisplayName() : null;
                    String locationCode = locationInfo != null ? locationInfo.getCode() : null;
                    return ClientDetailOutput.from(client, locationName, locationCode);
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取可用于客户选择的位置列表
     * 只返回国家级别的位置供客户选择
     * @return 位置列表
     */
    @Transactional(readOnly = true)
    public List<ClientLocationOutput> getClientLocations() {
        log.debug("Getting available locations for client selection");

        try {
            // 获取所有激活的国家位置
            List<LocationInfo> countryLocations = locationQueryService.getCountryLocations();

            List<ClientLocationOutput> locations = countryLocations.stream()
                    .filter(location -> location != null && location.getId() != null)
                    .map(location -> ClientLocationOutput.of(
                            location.getId(),
                            location.getDisplayName(),
                            location.getCode()
                    ))
                    .collect(Collectors.toList());

            log.debug("Found {} available locations", locations.size());
            return locations;
        } catch (Exception e) {
            log.error("Failed to get client locations", e);
            return List.of();
        }
    }
}