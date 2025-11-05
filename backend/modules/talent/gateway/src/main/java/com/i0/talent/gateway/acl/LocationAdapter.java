package com.i0.talent.gateway.acl;

import com.i0.location.application.dto.output.LocationOutput;
import com.i0.location.application.usecases.GetLocationUseCase;
import com.i0.talent.domain.exception.DomainException;
import com.i0.talent.domain.valueobjects.WorkLocation;
import com.i0.talent.domain.valueobjects.Nationality;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Location领域适配器
 *
 * 负责Employee领域与Location领域之间的交互，遵循ACL防腐层设计原则
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LocationAdapter {

    private final GetLocationUseCase getLocationUseCase;

    /**
     * 根据地点ID获取工作地点值对象
     *
     * @param locationId 地点ID
     * @return 工作地点值对象
     * @throws DomainException 当地点不存在或类型不支持时抛出
     */
    public WorkLocation fetchWorkLocation(String locationId) {
        log.debug("Fetching work location with id: {}", locationId);

        try {
            LocationOutput location = getLocationUseCase.execute(locationId);
            validateWorkLocation(location);

            return WorkLocation.of(
                location.getId(),
                location.getName(),
                location.getLocationTypeDisplayName() != null ? 
                    location.getLocationTypeDisplayName().toUpperCase() : null,
                location.getIsoCode()
            );
        } catch (IllegalArgumentException e) {
            throw new DomainException("工作地点不存在: " + locationId, e);
        }
    }

    /**
     * 根据国家ID获取国籍值对象
     *
     * @param countryId 国家ID
     * @return 国籍值对象
     * @throws DomainException 当国家不存在时抛出
     */
    public Nationality fetchNationality(String countryId) {
        log.debug("Fetching nationality with country id: {}", countryId);

        try {
            LocationOutput country = getLocationUseCase.execute(countryId);
            validateNationality(country);

            return Nationality.of(
                country.getId(),
                country.getName(),
                country.getIsoCode()
            );
        } catch (IllegalArgumentException e) {
            throw new DomainException("国籍国家不存在: " + countryId, e);
        }
    }

    /**
     * 验证地点是否可以作为工作地点
     *
     * @param location 地点信息
     * @throws DomainException 当地点类型不支持时抛出
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
     *
     * @param location 地点信息
     * @throws DomainException 当地点不是国家类型时抛出
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

    /**
     * 验证工作地点ID是否有效
     *
     * @param locationId 地点ID
     * @return 是否有效
     */
    public boolean isValidWorkLocation(String locationId) {
        try {
            fetchWorkLocation(locationId);
            return true;
        } catch (DomainException e) {
            log.debug("Invalid work location: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 验证国籍ID是否有效
     *
     * @param countryId 国家ID
     * @return 是否有效
     */
    public boolean isValidNationality(String countryId) {
        try {
            fetchNationality(countryId);
            return true;
        } catch (DomainException e) {
            log.debug("Invalid nationality: {}", e.getMessage());
            return false;
        }
    }
}