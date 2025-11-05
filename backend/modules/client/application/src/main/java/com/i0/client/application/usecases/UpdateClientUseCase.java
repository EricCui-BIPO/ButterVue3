package com.i0.client.application.usecases;

import com.i0.client.application.dto.input.UpdateClientInput;
import com.i0.client.application.dto.output.ClientOutput;
import com.i0.client.domain.entities.Client;
import com.i0.client.domain.repositories.ClientRepository;
import com.i0.client.domain.services.ClientDomainService;
import com.i0.client.domain.exceptions.ClientNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 更新客户用例
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateClientUseCase {

    private final ClientRepository clientRepository;
    private final ClientDomainService clientDomainService;
    
    /**
     * 执行更新客户操作
     * @param id 客户ID
     * @param input 更新客户输入
     * @return 更新后的客户输出
     */
    @Transactional
    public ClientOutput execute(String id, UpdateClientInput input) {
        log.info("Updating client with ID: {}", id);
        
        // 查找现有客户
        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> ClientNotFoundException.byId(id));
        
        // 验证业务规则（排除当前客户）
        clientDomainService.validateNameUniqueness(input.getName(), id);

        // 更新客户代码（如果提供了代码且有变化）
        if (input.getCode() != null && !input.getCode().trim().isEmpty()
                && !existingClient.getCode().equals(input.getCode())) {
            clientDomainService.validateCodeUniqueness(input.getCode(), id);
            existingClient.updateCode(input.getCode());
        }

        // 更新客户信息
        existingClient.update(
                input.getName(),
                input.getAliasName(),
                input.getLocationId(),
                input.getDescription()
        );
        
        // 保存更新后的客户
        Client savedClient = clientRepository.save(existingClient);
        
        log.info("Client updated successfully with ID: {}", savedClient.getId());

        // 转换为输出DTO
        return ClientOutput.from(savedClient);
    }

    }