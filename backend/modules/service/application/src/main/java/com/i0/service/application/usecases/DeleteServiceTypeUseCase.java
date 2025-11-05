package com.i0.service.application.usecases;

import com.i0.service.domain.entities.ServiceTypeEntity;
import com.i0.service.domain.repositories.ServiceTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteServiceTypeUseCase {

    private final ServiceTypeRepository serviceTypeRepository;

    /**
     * 删除服务类型（逻辑删除）
     * @param id 服务类型ID
     */
    @Transactional
    public void execute(String id) {
        log.info("Deleting service type with id: {}", id);

        // 查找服务类型
        ServiceTypeEntity serviceTypeEntity = serviceTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service type not found with id: " + id));

        // 执行逻辑删除
        serviceTypeRepository.delete(serviceTypeEntity);

        log.info("Service type deleted successfully with id: {}", id);
    }
}