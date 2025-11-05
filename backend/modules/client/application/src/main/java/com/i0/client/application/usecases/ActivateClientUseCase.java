package com.i0.client.application.usecases;

import com.i0.client.application.dto.output.ClientOutput;
import com.i0.client.domain.entities.Client;
import com.i0.client.domain.repositories.ClientRepository;
import com.i0.client.domain.exceptions.ClientNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 激活客户用例
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ActivateClientUseCase {
    
    private final ClientRepository clientRepository;
    
    /**
     * 执行激活客户操作
     * @param id 客户ID
     * @return 激活后的客户输出
     */
    @Transactional
    public ClientOutput execute(String id) {
        log.info("Activating client with ID: {}", id);
        
        // 查找现有客户
        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> ClientNotFoundException.byId(id));
        
        // 激活客户
        existingClient.activate();
        
        // 保存更新后的客户
        Client savedClient = clientRepository.save(existingClient);
        
        log.info("Client activated successfully with ID: {}", savedClient.getId());
        
        // 转换为输出DTO
        return ClientOutput.from(savedClient);
    }
}