package com.i0.client.domain.services;

import com.i0.client.domain.entities.Client;
import com.i0.client.domain.repositories.ClientRepository;
import com.i0.client.domain.exceptions.ClientAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 客户领域服务
 * 封装复杂的业务逻辑和跨实体操作
 */
@Slf4j
@RequiredArgsConstructor
public class ClientDomainService {
    
    private final ClientRepository clientRepository;

    /**
     * 验证客户名称的唯一性
     * @param name 客户名称
     * @throws ClientAlreadyExistsException 如果名称已存在
     */
    public void validateNameUniqueness(String name) {
        if (clientRepository.existsByName(name)) {
            log.warn("Client name already exists: {}", name);
            throw ClientAlreadyExistsException.byName(name);
        }
    }
    
    /**
     * 验证客户代码的唯一性
     * @param code 客户代码
     * @throws ClientAlreadyExistsException 如果代码已存在
     */
    public void validateCodeUniqueness(String code) {
        if (clientRepository.existsByCode(code)) {
            log.warn("Client code already exists: {}", code);
            throw ClientAlreadyExistsException.byCode(code);
        }
    }
    
    /**
     * 验证客户名称的唯一性（排除指定ID）
     * @param name 客户名称
     * @param excludeId 要排除的客户ID
     * @throws ClientAlreadyExistsException 如果名称已存在
     */
    public void validateNameUniqueness(String name, String excludeId) {
        if (clientRepository.existsByNameAndIdNot(name, excludeId)) {
            log.warn("Client name already exists: {} (excluding ID: {})", name, excludeId);
            throw ClientAlreadyExistsException.byName(name);
        }
    }
    
    /**
     * 验证客户代码的唯一性（排除指定ID）
     * @param code 客户代码
     * @param excludeId 要排除的客户ID
     * @throws ClientAlreadyExistsException 如果代码已存在
     */
    public void validateCodeUniqueness(String code, String excludeId) {
        if (clientRepository.existsByCodeAndIdNot(code, excludeId)) {
            log.warn("Client code already exists: {} (excluding ID: {})", code, excludeId);
            throw ClientAlreadyExistsException.byCode(code);
        }
    }
    
    /**
     * 检查客户是否可以被删除
     * 这里可以添加业务规则，比如检查是否有关联的服务或人才
     * @param client 要删除的客户
     * @return true如果可以删除
     */
    public boolean canBeDeleted(Client client) {
        // TODO: 实现业务规则检查
        // 例如：检查是否有关联的服务、人才等
        log.debug("Checking if client can be deleted: {}", client.getId());

        // 验证客户确实存在
        clientRepository.findById(client.getId());

        return true;
    }
}