package com.i0.client.gateway.acl;

import com.i0.client.domain.services.LocationQueryService;
import com.i0.client.domain.valueobjects.LocationInfo;
import com.i0.client.gateway.acl.converter.LocationConverter;
import com.i0.location.application.dto.output.LocationOutput;
import com.i0.location.application.usecases.GetLocationUseCase;
import com.i0.location.application.usecases.GetLocationsByIdsUseCase;
import com.i0.location.application.usecases.GetLocationsByTypeUseCase;
import com.i0.location.domain.valueobjects.LocationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 位置信息查询的ACL适配器
 * 实现客户端域定义的位置查询接口，通过适配器模式隔离对外部域的直接依赖
 */
@Component
@RequiredArgsConstructor
public class LocationQueryACLAdapter implements LocationQueryService {

    private final GetLocationUseCase getLocationUseCase;
    private final GetLocationsByIdsUseCase getLocationsByIdsUseCase;
    private final GetLocationsByTypeUseCase getLocationsByTypeUseCase;
    private final LocationConverter locationConverter;

    @Override
    public List<LocationInfo> getLocations(Iterable<String> locationIds) {
        if (locationIds == null) {
            return Collections.emptyList();
        }

        List<LocationOutput> locationOutputs = getLocationsByIdsUseCase.execute(locationIds);
        return locationConverter.toLocationInfoList(locationOutputs);
    }

    @Override
    public LocationInfo getLocation(String locationId) {
        if (locationId == null || locationId.trim().isEmpty()) {
            return null;
        }

        LocationOutput locationOutput = getLocationUseCase.execute(locationId);
        return locationOutput != null ? locationConverter.toLocationInfo(locationOutput) : null;
    }

    
    @Override
    public List<LocationInfo> getCountryLocations() {
        List<LocationOutput> countries = getLocationsByTypeUseCase.execute(LocationType.COUNTRY);
        return locationConverter.toLocationInfoList(countries);
    }

    }