-- 创建service_types表
-- 用于存储服务类型信息

CREATE TABLE service_types (
    id VARCHAR(36) NOT NULL,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(20) NOT NULL,
    description TEXT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,

    PRIMARY KEY (id)
);

-- 插入四种预设服务类型的初始数据（英文版本）
INSERT INTO service_types (id, name, code, description, is_active, created_at, updated_at, is_deleted) VALUES
('service-type-001', 'Employer of Record Service', 'EOR', 'Employer of Record service providing legal employment relationship management', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('service-type-002', 'Global Payroll Outsourcing', 'GPO', 'Global Payroll Outsourcing service providing multi-country payroll calculation and distribution', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('service-type-003', 'Independent Contractor Management', 'CONTRACTOR', 'Independent Contractor Management service providing contract and compliance management', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('service-type-004', 'Self-Employment Management', 'SELF', 'Self-Employment Management service providing compliance and tax management for self-employed individuals', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE);

-- 唯一索引（仅针对未删除的记录），支持逻辑删除后重复创建
-- CREATE UNIQUE INDEX uniq_service_types_name_active
--     ON service_types ((CASE WHEN is_deleted = 0 THEN name ELSE NULL END));
-- CREATE UNIQUE INDEX uniq_service_types_code_active
--     ON service_types ((CASE WHEN is_deleted = 0 THEN code ELSE NULL END));

-- 创建索引以提高查询性能
CREATE INDEX idx_service_types_code ON service_types(code);
CREATE INDEX idx_service_types_active ON service_types(is_active);
CREATE INDEX idx_service_types_created_at ON service_types(created_at);
