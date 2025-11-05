package com.i0.client.application.config;

import com.i0.client.domain.repositories.ClientRepository;
import com.i0.client.domain.services.ClientDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 客户应用层配置
 * 声明领域服务Bean，使它们可以被Spring管理
 */
@Configuration
public class ClientApplicationConfig {

    /**
     * 声明客户领域服务Bean
     * @param clientRepository 客户仓储
     * @return 客户领域服务实例
     */
    @Bean
    public ClientDomainService clientDomainService(ClientRepository clientRepository) {
        return new ClientDomainService(clientRepository);
    }
}