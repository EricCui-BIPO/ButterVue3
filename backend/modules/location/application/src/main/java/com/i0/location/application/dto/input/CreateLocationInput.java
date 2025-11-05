package com.i0.location.application.dto.input;

import com.i0.location.domain.valueobjects.LocationType;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 创建Location输入DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateLocationInput {

    /**
     * 地理位置名称
     */
    @NotBlank(message = "地理位置名称不能为空")
    @Size(max = 100, message = "地理位置名称不能超过100个字符")
    private String name;

    /**
     * 地理位置类型
     */
    @NotNull(message = "地理位置类型不能为空")
    private LocationType locationType;

    /**
     * ISO代码（支持ISO 3166-1和ISO 3166-2标准，最长10位）
     */
    @Size(max = 10, message = "ISO代码不能超过10个字符")
    private String isoCode;

    /**
     * 地理位置描述（可选）
     */
    @Size(max = 500, message = "地理位置描述不能超过500个字符")
    private String description;

    /**
     * 上级地理位置ID（大洲不需要，其他类型必需）
     */
    private String parentId;

    /**
     * 排序序号（可选）
     */
    private Integer sortOrder;
}