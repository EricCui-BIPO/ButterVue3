-- 为chat_messages表添加ui_components字段以支持UI组件引用信息
ALTER TABLE chat_messages
ADD COLUMN ui_components JSON COMMENT 'UI组件引用信息（JSON格式存储）'
AFTER function_description;

-- 添加注释说明
ALTER TABLE chat_messages COMMENT = '聊天消息表（无外键约束版本，提高并发性能，支持UI组件信息）';