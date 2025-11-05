package com.i0.agents.gateway.persistence.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.i0.agents.gateway.persistence.dataobjects.ChatMessageDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 聊天消息Mapper接口
 */
@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessageDO> {
    // 继承BaseMapper，获得基础CRUD功能
    // 不添加自定义方法，遵循架构规范
}