package com.i0.service.application.usecases;

import com.i0.service.application.dto.input.UpdateServiceTypeInput;
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
public class UpdateServiceTypeUseCase {

    private final ServiceTypeRepository serviceTypeRepository;

    /**
     * 更新服务类型
     * @param id 服务类型ID
     * @param input 更新服务类型输入
     * @return 更新后的服务类型输出
     */
    @Transactional
    public ServiceTypeOutput execute(String id, UpdateServiceTypeInput input) {
        log.info("Updating service type with id: {}", id);

        // 查找服务类型
        ServiceTypeEntity serviceTypeEntity = serviceTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service type not found with id: " + id));

        // 业务验证
        if (serviceTypeRepository.existsByNameExcludingId(input.getName(), id)) {
            throw new IllegalArgumentException("Service type name already exists: " + input.getName());
        }

        // 更新服务类型
        serviceTypeEntity.update(input.getName(), input.getDescription());

        // 保存更新
        ServiceTypeEntity updatedEntity = serviceTypeRepository.save(serviceTypeEntity);

        log.info("Service type updated successfully with id: {}", updatedEntity.getId());
        return ServiceTypeOutput.from(updatedEntity);
    }
}