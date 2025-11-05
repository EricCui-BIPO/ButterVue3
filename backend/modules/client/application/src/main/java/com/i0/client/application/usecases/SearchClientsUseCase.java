package com.i0.client.application.usecases;

import com.i0.client.application.dto.input.ClientPageInput;
import com.i0.client.application.dto.output.ClientDetailOutput;
import com.i0.client.domain.entities.Client;
import com.i0.client.domain.repositories.ClientRepository;
import com.i0.client.domain.services.LocationQueryService;
import com.i0.client.domain.valueobjects.LocationInfo;
import com.i0.domain.core.pagination.Pageable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 搜索客户用例
 * 负责分页查询客户的业务逻辑
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SearchClientsUseCase {

    private final ClientRepository clientRepository;
    private final LocationQueryService locationQueryService;

    /**
     * 执行搜索客户用例
     *
     * @param input 分页查询请求
     * @return 分页查询结果
     */
    @Transactional(readOnly = true)
    public Pageable<ClientDetailOutput> execute(ClientPageInput input) {
        log.info("Searching clients with page: {}, size: {}, keyword: {}, locationId: {}, activeOnly: {}",
            input.getPage(), input.getSize(), input.getKeyword(), input.getLocationId(), input.getActiveOnly());

        // 业务逻辑：验证请求参数
        validateSearchRequest(input);

        // 业务逻辑：标准化搜索条件
        String normalizedKeyword = normalizeKeyword(input.getKeyword());

        // 委托给repository层进行数据查询
        Pageable<Client> clientPage = clientRepository.searchClients(
            normalizedKeyword,
            input.getLocationId(),
            input.getActiveOnly(),
            input.getPage(),
            input.getSize(),
            input.getSortBy(),
            input.getSortDirection()
        );

        log.info("Found {} clients (page {} of {}, total {})",
            clientPage.getNumberOfElements(),
            clientPage.getPage() + 1,
            clientPage.getTotalPages(),
            clientPage.getTotal());

        // 转换为输出DTO，包含位置名称
        return enrichClientsWithLocationNames(clientPage);
    }

    /**
     * 为客户列表添加位置信息
     *
     * @param clientPage 客户分页数据
     * @return 包含位置信息的客户详情输出DTO分页数据
     */
    private Pageable<ClientDetailOutput> enrichClientsWithLocationNames(Pageable<Client> clientPage) {
        // 收集所有需要查询的位置ID
        java.util.Set<String> locationIds = new java.util.HashSet<>();
        for (Client client : clientPage.getContent()) {
            String locationId = client.getLocationId();
            if (locationId != null && !locationId.isEmpty()) {
                locationIds.add(locationId);
            }
        }

        // 批量获取位置信息
        List<LocationInfo> locationInfos = locationQueryService.getLocations(locationIds);

        // 转换为Map便于查找
        java.util.Map<String, LocationInfo> locationInfoMap = locationInfos.stream()
            .collect(java.util.stream.Collectors.toMap(
                LocationInfo::getId,
                locationInfo -> locationInfo,
                (existing, replacement) -> existing));

        // 转换为输出DTO并添加位置信息
        return clientPage.map(client -> {
            LocationInfo locationInfo = locationInfoMap.get(client.getLocationId());
            String locationName = locationInfo != null ? locationInfo.getDisplayName() : null;
            String locationCode = locationInfo != null ? locationInfo.getCode() : null;
            return ClientDetailOutput.from(client, locationName, locationCode);
        });
    }

    /**
     * 验证搜索请求参数
     * 业务逻辑：确保请求参数的有效性
     */
    private void validateSearchRequest(ClientPageInput input) {
        if (input.getPage() < 0) {
            throw new IllegalArgumentException("页码不能为负数");
        }
        if (input.getSize() <= 0) {
            throw new IllegalArgumentException("每页大小必须大于0");
        }
        if (input.getSize() > 1000) {
            throw new IllegalArgumentException("每页大小不能超过1000");
        }
    }

    /**
     * 标准化关键字
     * 业务逻辑：对搜索关键字进行标准化处理
     */
    private String normalizeKeyword(String keyword) {
        if (keyword == null) {
            return null;
        }
        String normalized = keyword.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}