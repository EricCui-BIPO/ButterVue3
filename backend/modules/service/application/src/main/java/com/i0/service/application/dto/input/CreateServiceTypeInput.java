package com.i0.service.application.dto.input;

import com.i0.service.domain.valueobjects.ServiceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 创建服务类型输入DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateServiceTypeInput {

    @NotBlank(message = "Service type name cannot be empty")
    @Size(max = 100, message = "Service type name cannot exceed 100 characters")
    private String name;

    @NotNull(message = "Service type cannot be null")
    private ServiceType serviceType;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
}