package com.i0.agents.gateway.config;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * AI对话服务配置
 * 配置AIConversationService相关的Bean
 */
@Slf4j
@Configuration
public class AIConversationConfiguration {

    /**
     * 创建OkHttpClient Bean（如果项目中还没有的话）
     */
    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();
    }

}