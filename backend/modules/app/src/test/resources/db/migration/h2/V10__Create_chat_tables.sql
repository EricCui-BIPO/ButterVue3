-- 创建聊天会话表 (H2兼容版本)
CREATE TABLE IF NOT EXISTS chat_sessions (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    user_id VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE NOT NULL
);

-- 创建聊天消息表 (H2兼容版本，无外键约束)
CREATE TABLE IF NOT EXISTS chat_messages (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    session_id VARCHAR(36) NOT NULL,
    role VARCHAR(20) NOT NULL,
    content CLOB,
    function_name VARCHAR(100),
    function_arguments CLOB,
    function_description CLOB,
    parent_message_id VARCHAR(36),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE NOT NULL
);

-- 创建索引（优化并发性能）
CREATE INDEX IF NOT EXISTS idx_chat_sessions_user_id ON chat_sessions(user_id);
CREATE INDEX IF NOT EXISTS idx_chat_sessions_status ON chat_sessions(status);
CREATE INDEX IF NOT EXISTS idx_chat_sessions_created_at ON chat_sessions(created_at);
CREATE INDEX IF NOT EXISTS idx_chat_sessions_updated_at ON chat_sessions(updated_at);
CREATE INDEX IF NOT EXISTS idx_chat_sessions_title ON chat_sessions(title);

CREATE INDEX IF NOT EXISTS idx_chat_messages_session_id ON chat_messages(session_id);
CREATE INDEX IF NOT EXISTS idx_chat_messages_session_timestamp ON chat_messages(session_id, timestamp);
CREATE INDEX IF NOT EXISTS idx_chat_messages_session_role ON chat_messages(session_id, role);
CREATE INDEX IF NOT EXISTS idx_chat_messages_role ON chat_messages(role);
CREATE INDEX IF NOT EXISTS idx_chat_messages_timestamp ON chat_messages(timestamp);
CREATE INDEX IF NOT EXISTS idx_chat_messages_parent_id ON chat_messages(parent_message_id);
CREATE INDEX IF NOT EXISTS idx_chat_messages_function_name ON chat_messages(function_name);

-- 插入测试数据
INSERT INTO chat_sessions (id, title, status, user_id) VALUES
('test-session-1', '测试会话1', 'ACTIVE', 'test-user-1'),
('test-session-2', '测试会话2', 'ACTIVE', 'test-user-1');

INSERT INTO chat_messages (id, session_id, role, content, timestamp) VALUES
('test-message-1', 'test-session-1', 'USER', '你好，我想创建一个实体', CURRENT_TIMESTAMP),
('test-message-2', 'test-session-1', 'ASSISTANT', '好的，请告诉我您想创建什么样的实体？', CURRENT_TIMESTAMP + INTERVAL '1' SECOND);