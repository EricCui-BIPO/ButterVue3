package com.i0.entity.application.dto.input;

import com.i0.entity.domain.valueobjects.EntityType;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.Max;

/**
 * Entity分页查询输入DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityPageInput {
    
    /**
     * 页码（从0开始）
     */
    @Min(value = 0, message = "页码不能小于0")
    @Builder.Default
    private Integer page = 0;
    
    /**
     * 每页大小
     */
    @Min(value = 1, message = "每页大小不能小于1")
    @Max(value = 100, message = "每页大小不能超过100")
    @Builder.Default
    private Integer size = 20;
    
    /**
     * 实体类型过滤（可选）
     */
    private EntityType entityType;
    
    /**
     * 名称关键字搜索（可选）
     */
    private String nameKeyword;
    
    /**
     * 是否只查询激活的实体
     * null = 不限制（返回所有实体）
     * true = 只返回激活的实体
     * false = 只返回未激活的实体
     */
    private Boolean activeOnly;
}