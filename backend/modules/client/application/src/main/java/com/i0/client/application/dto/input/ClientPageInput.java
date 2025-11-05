package com.i0.client.application.dto.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

/**
 * 客户分页查询输入DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientPageInput {
    
    /**
     * 页码（从0开始）
     */
    @Min(value = 0, message = "Page number must be non-negative")
    @Builder.Default
    private Integer page = 0;
    
    /**
     * 每页大小
     */
    @Min(value = 1, message = "Page size must be positive")
    @Builder.Default
    private Integer size = 20;
    
    /**
     * 搜索关键字（用于搜索名称、代码、别名）
     */
    private String keyword;
    
    /**
     * 位置ID
     */
    private String locationId;
    
    /**
     * 是否只查询激活状态的客户
     */
    private Boolean activeOnly;
    
    /**
     * 排序字段
     */
    private String sortBy;
    
    /**
     * 排序方向（ASC或DESC）
     */
    @Pattern(regexp = "^(ASC|DESC)$", message = "Sort direction must be ASC or DESC")
    private String sortDirection;
    
    /**
     * 获取排序字段，如果为空则返回默认值
     */
    public String getSortBy() {
        return sortBy != null && !sortBy.trim().isEmpty() ? sortBy : "createdAt";
    }
    
    /**
     * 获取排序方向，如果为空则返回默认值
     */
    public String getSortDirection() {
        return sortDirection != null && !sortDirection.trim().isEmpty() ? sortDirection : "DESC";
    }
}