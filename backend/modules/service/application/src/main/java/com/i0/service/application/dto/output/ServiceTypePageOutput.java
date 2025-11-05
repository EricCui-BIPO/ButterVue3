package com.i0.service.application.dto.output;

import com.i0.service.domain.entities.ServiceTypeEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 服务类型分页输出DTO
 * 用于分页查询时返回单个服务类型信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceTypePageOutput {

    private String id;
    private String name;
    private String code;
    private String displayName;
    private String description;
    private Boolean active;
    private Boolean outsourcingService;
    private Boolean managementService;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    /**
     * 从领域实体创建分页输出DTO
     * @param entity 服务类型实体
     * @return 分页输出DTO
     */
    public static ServiceTypePageOutput from(ServiceTypeEntity entity) {
        if (entity == null) {
            return null;
        }

        return ServiceTypePageOutput.builder()
                .id(entity.getId())
                .name(entity.getName())
                .code(entity.getCode())
                .displayName(entity.getServiceTypeDisplayName())
                .description(entity.getDescription())
                .active(entity.isActive())
                .outsourcingService(entity.isOutsourcingService())
                .managementService(entity.isManagementService())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}