-- 创建聊天会话表
CREATE TABLE IF NOT EXISTS chat_sessions (
    id VARCHAR(100) NOT NULL PRIMARY KEY COMMENT '会话ID',
    title VARCHAR(100) NOT NULL COMMENT '会话标题',
    status ENUM('ACTIVE', 'PAUSED', 'COMPLETED', 'CLOSED') NOT NULL DEFAULT 'ACTIVE' COMMENT '会话状态',
    user_id VARCHAR(50) COMMENT '用户ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT(1) DEFAULT 0 NOT NULL COMMENT '逻辑删除标记',
    INDEX idx_chat_sessions_user_id (user_id),
    INDEX idx_chat_sessions_status (status),
    INDEX idx_chat_sessions_created_at (created_at),
    INDEX idx_chat_sessions_updated_at (updated_at),
    INDEX idx_chat_sessions_title (title)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天会话表';

-- 创建聊天消息表（无外键约束版本，提高并发性能）
CREATE TABLE IF NOT EXISTS chat_messages (
    id VARCHAR(100) NOT NULL PRIMARY KEY COMMENT '消息ID',
    session_id VARCHAR(100) NOT NULL COMMENT '会话ID',
    role ENUM('USER', 'ASSISTANT', 'SYSTEM', 'FUNCTION') NOT NULL COMMENT '消息角色',
    content TEXT COMMENT '消息内容',
    function_name VARCHAR(100) COMMENT '函数名称',
    function_arguments JSON COMMENT '函数参数',
    function_description TEXT COMMENT '函数描述',
    parent_message_id VARCHAR(100) COMMENT '父消息ID',
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '消息时间',
    is_deleted TINYINT(1) DEFAULT 0 NOT NULL COMMENT '逻辑删除标记',
    INDEX idx_chat_messages_session_id (session_id),
    INDEX idx_chat_messages_session_timestamp (session_id, timestamp),
    INDEX idx_chat_messages_session_role (session_id, role),
    INDEX idx_chat_messages_role (role),
    INDEX idx_chat_messages_timestamp (timestamp),
    INDEX idx_chat_messages_parent_id (parent_message_id),
    INDEX idx_chat_messages_function_name (function_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天消息表（无外键约束版本，提高并发性能）';