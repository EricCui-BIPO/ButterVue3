package com.i0.service.application.dto.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 更新服务类型输入DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateServiceTypeInput {

    @NotBlank(message = "Service type name cannot be empty")
    @Size(max = 100, message = "Service type name cannot exceed 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
}