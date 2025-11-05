package com.i0.client.gateway.acl.converter;

import com.i0.client.domain.valueobjects.LocationInfo;
import com.i0.location.application.dto.output.LocationOutput;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 位置数据转换器
 * 专门负责LocationOutput和LocationInfo之间的转换
 */
@Component
public class LocationConverter {

    /**
     * 将LocationOutput转换为LocationInfo
     */
    public LocationInfo toLocationInfo(LocationOutput locationOutput) {
        if (locationOutput == null) {
            return null;
        }

        return LocationInfo.builder()
                .id(locationOutput.getId())
                .name(locationOutput.getName())
                .code(locationOutput.getIsoCode())
                .type(locationOutput.getLocationType() != null ?
                      locationOutput.getLocationType().name() : null)
                .parentId(locationOutput.getParentId())
                .description(locationOutput.getDescription())
                .active(locationOutput.getActive())
                .createdAt(locationOutput.getCreatedAt())
                .updatedAt(locationOutput.getUpdatedAt())
                .build();
    }

    /**
     * 批量转换LocationOutput为LocationInfo
     */
    public List<LocationInfo> toLocationInfoList(List<LocationOutput> locationOutputs) {
        if (locationOutputs == null || locationOutputs.isEmpty()) {
            return List.of();
        }

        return locationOutputs.stream()
                .map(this::toLocationInfo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}