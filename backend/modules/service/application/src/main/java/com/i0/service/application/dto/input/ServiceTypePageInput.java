package com.i0.service.application.dto.input;

import com.i0.service.domain.valueobjects.ServiceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 服务类型分页查询输入DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceTypePageInput {

    @Builder.Default
    @Min(value = 0, message = "Page number cannot be negative")
    private Integer page = 0;

    @Builder.Default
    @Min(value = 1, message = "Page size must be at least 1")
    private Integer size = 20;

    private ServiceType serviceType;

    private String nameKeyword;

    private Boolean activeOnly;
}