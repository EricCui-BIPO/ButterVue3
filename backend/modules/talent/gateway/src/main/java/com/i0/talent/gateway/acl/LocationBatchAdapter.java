package com.i0.talent.gateway.acl;

import com.i0.location.application.dto.output.LocationOutput;
import com.i0.location.application.usecases.GetLocationsBatchUseCase;
import com.i0.talent.domain.exception.DomainException;
import com.i0.talent.domain.valueobjects.WorkLocation;
import com.i0.talent.domain.valueobjects.Nationality;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Location领域适配器 - 真正的批量查询版本
 *
 * 使用GetLocationsBatchUseCase进行真正的批量查询，彻底解决N+1查询问题
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LocationBatchAdapter {

    private final GetLocationsBatchUseCase getLocationsBatchUseCase;

    /**
     * 批量获取工作地点值对象
     *
     * @param locationIds 地点ID列表
     * @return 地点ID到工作地点的映射
     */
    public Map<String, WorkLocation> fetchWorkLocationsBatch(List<String> locationIds) {
        if (locationIds == null || locationIds.isEmpty()) {
            return Collections.emptyMap();
        }

        // 过滤掉空值和重复值
        List<String> validIds = locationIds.stream()
                .filter(StringUtils::hasText)
                .distinct()
                .collect(Collectors.toList());

        if (validIds.isEmpty()) {
            return Collections.emptyMap();
        }

        log.debug("Batch fetching work locations for {} ids", validIds.size());

        // 真正的批量查询 - 一次数据库调用获取所有Location
        Map<String, LocationOutput> locationOutputs = getLocationsBatchUseCase.execute(validIds);

        Map<String, WorkLocation> result = new HashMap<>();

        // 批量转换和验证
        for (Map.Entry<String, LocationOutput> entry : locationOutputs.entrySet()) {
            String locationId = entry.getKey();
            LocationOutput location = entry.getValue();

            try {
                validateWorkLocation(location);

                WorkLocation workLocation = WorkLocation.of(
                    location.getId(),
                    location.getName(),
                    location.getLocationTypeDisplayName() != null ? 
                        location.getLocationTypeDisplayName().toUpperCase() : null,
                    location.getIsoCode()
                );
                result.put(locationId, workLocation);
            } catch (Exception e) {
                log.warn("Failed to convert work location for ID: {}, error: {}", locationId, e.getMessage());
                // 创建默认对象，避免null值
                result.put(locationId, WorkLocation.of(locationId, "Unknown", null));
            }
        }

        // 为未找到的ID创建默认对象
        for (String locationId : validIds) {
            if (!result.containsKey(locationId)) {
                log.warn("WorkLocation not found for ID: {}, creating default", locationId);
                result.put(locationId, WorkLocation.of(locationId, "Unknown", null));
            }
        }

        log.debug("Successfully fetched {} work locations from {} requested", result.size(), validIds.size());
        return result;
    }

    /**
     * 批量获取国籍值对象
     *
     * @param countryIds 国家ID列表
     * @return 国家ID到国籍的映射
     */
    public Map<String, Nationality> fetchNationalitiesBatch(List<String> countryIds) {
        if (countryIds == null || countryIds.isEmpty()) {
            return Collections.emptyMap();
        }

        // 过滤掉空值和重复值
        List<String> validIds = countryIds.stream()
                .filter(StringUtils::hasText)
                .distinct()
                .collect(Collectors.toList());

        if (validIds.isEmpty()) {
            return Collections.emptyMap();
        }

        log.debug("Batch fetching nationalities for {} ids", validIds.size());

        // 真正的批量查询 - 一次数据库调用获取所有Country
        Map<String, LocationOutput> locationOutputs = getLocationsBatchUseCase.execute(validIds);

        Map<String, Nationality> result = new HashMap<>();

        // 批量转换和验证
        for (Map.Entry<String, LocationOutput> entry : locationOutputs.entrySet()) {
            String countryId = entry.getKey();
            LocationOutput country = entry.getValue();

            try {
                validateNationality(country);

                Nationality nationality = Nationality.of(
                    country.getId(),
                    country.getName(),
                    country.getIsoCode()
                );
                result.put(countryId, nationality);
            } catch (Exception e) {
                log.warn("Failed to convert nationality for ID: {}, error: {}", countryId, e.getMessage());
                // 创建默认对象，避免null值
                result.put(countryId, Nationality.of(countryId, "Unknown", null));
            }
        }

        // 为未找到的ID创建默认对象
        for (String countryId : validIds) {
            if (!result.containsKey(countryId)) {
                log.warn("Nationality not found for ID: {}, creating default", countryId);
                result.put(countryId, Nationality.of(countryId, "Unknown", null));
            }
        }

        log.debug("Successfully fetched {} nationalities from {} requested", result.size(), validIds.size());
        return result;
    }

    /**
     * 验证地点是否可以作为工作地点
     */
    private void validateWorkLocation(LocationOutput location) {
        if (location == null) {
            throw new DomainException("工作地点信息不能为空");
        }

        String locationTypeDisplayName = location.getLocationTypeDisplayName();
        if (locationTypeDisplayName != null &&
            !"Country".equals(locationTypeDisplayName) &&
            !"Province".equals(locationTypeDisplayName) &&
            !"City".equals(locationTypeDisplayName)) {
            throw new DomainException(
                String.format("工作地点类型不支持: %s，仅支持国家、省份、城市", locationTypeDisplayName)
            );
        }

        if (!location.getActive()) {
            throw new DomainException("工作地点已停用: " + location.getName());
        }
    }

    /**
     * 验证地点是否可以作为国籍
     */
    private void validateNationality(LocationOutput location) {
        if (location == null) {
            throw new DomainException("国籍国家信息不能为空");
        }

        String locationTypeDisplayName = location.getLocationTypeDisplayName();
        if (!"Country".equals(locationTypeDisplayName)) {
            throw new DomainException(
                String.format("国籍只能选择国家，当前地点类型为: %s", locationTypeDisplayName)
            );
        }

        if (!location.getActive()) {
            throw new DomainException("国籍国家已停用: " + location.getName());
        }
    }
}