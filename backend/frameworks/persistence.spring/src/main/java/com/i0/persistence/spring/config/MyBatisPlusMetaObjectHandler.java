package com.i0.persistence.spring.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus元数据对象处理器
 * 用于自动填充创建时间和更新时间
 */
@Slf4j
@Component
public class MyBatisPlusMetaObjectHandler implements MetaObjectHandler {
    
    private static final String CREATED_AT = "createdAt";
    private static final String UPDATED_AT = "updatedAt";
    
    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("Start insert fill...");
        
        LocalDateTime now = LocalDateTime.now();
        
        // 填充创建时间
        this.strictInsertFill(metaObject, CREATED_AT, LocalDateTime.class, now);
        
        // 填充更新时间
        this.strictInsertFill(metaObject, UPDATED_AT, LocalDateTime.class, now);
    }
    
    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("Start update fill...");
        
        LocalDateTime now = LocalDateTime.now();
        
        // 填充更新时间
        this.strictUpdateFill(metaObject, UPDATED_AT, LocalDateTime.class, now);
    }
}