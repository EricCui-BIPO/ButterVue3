package com.i0.location.application.dto.input;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 更新Location输入DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLocationInput {

    /**
     * 地理位置名称
     */
    @NotBlank(message = "地理位置名称不能为空")
    @Size(max = 100, message = "地理位置名称不能超过100个字符")
    private String name;

    /**
     * 地理位置描述（可选）
     */
    @Size(max = 500, message = "地理位置描述不能超过500个字符")
    private String description;

    /**
     * 上级地理位置ID（可选）
     */
    private String parentId;

    /**
     * 排序序号（可选）
     */
    private Integer sortOrder;
}