package com.i0.location.application.dto.output;

import com.i0.location.domain.entities.Location;
import com.i0.location.domain.valueobjects.LocationType;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

/**
 * Location输出DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationOutput {

    /**
     * 地理位置ID
     */
    private String id;

    /**
     * 地理位置名称
     */
    private String name;

    /**
     * 地理位置类型
     */
    private LocationType locationType;

    /**
     * 地理位置类型显示名称
     */
    private String locationTypeDisplayName;

    /**
     * 地理位置类型中文名称
     */
    private String locationTypeChineseName;

    /**
     * ISO代码
     */
    private String isoCode;

    /**
     * 地理位置描述
     */
    private String description;

    /**
     * 上级地理位置ID
     */
    private String parentId;

    /**
     * 层级深度
     */
    private Integer level;

    /**
     * 排序序号
     */
    private Integer sortOrder;

    /**
     * 是否激活
     */
    private Boolean active;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 版本号
     */
    private Long version;

    /**
     * 子级地理位置列表（用于树形结构）
     */
    private List<LocationOutput> children;

    /**
     * 从Location实体转换为LocationOutput
     * @param location Location实体
     * @return LocationOutput
     */
    public static LocationOutput from(Location location) {
        if (location == null) {
            return null;
        }

        return LocationOutput.builder()
                .id(location.getId())
                .name(location.getName())
                .locationType(location.getLocationType())
                .locationTypeDisplayName(location.getLocationType() != null ? location.getLocationType().getDisplayName() : null)
                .locationTypeChineseName(location.getLocationType() != null ? location.getLocationType().getChineseName() : null)
                .isoCode(location.getIsoCode())
                .description(location.getDescription())
                .parentId(location.getParentId())
                .level(location.getLevel())
                .sortOrder(location.getSortOrder())
                .active(location.getActive())
                .createdAt(location.getCreatedAt())
                .updatedAt(location.getUpdatedAt())
                .version(location.getVersion())
                .children(new ArrayList<>())
                .build();
    }
}