package com.i0.entity.application.dto.input;

import com.i0.entity.domain.valueobjects.EntityType;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 创建Entity输入DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEntityInput {
    
    /**
     * 实体名称
     */
    @NotBlank(message = "实体名称不能为空")
    @Size(max = 100, message = "实体名称不能超过100个字符")
    private String name;
    
    /**
     * 实体类型
     */
    @NotNull(message = "实体类型不能为空")
    private EntityType entityType;
    
    /**
     * 实体代码（可选）
     */
    @Size(max = 50, message = "实体代码不能超过50个字符")
    private String code;
    
    /**
     * 实体描述（可选）
     */
    @Size(max = 500, message = "实体描述不能超过500个字符")
    private String description;
}