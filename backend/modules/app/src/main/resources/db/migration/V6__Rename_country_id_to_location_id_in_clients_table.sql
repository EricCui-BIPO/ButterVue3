-- 修改客户表：将country_id重命名为location_id以符合位置管理系统的命名规范

-- 重命名列
ALTER TABLE clients CHANGE COLUMN country_id location_id VARCHAR(64) NOT NULL COMMENT '位置ID';

-- 重命名索引
DROP INDEX idx_clients_country_id ON clients;
DROP INDEX idx_clients_country_active ON clients;
DROP INDEX idx_clients_search ON clients;

CREATE INDEX idx_clients_location_id ON clients (location_id);
CREATE INDEX idx_clients_location_active ON clients (location_id, is_active, is_deleted);
CREATE INDEX idx_clients_search ON clients (name, code, alias_name, location_id, is_active, is_deleted);

-- 更新测试数据中的country引用为location引用
-- 注意：这里假设locations表中已经存在对应的位置记录
UPDATE clients SET location_id = 'location_us' WHERE location_id = 'country_us';
UPDATE clients SET location_id = 'location_cn' WHERE location_id = 'country_cn';
UPDATE clients SET location_id = 'location_kr' WHERE location_id = 'country_kr';
UPDATE clients SET location_id = 'location_jp' WHERE location_id = 'country_jp';
UPDATE clients SET location_id = 'location_nl' WHERE location_id = 'country_nl';