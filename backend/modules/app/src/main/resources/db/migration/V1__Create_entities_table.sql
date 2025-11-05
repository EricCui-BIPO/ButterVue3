-- 创建entities表
-- 用于存储实体信息 (H2 Database)

CREATE TABLE entities (
    id VARCHAR(36) NOT NULL,
    name VARCHAR(100) NOT NULL,
    entity_type VARCHAR(20) NOT NULL,
    code VARCHAR(50) NULL,
    description TEXT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,

    PRIMARY KEY (id)
);

-- 唯一索引（仅针对未删除的数据），支持逻辑删除后重新创建
-- CREATE UNIQUE INDEX uniq_entities_name_active
--     ON entities ((CASE WHEN is_deleted = 0 THEN name ELSE NULL END));
-- CREATE UNIQUE INDEX uniq_entities_code_active
--     ON entities ((CASE WHEN is_deleted = 0 THEN code ELSE NULL END));

-- 插入初始数据
INSERT INTO entities (id, name, entity_type, code, description, is_active, created_at, updated_at, is_deleted) VALUES
('bipo-entity-001', 'BIPO总部', 'BIPO_ENTITY', 'BIPO_HQ', 'BIPO公司总部实体', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('client-entity-001', '示例客户公司', 'CLIENT_ENTITY', 'CLIENT_DEMO', '示例客户公司实体', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('vendor-entity-001', '示例供应商公司', 'VENDOR_ENTITY', 'VENDOR_DEMO', '示例供应商公司实体', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE);
