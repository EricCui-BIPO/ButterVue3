package com.i0.service.application.usecases;

import com.i0.service.application.dto.output.ServiceTypeOutput;
import com.i0.service.domain.repositories.ServiceTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class GetServiceTypeUseCase {

    private final ServiceTypeRepository serviceTypeRepository;

    /**
     * 根据ID获取服务类型
     * @param id 服务类型ID
     * @return 服务类型输出
     */
    @Transactional(readOnly = true)
    public ServiceTypeOutput execute(String id) {
        log.debug("Getting service type by id: {}", id);

        return serviceTypeRepository.findById(id)
                .map(ServiceTypeOutput::from)
                .orElseThrow(() -> new IllegalArgumentException("Service type not found with id: " + id));
    }
}