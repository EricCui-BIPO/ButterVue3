package com.i0.service.application.usecases;

import com.i0.service.application.dto.output.ServiceTypeOutput;
import com.i0.service.domain.entities.ServiceTypeEntity;
import com.i0.service.domain.repositories.ServiceTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeactivateServiceTypeUseCase {

    private final ServiceTypeRepository serviceTypeRepository;

    /**
     * 停用服务类型
     * @param id 服务类型ID
     * @return 停用后的服务类型信息
     */
    @Transactional
    public ServiceTypeOutput execute(String id) {
        log.info("Deactivating service type with id: {}", id);

        // 查找服务类型
        ServiceTypeEntity serviceTypeEntity = serviceTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service type not found with id: " + id));

        // 停用服务类型
        serviceTypeEntity.deactivate();

        // 保存更新后的实体
        ServiceTypeEntity updatedEntity = serviceTypeRepository.save(serviceTypeEntity);

        log.info("Service type deactivated successfully with id: {}", id);

        // 转换为输出DTO
        return ServiceTypeOutput.builder()
                .id(updatedEntity.getId())
                .name(updatedEntity.getName())
                .code(updatedEntity.getCode())
                .displayName(updatedEntity.getServiceTypeDisplayName())
                .description(updatedEntity.getDescription())
                .active(updatedEntity.isActive())
                .outsourcingService(updatedEntity.isOutsourcingService())
                .managementService(updatedEntity.isManagementService())
                .createdAt(updatedEntity.getCreatedAt())
                .updatedAt(updatedEntity.getUpdatedAt())
                .version(updatedEntity.getVersion())
                .build();
    }
}