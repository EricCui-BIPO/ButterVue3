package com.i0.client.application.usecases;

import com.i0.client.application.dto.input.CreateClientInput;
import com.i0.client.application.dto.output.ClientOutput;
import com.i0.client.domain.entities.Client;
import com.i0.client.domain.repositories.ClientRepository;
import com.i0.client.domain.services.ClientDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 创建客户用例
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CreateClientUseCase {
    
    private final ClientRepository clientRepository;
    private final ClientDomainService clientDomainService;
    
    /**
     * 执行创建客户操作
     * @param input 创建客户输入
     * @return 创建的客户输出
     */
    @Transactional
    public ClientOutput execute(CreateClientInput input) {
        log.info("Creating client with name: {}, code: {}", input.getName(), input.getCode());
        
        // 验证业务规则
        clientDomainService.validateNameUniqueness(input.getName());
        clientDomainService.validateCodeUniqueness(input.getCode());
        
        // 创建客户实体
        Client client = Client.create(
                input.getName(),
                input.getCode(),
                input.getAliasName(),
                input.getLocationId(),
                input.getDescription()
        );
        
        // 保存客户
        Client savedClient = clientRepository.save(client);
        
        log.info("Client created successfully with ID: {}", savedClient.getId());
        
        // 转换为输出DTO
        return ClientOutput.from(savedClient);
    }
}