package com.i0.location.application.dto.input;

import com.i0.location.domain.valueobjects.LocationType;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.Max;

/**
 * Location分页查询输入DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationPageInput {

    /**
     * 名称关键字（可选）
     */
    private String name;

    /**
     * 地理位置类型（可选）
     */
    private LocationType locationType;

    /**
     * 上级地理位置ID（可选）
     */
    private String parentId;

    /**
     * 是否只查询激活状态（可选，null表示不限制）
     */
    private Boolean activeOnly;

    /**
     * 页码（从0开始）
     */
    @Builder.Default
    @Min(value = 0, message = "页码不能小于0")
    private Integer page = 0;

    /**
     * 每页大小
     */
    @Builder.Default
    @Min(value = 1, message = "每页大小不能小于1")
    @Max(value = 100, message = "每页大小不能超过100")
    private Integer size = 20;
}