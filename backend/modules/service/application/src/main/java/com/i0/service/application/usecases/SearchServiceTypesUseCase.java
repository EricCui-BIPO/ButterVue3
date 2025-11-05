package com.i0.service.application.usecases;

import com.i0.service.application.dto.input.ServiceTypePageInput;
import com.i0.service.application.dto.output.ServiceTypePageOutput;
import com.i0.service.domain.entities.ServiceTypeEntity;
import com.i0.service.domain.repositories.ServiceTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.i0.domain.core.pagination.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class SearchServiceTypesUseCase {

    private final ServiceTypeRepository serviceTypeRepository;

    /**
     * 搜索服务类型（分页）
     * @param input 分页查询输入
     * @return 分页查询结果
     */
    @Transactional(readOnly = true)
    public Pageable<ServiceTypePageOutput> execute(ServiceTypePageInput input) {
        log.debug("Searching service types with criteria: nameKeyword={}, serviceType={}, activeOnly={}, page={}, size={}",
                input.getNameKeyword(), input.getServiceType(), input.getActiveOnly(), input.getPage(), input.getSize());
        // 执行搜索
        Pageable<ServiceTypeEntity> result = serviceTypeRepository.searchServiceTypes(
                input.getNameKeyword(),
                input.getServiceType(),
                input.getActiveOnly(),
                input.getPage(),
                input.getSize()
        );

        log.debug("Found {} service types", result.getTotal());
        return result.map(ServiceTypePageOutput::from);
    }
}