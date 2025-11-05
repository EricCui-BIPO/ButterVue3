package com.i0.client.application.dto.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 更新客户输入DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateClientInput {
    
    /**
     * 客户名称
     */
    @NotBlank(message = "Client name cannot be blank")
    @Size(max = 100, message = "Client name cannot exceed 100 characters")
    private String name;
    
    /**
     * 客户代码（可选，如果提供则会更新客户代码）
     */
    @Size(max = 50, message = "Client code cannot exceed 50 characters")
    private String code;
    
    /**
     * 客户别名
     */
    @Size(max = 100, message = "Client alias name cannot exceed 100 characters")
    private String aliasName;
    
    /**
     * 位置ID
     */
    @NotNull(message = "Location ID cannot be null")
    @NotBlank(message = "Location ID cannot be blank")
    private String locationId;
    
    /**
     * 描述
     */
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
}