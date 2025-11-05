package com.i0.agents.gateway.persistence.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.i0.agents.gateway.persistence.dataobjects.ChatSessionDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 聊天会话Mapper接口
 */
@Mapper
public interface ChatSessionMapper extends BaseMapper<ChatSessionDO> {
    // 继承BaseMapper，获得基础CRUD功能
    // 不添加自定义方法，遵循架构规范
}