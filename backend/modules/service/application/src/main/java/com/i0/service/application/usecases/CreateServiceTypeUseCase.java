package com.i0.service.application.usecases;

import com.i0.service.application.dto.input.CreateServiceTypeInput;
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
public class CreateServiceTypeUseCase {

    private final ServiceTypeRepository serviceTypeRepository;

    /**
     * 创建服务类型
     * @param input 创建服务类型输入
     * @return 创建的服务类型输出
     */
    @Transactional
    public ServiceTypeOutput execute(CreateServiceTypeInput input) {
        log.info("Creating service type with name: {}, code: {}", input.getName(), input.getServiceType());

        // 业务验证
        if (serviceTypeRepository.existsByName(input.getName())) {
            throw new IllegalArgumentException("Service type name already exists: " + input.getName());
        }

        if (serviceTypeRepository.existsByServiceType(input.getServiceType())) {
            throw new IllegalArgumentException("Service type code already exists: " + input.getServiceType().getCode());
        }

        // 创建服务类型实体
        ServiceTypeEntity serviceTypeEntity = ServiceTypeEntity.create(
                input.getName(),
                input.getServiceType(),
                input.getDescription()
        );

        // 保存服务类型
        ServiceTypeEntity savedEntity = serviceTypeRepository.save(serviceTypeEntity);

        log.info("Service type created successfully with id: {}", savedEntity.getId());
        return ServiceTypeOutput.from(savedEntity);
    }
}