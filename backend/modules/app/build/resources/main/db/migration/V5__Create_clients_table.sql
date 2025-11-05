-- 创建客户表
CREATE TABLE clients (
    id VARCHAR(64) NOT NULL COMMENT '客户ID',
    name VARCHAR(100) NOT NULL COMMENT '客户名称',
    code VARCHAR(50) NOT NULL COMMENT '客户代码',
    alias_name VARCHAR(100) COMMENT '客户别名',
    country_id VARCHAR(64) NOT NULL COMMENT '国家ID',
    description TEXT COMMENT '描述',
    is_active TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否激活(1=激活,0=未激活)',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    version BIGINT NOT NULL DEFAULT 0 COMMENT '版本号（乐观锁）',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标记(1=已删除,0=未删除)',
    creator_id VARCHAR(64) COMMENT '创建者ID',
    updater_id VARCHAR(64) COMMENT '更新者ID',
    creator VARCHAR(100) COMMENT '创建者姓名',
    updater VARCHAR(100) COMMENT '更新者姓名',
    PRIMARY KEY (id),
    UNIQUE KEY uk_clients_name (name, is_deleted),
    UNIQUE KEY uk_clients_code (code, is_deleted),
    KEY idx_clients_country_id (country_id),
    KEY idx_clients_active (is_active),
    KEY idx_clients_created_at (created_at),
    KEY idx_clients_name_code (name, code),
    KEY idx_clients_alias_name (alias_name),
    KEY idx_clients_creator (creator_id),
    KEY idx_clients_updater (updater_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户表';

-- 创建索引以优化搜索性能
CREATE INDEX idx_clients_search ON clients (name, code, alias_name, country_id, is_active, is_deleted);
CREATE INDEX idx_clients_country_active ON clients (country_id, is_active, is_deleted);
CREATE INDEX idx_clients_active_created ON clients (is_active, is_deleted, created_at);

-- 插入客户测试数据（基于世界知名科技公司）
INSERT INTO clients (id, name, code, alias_name, country_id, description, is_active, created_at, updated_at, version, is_deleted, creator_id, updater_id, creator, updater) VALUES
-- 美国科技公司
('client_001', 'Apple Inc.', 'APPLE', '苹果公司', 'country_us', '全球领先的消费电子产品和软件公司，以iPhone、iPad、Mac等产品闻名', 1, NOW(), NOW(), 0, 0, 'system', 'system', 'system', 'system'),
('client_002', 'Microsoft Corporation', 'MSFT', '微软公司', 'country_us', '全球最大的软件公司之一，Windows操作系统和Office办公软件的开发商', 1, NOW(), NOW(), 0, 0, 'system', 'system', 'system', 'system'),
('client_003', 'Google LLC', 'GOOGL', '谷歌公司', 'country_us', '全球最大的搜索引擎公司，Android系统和Chrome浏览器的开发商', 1, NOW(), NOW(), 0, 0, 'system', 'system', 'system', 'system'),
('client_004', 'Amazon.com Inc.', 'AMZN', '亚马逊公司', 'country_us', '全球最大的电子商务和云计算公司，AWS云服务提供商', 1, NOW(), NOW(), 0, 0, 'system', 'system', 'system', 'system'),

-- 中国科技公司
('client_005', 'Tencent Holdings Limited', 'TENCENT', '腾讯控股', 'country_cn', '中国最大的互联网公司之一，微信、QQ的开发商', 1, NOW(), NOW(), 0, 0, 'system', 'system', 'system', 'system'),
('client_006', 'Alibaba Group Holding Limited', 'BABA', '阿里巴巴集团', 'country_cn', '中国最大的电子商务公司，淘宝、天猫、支付宝的运营商', 1, NOW(), NOW(), 0, 0, 'system', 'system', 'system', 'system'),
('client_007', 'Baidu Inc.', 'BIDU', '百度公司', 'country_cn', '中国最大的搜索引擎公司，人工智能和自动驾驶技术领先企业', 0, NOW(), NOW(), 0, 0, 'system', 'system', 'system', 'system'),

-- 韩国科技公司
('client_008', 'Samsung Electronics Co., Ltd.', 'SAMSUNG', '三星电子', 'country_kr', '全球最大的智能手机和半导体制造商', 1, NOW(), NOW(), 0, 0, 'system', 'system', 'system', 'system'),

-- 日本科技公司
('client_009', 'Sony Corporation', 'SONY', '索尼公司', 'country_jp', '全球知名的电子产品、游戏和娱乐公司，PlayStation游戏机制造商', 1, NOW(), NOW(), 0, 0, 'system', 'system', 'system', 'system'),

-- 荷兰科技公司
('client_010', 'ASML Holding N.V.', 'ASML', 'ASML公司', 'country_nl', '全球领先的半导体设备制造商，光刻机技术领导者', 1, NOW(), NOW(), 0, 0, 'system', 'system', 'system', 'system');