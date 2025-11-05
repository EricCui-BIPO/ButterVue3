package com.i0.client.application.usecases;

import com.i0.client.domain.entities.Client;
import com.i0.client.domain.repositories.ClientRepository;
import com.i0.client.domain.services.ClientDomainService;
import com.i0.client.domain.exceptions.ClientNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 删除客户用例
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteClientUseCase {
    
    private final ClientRepository clientRepository;
    private final ClientDomainService clientDomainService;
    
    /**
     * 执行删除客户操作
     * @param id 客户ID
     */
    @Transactional
    public void execute(String id) {
        log.info("Deleting client with ID: {}", id);
        
        // 查找现有客户
        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> ClientNotFoundException.byId(id));
        
        // 检查是否可以删除
        if (!clientDomainService.canBeDeleted(existingClient)) {
            throw new IllegalStateException("Client cannot be deleted due to business constraints");
        }
        
        // 删除客户
        clientRepository.delete(existingClient);
        
        log.info("Client deleted successfully with ID: {}", id);
    }
}