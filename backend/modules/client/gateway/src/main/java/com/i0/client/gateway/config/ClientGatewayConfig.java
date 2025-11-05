package com.i0.client.gateway.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * 客户网关层配置
 * MyBatis-Plus配置已统一在MybatisPlusConfig中管理
 */
@Configuration
@MapperScan("com.i0.client.gateway.persistence.mappers")
public class ClientGatewayConfig {

    // MyBatis-Plus拦截器配置已移至全局配置类 MybatisPlusConfig
    // 避免Bean定义冲突
}