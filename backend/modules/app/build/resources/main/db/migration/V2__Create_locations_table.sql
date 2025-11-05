-- 创建locations表
-- 用于存储地理位置信息 (MySQL Database)

CREATE TABLE locations (
    id VARCHAR(36) NOT NULL COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '地理位置名称',
    location_type VARCHAR(20) NOT NULL COMMENT '地理位置类型',
    iso_code VARCHAR(10) NULL COMMENT 'ISO代码（支持ISO 3166-1和ISO 3166-2标准）',
    description TEXT NULL COMMENT '地理位置描述',
    parent_id VARCHAR(36) NULL COMMENT '上级地理位置ID',
    level INT NOT NULL COMMENT '层级深度（0:大洲, 1:国家, 2:省/州, 3:市）',
    sort_order INT DEFAULT 0 COMMENT '排序序号',
    is_active BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否激活',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否删除（软删除标记）',

    PRIMARY KEY (id),
    KEY idx_parent_id (parent_id),
    KEY idx_location_type (location_type),
    KEY idx_level (level),
    KEY idx_is_active (is_active),
    KEY idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='地理位置表';

-- 唯一索引（仅针对未删除的数据），支持逻辑删除后重新创建
-- CREATE UNIQUE INDEX uk_locations_iso_code_active
--     ON locations ((CASE WHEN is_deleted = 0 THEN iso_code ELSE NULL END));

-- 插入大洲数据（ISO 3166-1 alpha-2标准）
INSERT INTO locations (id, name, location_type, iso_code, description, parent_id, level, sort_order, is_active, created_at, updated_at, is_deleted) VALUES
('continent-as', '亚洲', 'CONTINENT', 'AS', '亚洲 continent', NULL, 0, 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('continent-af', '非洲', 'CONTINENT', 'AF', '非洲 continent', NULL, 0, 2, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('continent-na', '北美洲', 'CONTINENT', 'NA', '北美洲 continent', NULL, 0, 3, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('continent-sa', '南美洲', 'CONTINENT', 'SA', '南美洲 continent', NULL, 0, 4, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('continent-an', '南极洲', 'CONTINENT', 'AN', '南极洲 continent', NULL, 0, 5, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('continent-eu', '欧洲', 'CONTINENT', 'EU', '欧洲 continent', NULL, 0, 6, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('continent-oc', '大洋洲', 'CONTINENT', 'OC', '大洋洲 continent', NULL, 0, 7, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE);

-- 插入主要国家数据（ISO 3166-1 alpha-2标准）
INSERT INTO locations (id, name, location_type, iso_code, description, parent_id, level, sort_order, is_active, created_at, updated_at, is_deleted) VALUES
-- 亚洲国家
('country-cn', '中国', 'COUNTRY', 'CN', '中华人民共和国', 'continent-as', 1, 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('country-jp', '日本', 'COUNTRY', 'JP', '日本国', 'continent-as', 1, 2, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('country-kr', '韩国', 'COUNTRY', 'KR', '大韩民国', 'continent-as', 1, 3, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('country-sg', '新加坡', 'COUNTRY', 'SG', '新加坡共和国', 'continent-as', 1, 4, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('country-in', '印度', 'COUNTRY', 'IN', '印度共和国', 'continent-as', 1, 5, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('country-th', '泰国', 'COUNTRY', 'TH', '泰王国', 'continent-as', 1, 6, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('country-my', '马来西亚', 'COUNTRY', 'MY', '马来西亚', 'continent-as', 1, 7, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('country-vn', '越南', 'COUNTRY', 'VN', '越南社会主义共和国', 'continent-as', 1, 8, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('country-ph', '菲律宾', 'COUNTRY', 'PH', '菲律宾共和国', 'continent-as', 1, 9, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('country-id', '印度尼西亚', 'COUNTRY', 'ID', '印度尼西亚共和国', 'continent-as', 1, 10, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),

-- 欧洲国家
('country-gb', '英国', 'COUNTRY', 'GB', '大不列颠及北爱尔兰联合王国', 'continent-eu', 1, 11, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('country-de', '德国', 'COUNTRY', 'DE', '德意志联邦共和国', 'continent-eu', 1, 12, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('country-fr', '法国', 'COUNTRY', 'FR', '法兰西共和国', 'continent-eu', 1, 13, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('country-it', '意大利', 'COUNTRY', 'IT', '意大利共和国', 'continent-eu', 1, 14, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('country-es', '西班牙', 'COUNTRY', 'ES', '西班牙王国', 'continent-eu', 1, 15, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('country-nl', '荷兰', 'COUNTRY', 'NL', '荷兰王国', 'continent-eu', 1, 16, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('country-be', '比利时', 'COUNTRY', 'BE', '比利时王国', 'continent-eu', 1, 17, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('country-ch', '瑞士', 'COUNTRY', 'CH', '瑞士联邦', 'continent-eu', 1, 18, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('country-at', '奥地利', 'COUNTRY', 'AT', '奥地利共和国', 'continent-eu', 1, 19, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('country-se', '瑞典', 'COUNTRY', 'SE', '瑞典王国', 'continent-eu', 1, 20, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('country-no', '挪威', 'COUNTRY', 'NO', '挪威王国', 'continent-eu', 1, 21, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('country-dk', '丹麦', 'COUNTRY', 'DK', '丹麦王国', 'continent-eu', 1, 22, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('country-fi', '芬兰', 'COUNTRY', 'FI', '芬兰共和国', 'continent-eu', 1, 23, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('country-pl', '波兰', 'COUNTRY', 'PL', '波兰共和国', 'continent-eu', 1, 24, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('country-cz', '捷克', 'COUNTRY', 'CZ', '捷克共和国', 'continent-eu', 1, 25, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('country-hu', '匈牙利', 'COUNTRY', 'HU', '匈牙利', 'continent-eu', 1, 26, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('country-gr', '希腊', 'COUNTRY', 'GR', '希腊共和国', 'continent-eu', 1, 27, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('country-pt', '葡萄牙', 'COUNTRY', 'PT', '葡萄牙共和国', 'continent-eu', 1, 28, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),

-- 北美洲国家
('country-us', '美国', 'COUNTRY', 'US', '美利坚合众国', 'continent-na', 1, 29, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('country-ca', '加拿大', 'COUNTRY', 'CA', '加拿大', 'continent-na', 1, 30, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('country-mx', '墨西哥', 'COUNTRY', 'MX', '墨西哥合众国', 'continent-na', 1, 31, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),

-- 大洋洲国家
('country-au', '澳大利亚', 'COUNTRY', 'AU', '澳大利亚联邦', 'continent-oc', 1, 32, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('country-nz', '新西兰', 'COUNTRY', 'NZ', '新西兰', 'continent-oc', 1, 33, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),

-- 非洲国家
('country-za', '南非', 'COUNTRY', 'ZA', '南非共和国', 'continent-af', 1, 34, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('country-eg', '埃及', 'COUNTRY', 'EG', '阿拉伯埃及共和国', 'continent-af', 1, 35, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('country-ng', '尼日利亚', 'COUNTRY', 'NG', '尼日利亚联邦共和国', 'continent-af', 1, 36, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('country-ke', '肯尼亚', 'COUNTRY', 'KE', '肯尼亚共和国', 'continent-af', 1, 37, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),

-- 南美洲国家
('country-br', '巴西', 'COUNTRY', 'BR', '巴西联邦共和国', 'continent-sa', 1, 38, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('country-ar', '阿根廷', 'COUNTRY', 'AR', '阿根廷共和国', 'continent-sa', 1, 39, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('country-cl', '智利', 'COUNTRY', 'CL', '智利共和国', 'continent-sa', 1, 40, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('country-co', '哥伦比亚', 'COUNTRY', 'CO', '哥伦比亚共和国', 'continent-sa', 1, 41, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('country-pe', '秘鲁', 'COUNTRY', 'PE', '秘鲁共和国', 'continent-sa', 1, 42, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE);

-- 插入中国主要省份（按照ISO 3166-2标准补全ISO代码）
INSERT INTO locations (id, name, location_type, iso_code, description, parent_id, level, sort_order, is_active, created_at, updated_at, is_deleted) VALUES
('province-bj', '北京', 'PROVINCE', 'CN-11', '中华人民共和国首都', 'country-cn', 2, 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('province-sh', '上海', 'PROVINCE', 'CN-31', '中华人民共和国直辖市', 'country-cn', 2, 2, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('province-gd', '广东', 'PROVINCE', 'CN-44', '广东省', 'country-cn', 2, 3, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('province-zj', '浙江', 'PROVINCE', 'CN-33', '浙江省', 'country-cn', 2, 4, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('province-js', '江苏', 'PROVINCE', 'CN-32', '江苏省', 'country-cn', 2, 5, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('province-sd', '山东', 'PROVINCE', 'CN-37', '山东省', 'country-cn', 2, 6, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('province-hn', '河南', 'PROVINCE', 'CN-41', '河南省', 'country-cn', 2, 7, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('province-sc', '四川', 'PROVINCE', 'CN-51', '四川省', 'country-cn', 2, 8, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('province-hb', '湖北', 'PROVINCE', 'CN-42', '湖北省', 'country-cn', 2, 9, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('province-fj', '福建', 'PROVINCE', 'CN-35', '福建省', 'country-cn', 2, 10, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('province-tj', '天津', 'PROVINCE', 'CN-12', '中华人民共和国直辖市', 'country-cn', 2, 11, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('province-cq', '重庆', 'PROVINCE', 'CN-50', '中华人民共和国直辖市', 'country-cn', 2, 12, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('province-ln', '辽宁', 'PROVINCE', 'CN-21', '辽宁省', 'country-cn', 2, 13, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('province-hlj', '黑龙江', 'PROVINCE', 'CN-23', '黑龙江省', 'country-cn', 2, 14, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('province-jl', '吉林', 'PROVINCE', 'CN-22', '吉林省', 'country-cn', 2, 15, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('province-he', '河北', 'PROVINCE', 'CN-13', '河北省', 'country-cn', 2, 16, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('province-sx', '山西', 'PROVINCE', 'CN-14', '山西省', 'country-cn', 2, 17, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('province-sn', '陕西', 'PROVINCE', 'CN-61', '陕西省', 'country-cn', 2, 18, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('province-gs', '甘肃', 'PROVINCE', 'CN-62', '甘肃省', 'country-cn', 2, 19, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('province-nmg', '内蒙古', 'PROVINCE', 'CN-15', '内蒙古自治区', 'country-cn', 2, 20, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('province-xj', '新疆', 'PROVINCE', 'CN-65', '新疆维吾尔自治区', 'country-cn', 2, 21, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('province-qh', '青海', 'PROVINCE', 'CN-63', '青海省', 'country-cn', 2, 22, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('province-nx', '宁夏', 'PROVINCE', 'CN-64', '宁夏回族自治区', 'country-cn', 2, 23, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('province-gx', '广西', 'PROVINCE', 'CN-45', '广西壮族自治区', 'country-cn', 2, 24, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('province-gz', '贵州', 'PROVINCE', 'CN-52', '贵州省', 'country-cn', 2, 25, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('province-yn', '云南', 'PROVINCE', 'CN-53', '云南省', 'country-cn', 2, 26, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('province-ha', '海南', 'PROVINCE', 'CN-46', '海南省', 'country-cn', 2, 27, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('province-xz', '西藏', 'PROVINCE', 'CN-54', '西藏自治区', 'country-cn', 2, 28, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('province-ah', '安徽', 'PROVINCE', 'CN-34', '安徽省', 'country-cn', 2, 29, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('province-jx', '江西', 'PROVINCE', 'CN-36', '江西省', 'country-cn', 2, 30, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('province-hun', '湖南', 'PROVINCE', 'CN-43', '湖南省', 'country-cn', 2, 31, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE);

-- 插入中国主要城市（市级，不包含区级细分）
INSERT INTO locations (id, name, location_type, iso_code, description, parent_id, level, sort_order, is_active, created_at, updated_at, is_deleted) VALUES
-- 直辖市（北京、上海、天津、重庆作为省级，不再添加城市级别）

-- 广东主要城市
('city-gd-gz', '广州市', 'CITY', 'CN-44-GZ', '广东省广州市', 'province-gd', 3, 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('city-gd-sz', '深圳市', 'CITY', 'CN-44-SZ', '广东省深圳市', 'province-gd', 3, 2, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('city-gd-dg', '东莞市', 'CITY', 'CN-44-DG', '广东省东莞市', 'province-gd', 3, 3, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('city-gd-fs', '佛山市', 'CITY', 'CN-44-FS', '广东省佛山市', 'province-gd', 3, 4, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('city-gd-zs', '中山市', 'CITY', 'CN-44-ZS', '广东省中山市', 'province-gd', 3, 5, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('city-gd-zh', '珠海市', 'CITY', 'CN-44-ZH', '广东省珠海市', 'province-gd', 3, 6, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('city-gd-st', '汕头市', 'CITY', 'CN-44-ST', '广东省汕头市', 'province-gd', 3, 7, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),

-- 浙江主要城市
('city-zj-hz', '杭州市', 'CITY', 'CN-33-HZ', '浙江省杭州市', 'province-zj', 3, 8, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('city-zj-nb', '宁波市', 'CITY', 'CN-33-NB', '浙江省宁波市', 'province-zj', 3, 9, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('city-zj-wz', '温州市', 'CITY', 'CN-33-WZ', '浙江省温州市', 'province-zj', 3, 10, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('city-zj-jx', '嘉兴市', 'CITY', 'CN-33-JX', '浙江省嘉兴市', 'province-zj', 3, 11, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('city-zj-huz', '湖州市', 'CITY', 'CN-33-HUZ', '浙江省湖州市', 'province-zj', 3, 12, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('city-zj-sx', '绍兴市', 'CITY', 'CN-33-SX', '浙江省绍兴市', 'province-zj', 3, 13, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),

-- 江苏主要城市
('city-js-nj', '南京市', 'CITY', 'CN-32-NJ', '江苏省南京市', 'province-js', 3, 14, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('city-js-sz', '苏州市', 'CITY', 'CN-32-SZ', '江苏省苏州市', 'province-js', 3, 15, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('city-js-wx', '无锡市', 'CITY', 'CN-32-WX', '江苏省无锡市', 'province-js', 3, 16, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('city-js-cz', '常州市', 'CITY', 'CN-32-CZ', '江苏省常州市', 'province-js', 3, 17, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('city-js-ntz', '南通市', 'CITY', 'CN-32-NTZ', '江苏省南通市', 'province-js', 3, 18, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('city-js-yz', '扬州市', 'CITY', 'CN-32-YZ', '江苏省扬州市', 'province-js', 3, 19, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),

-- 山东主要城市
('city-sd-jn', '济南市', 'CITY', 'CN-37-JN', '山东省济南市', 'province-sd', 3, 20, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('city-sd-qd', '青岛市', 'CITY', 'CN-37-QD', '山东省青岛市', 'province-sd', 3, 21, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('city-sd-yt', '烟台市', 'CITY', 'CN-37-YT', '山东省烟台市', 'province-sd', 3, 22, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('city-sd-wf', '潍坊市', 'CITY', 'CN-37-WF', '山东省潍坊市', 'province-sd', 3, 23, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),

-- 四川主要城市
('city-sc-cd', '成都市', 'CITY', 'CN-51-CD', '四川省成都市', 'province-sc', 3, 24, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('city-sc-my', '绵阳市', 'CITY', 'CN-51-MY', '四川省绵阳市', 'province-sc', 3, 25, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),

-- 湖北主要城市
('city-hb-wh', '武汉市', 'CITY', 'CN-42-WH', '湖北省武汉市', 'province-hb', 3, 26, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('city-hb-yc', '宜昌市', 'CITY', 'CN-42-YC', '湖北省宜昌市', 'province-hb', 3, 27, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),

-- 福建主要城市
('city-fj-fz', '福州市', 'CITY', 'CN-35-FZ', '福建省福州市', 'province-fj', 3, 28, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('city-fj-xm', '厦门市', 'CITY', 'CN-35-XM', '福建省厦门市', 'province-fj', 3, 29, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE),
('city-fj-qz', '泉州市', 'CITY', 'CN-35-QZ', '福建省泉州市', 'province-fj', 3, 30, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE);
