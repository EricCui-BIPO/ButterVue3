package com.i0.service.application.dto.output;

import com.i0.service.domain.entities.ServiceTypeEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 服务类型输出DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceTypeOutput {

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

    private Long version;

    /**
     * 从领域实体创建输出DTO
     * @param entity 服务类型实体
     * @return 输出DTO
     */
    public static ServiceTypeOutput from(ServiceTypeEntity entity) {
        if (entity == null) {
            return null;
        }

        return ServiceTypeOutput.builder()
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
                .version(entity.getVersion())
                .build();
    }
}