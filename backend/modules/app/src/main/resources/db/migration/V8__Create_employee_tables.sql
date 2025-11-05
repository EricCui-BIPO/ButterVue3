-- 创建员工表
CREATE TABLE employees (
    id VARCHAR(36) PRIMARY KEY COMMENT '员工唯一标识符',
    name VARCHAR(100) NOT NULL COMMENT '员工姓名',
    employee_number VARCHAR(50) NOT NULL COMMENT '员工工号',
    work_location_id VARCHAR(100) COMMENT '工作地点ID',
    nationality_id VARCHAR(100) COMMENT '国籍ID',
    email VARCHAR(255) NOT NULL COMMENT '邮箱地址',
    department VARCHAR(100) COMMENT '部门',
    position VARCHAR(100) COMMENT '岗位',
    join_date DATETIME NOT NULL COMMENT '入职日期',
    leave_date DATETIME COMMENT '离职日期',
    data_location VARCHAR(20) NOT NULL COMMENT '数据存储位置',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '员工状态',
    client_id VARCHAR(36) COMMENT '所属客户ID',

    -- 审计字段
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    creator_id VARCHAR(36) COMMENT '创建者ID',
    updater_id VARCHAR(36) COMMENT '更新者ID',
    creator VARCHAR(100) COMMENT '创建者姓名',
    updater VARCHAR(100) COMMENT '更新者姓名',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标记 (0=未删除, 1=已删除)',

    -- 索引
    INDEX idx_employee_number (employee_number),
    INDEX idx_employee_email (email),
    INDEX idx_employee_department (department),
    INDEX idx_employee_work_location_id (work_location_id),
    INDEX idx_employee_nationality_id (nationality_id),
    INDEX idx_employee_status (status),
    INDEX idx_employee_data_location (data_location),
    INDEX idx_employee_client_id (client_id),
    INDEX idx_employee_created_at (created_at),

    -- 唯一约束
    UNIQUE KEY uk_employee_number (employee_number, is_deleted),
    UNIQUE KEY uk_employee_email (email, is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工信息表';

-- 创建员工条件字段表（为后续版本预留）
CREATE TABLE employee_conditional_fields (
    id VARCHAR(36) PRIMARY KEY COMMENT '条件字段唯一标识符',
    employee_id VARCHAR(36) NOT NULL COMMENT '员工ID',
    field_name VARCHAR(100) NOT NULL COMMENT '字段名称',
    field_value TEXT COMMENT '字段值',
    field_type VARCHAR(20) NOT NULL COMMENT '字段类型',

    -- 审计字段
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    creator_id VARCHAR(36) COMMENT '创建者ID',
    updater_id VARCHAR(36) COMMENT '更新者ID',
    creator VARCHAR(100) COMMENT '创建者姓名',
    updater VARCHAR(100) COMMENT '更新者姓名',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标记 (0=未删除, 1=已删除)',

    -- 索引
    INDEX idx_employee_id (employee_id),
    INDEX idx_field_name (field_name),
    INDEX idx_field_type (field_type),
    INDEX idx_created_at (created_at),

    -- 外键约束
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,

    -- 唯一约束
    UNIQUE KEY uk_employee_field (employee_id, field_name, is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工条件字段表';

-- 插入初始数据（示例数据）
INSERT INTO employees (
    id, name, employee_number, work_location_id, nationality_id, email, department, position,
    join_date, data_location, status, client_id, creator, updater
) VALUES 
-- Apple Inc. (client_001) employees - 17 employees
('employee-001', 'John Anderson', 'APPLE001', 'city-gd-sz', 'country-us', 'john.anderson@apple.com', 'Engineering', 'iOS Developer', '2023-01-15', 'SINGAPORE', 'ACTIVE', 'client_001', 'system', 'system'),
('employee-002', 'Sarah Chen', 'APPLE002', 'city-js-sz', 'country-cn', 'sarah.chen@apple.com', 'Design', 'UX Designer', '2023-02-20', 'NINGXIA', 'ACTIVE', 'client_001', 'system', 'system'),
('employee-003', 'Michael Johnson', 'APPLE003', 'city-gd-gz', 'country-us', 'michael.johnson@apple.com', 'Engineering', 'Senior Software Engineer', '2022-11-10', 'SINGAPORE', 'ACTIVE', 'client_001', 'system', 'system'),
('employee-004', 'Lisa Wang', 'APPLE004', 'city-zj-hz', 'country-cn', 'lisa.wang@apple.com', 'Marketing', 'Product Marketing Manager', '2023-03-05', 'NINGXIA', 'ACTIVE', 'client_001', 'system', 'system'),
('employee-005', 'David Kim', 'APPLE005', 'city-gd-sz', 'country-kr', 'david.kim@apple.com', 'Engineering', 'Hardware Engineer', '2022-09-18', 'SINGAPORE', 'ACTIVE', 'client_001', 'system', 'system'),
('employee-006', 'Emma Thompson', 'APPLE006', 'city-js-nj', 'country-gb', 'emma.thompson@apple.com', 'Sales', 'Sales Director', '2022-08-12', 'GERMANY', 'ACTIVE', 'client_001', 'system', 'system'),
('employee-007', 'Zhang Wei', 'APPLE007', 'city-gd-gz', 'country-cn', 'zhang.wei@apple.com', 'Engineering', 'Machine Learning Engineer', '2023-04-22', 'NINGXIA', 'ACTIVE', 'client_001', 'system', 'system'),
('employee-008', 'Robert Brown', 'APPLE008', 'city-zj-nb', 'country-us', 'robert.brown@apple.com', 'Operations', 'Operations Manager', '2022-12-03', 'SINGAPORE', 'ACTIVE', 'client_001', 'system', 'system'),
('employee-009', 'Yuki Tanaka', 'APPLE009', 'city-js-wx', 'country-jp', 'yuki.tanaka@apple.com', 'Design', 'Industrial Designer', '2023-01-28', 'SINGAPORE', 'ACTIVE', 'client_001', 'system', 'system'),
('employee-010', 'Maria Rodriguez', 'APPLE010', 'city-gd-fs', 'country-es', 'maria.rodriguez@apple.com', 'Marketing', 'Brand Manager', '2022-10-15', 'GERMANY', 'ACTIVE', 'client_001', 'system', 'system'),
('employee-011', 'Alex Wilson', 'APPLE011', 'city-zj-wz', 'country-ca', 'alex.wilson@apple.com', 'Engineering', 'DevOps Engineer', '2023-05-08', 'SINGAPORE', 'ACTIVE', 'client_001', 'system', 'system'),
('employee-012', 'Sophie Martin', 'APPLE012', 'city-js-cz', 'country-fr', 'sophie.martin@apple.com', 'HR', 'HR Business Partner', '2022-07-20', 'GERMANY', 'ACTIVE', 'client_001', 'system', 'system'),
('employee-013', 'Chen Ming', 'APPLE013', 'city-gd-dg', 'country-cn', 'chen.ming@apple.com', 'Engineering', 'Quality Assurance Engineer', '2023-02-14', 'NINGXIA', 'ACTIVE', 'client_001', 'system', 'system'),
('employee-014', 'James Davis', 'APPLE014', 'city-zj-jx', 'country-au', 'james.davis@apple.com', 'Finance', 'Financial Analyst', '2022-11-25', 'SINGAPORE', 'ACTIVE', 'client_001', 'system', 'system'),
('employee-015', 'Anna Mueller', 'APPLE015', 'city-js-ntz', 'country-de', 'anna.mueller@apple.com', 'Legal', 'Legal Counsel', '2023-03-18', 'GERMANY', 'ACTIVE', 'client_001', 'system', 'system'),
('employee-016', 'Li Xiaoming', 'APPLE016', 'city-gd-zs', 'country-cn', 'li.xiaoming@apple.com', 'Engineering', 'Data Scientist', '2022-12-10', 'NINGXIA', 'ACTIVE', 'client_001', 'system', 'system'),
('employee-017', 'Thomas Anderson', 'APPLE017', 'city-zj-huz', 'country-se', 'thomas.anderson@apple.com', 'Product', 'Product Manager', '2023-04-05', 'GERMANY', 'ACTIVE', 'client_001', 'system', 'system'),

-- Tencent Holdings (client_005) employees - 17 employees
('employee-018', 'Wang Jiahao', 'TENCENT001', 'city-gd-sz', 'country-cn', 'wang.jiahao@tencent.com', 'Engineering', 'Backend Developer', '2023-01-12', 'NINGXIA', 'ACTIVE', 'client_005', 'system', 'system'),
('employee-019', 'Liu Meili', 'TENCENT002', 'city-gd-gz', 'country-cn', 'liu.meili@tencent.com', 'Product', 'Product Manager', '2022-10-08', 'NINGXIA', 'ACTIVE', 'client_005', 'system', 'system'),
('employee-020', 'Kevin Park', 'TENCENT003', 'city-js-sz', 'country-kr', 'kevin.park@tencent.com', 'Engineering', 'Frontend Developer', '2023-02-25', 'SINGAPORE', 'ACTIVE', 'client_005', 'system', 'system'),
('employee-021', 'Zhang Yifei', 'TENCENT004', 'city-zj-hz', 'country-cn', 'zhang.yifei@tencent.com', 'Design', 'UI/UX Designer', '2022-11-30', 'NINGXIA', 'ACTIVE', 'client_005', 'system', 'system'),
('employee-022', 'Hiroshi Sato', 'TENCENT005', 'city-gd-dg', 'country-jp', 'hiroshi.sato@tencent.com', 'Engineering', 'Game Developer', '2023-03-15', 'SINGAPORE', 'ACTIVE', 'client_005', 'system', 'system'),
('employee-023', 'Chen Xiaoli', 'TENCENT006', 'city-js-nj', 'country-cn', 'chen.xiaoli@tencent.com', 'Marketing', 'Digital Marketing Specialist', '2022-09-22', 'NINGXIA', 'ACTIVE', 'client_005', 'system', 'system'),
('employee-024', 'Ryan Thompson', 'TENCENT007', 'city-zj-nb', 'country-us', 'ryan.thompson@tencent.com', 'Engineering', 'Cloud Architect', '2023-04-18', 'SINGAPORE', 'ACTIVE', 'client_005', 'system', 'system'),
('employee-025', 'Xu Ting', 'TENCENT008', 'city-gd-fs', 'country-cn', 'xu.ting@tencent.com', 'Data', 'Data Analyst', '2022-12-05', 'NINGXIA', 'ACTIVE', 'client_005', 'system', 'system'),
('employee-026', 'Sophie Lee', 'TENCENT009', 'city-js-wx', 'country-sg', 'sophie.lee@tencent.com', 'Operations', 'Business Operations Manager', '2023-01-20', 'SINGAPORE', 'ACTIVE', 'client_005', 'system', 'system'),
('employee-027', 'Huang Zhiqiang', 'TENCENT010', 'city-zj-wz', 'country-cn', 'huang.zhiqiang@tencent.com', 'Engineering', 'Security Engineer', '2022-08-14', 'NINGXIA', 'ACTIVE', 'client_005', 'system', 'system'),
('employee-028', 'Daniel Kim', 'TENCENT011', 'city-gd-zh', 'country-kr', 'daniel.kim@tencent.com', 'Finance', 'Finance Manager', '2023-05-02', 'SINGAPORE', 'ACTIVE', 'client_005', 'system', 'system'),
('employee-029', 'Li Jing', 'TENCENT012', 'city-js-cz', 'country-cn', 'li.jing@tencent.com', 'HR', 'Talent Acquisition Specialist', '2022-10-28', 'NINGXIA', 'ACTIVE', 'client_005', 'system', 'system'),
('employee-030', 'Mark Johnson', 'TENCENT013', 'city-zj-jx', 'country-ca', 'mark.johnson@tencent.com', 'Engineering', 'AI Research Engineer', '2023-03-08', 'SINGAPORE', 'ACTIVE', 'client_005', 'system', 'system'),
('employee-031', 'Yang Mei', 'TENCENT014', 'city-gd-st', 'country-cn', 'yang.mei@tencent.com', 'Legal', 'Compliance Officer', '2022-11-15', 'NINGXIA', 'ACTIVE', 'client_005', 'system', 'system'),
('employee-032', 'Takeshi Yamamoto', 'TENCENT015', 'city-js-ntz', 'country-jp', 'takeshi.yamamoto@tencent.com', 'Product', 'Technical Product Manager', '2023-04-25', 'SINGAPORE', 'ACTIVE', 'client_005', 'system', 'system'),
('employee-033', 'Zhou Liang', 'TENCENT016', 'city-zj-huz', 'country-cn', 'zhou.liang@tencent.com', 'Engineering', 'Mobile Developer', '2022-12-18', 'NINGXIA', 'ACTIVE', 'client_005', 'system', 'system'),
('employee-034', 'Emily Chen', 'TENCENT017', 'city-js-yz', 'country-tw', 'emily.chen@tencent.com', 'Design', 'Visual Designer', '2023-02-10', 'SINGAPORE', 'ACTIVE', 'client_005', 'system', 'system'),

-- Samsung Electronics (client_008) employees - 16 employees
('employee-035', 'Park Seung-ho', 'SAMSUNG001', 'city-gd-sz', 'country-kr', 'park.seungho@samsung.com', 'Engineering', 'Hardware Engineer', '2023-01-08', 'SINGAPORE', 'ACTIVE', 'client_008', 'system', 'system'),
('employee-036', 'Kim Min-jung', 'SAMSUNG002', 'city-js-sz', 'country-kr', 'kim.minjung@samsung.com', 'R&D', 'Research Scientist', '2022-09-15', 'SINGAPORE', 'ACTIVE', 'client_008', 'system', 'system'),
('employee-037', 'Chen Hao', 'SAMSUNG003', 'city-gd-gz', 'country-cn', 'chen.hao@samsung.com', 'Manufacturing', 'Production Manager', '2023-02-28', 'NINGXIA', 'ACTIVE', 'client_008', 'system', 'system'),
('employee-038', 'Lee Soo-jin', 'SAMSUNG004', 'city-zj-hz', 'country-kr', 'lee.soojin@samsung.com', 'Design', 'Product Designer', '2022-11-12', 'SINGAPORE', 'ACTIVE', 'client_008', 'system', 'system'),
('employee-039', 'Raj Patel', 'SAMSUNG005', 'city-js-nj', 'country-in', 'raj.patel@samsung.com', 'Engineering', 'Software Engineer', '2023-03-20', 'SINGAPORE', 'ACTIVE', 'client_008', 'system', 'system'),
('employee-040', 'Choi Hyun-woo', 'SAMSUNG006', 'city-gd-dg', 'country-kr', 'choi.hyunwoo@samsung.com', 'Sales', 'Regional Sales Manager', '2022-08-05', 'SINGAPORE', 'ACTIVE', 'client_008', 'system', 'system'),
('employee-041', 'Wang Xin', 'SAMSUNG007', 'city-zj-nb', 'country-cn', 'wang.xin@samsung.com', 'Quality', 'Quality Control Engineer', '2023-04-12', 'NINGXIA', 'ACTIVE', 'client_008', 'system', 'system'),
('employee-042', 'Jung Eun-hye', 'SAMSUNG008', 'city-js-wx', 'country-kr', 'jung.eunhye@samsung.com', 'Marketing', 'Brand Marketing Manager', '2022-10-18', 'SINGAPORE', 'ACTIVE', 'client_008', 'system', 'system'),
('employee-043', 'Liu Gang', 'SAMSUNG009', 'city-gd-fs', 'country-cn', 'liu.gang@samsung.com', 'Supply Chain', 'Supply Chain Analyst', '2023-01-25', 'NINGXIA', 'ACTIVE', 'client_008', 'system', 'system'),
('employee-044', 'Yoon Jae-min', 'SAMSUNG010', 'city-zj-wz', 'country-kr', 'yoon.jaemin@samsung.com', 'Finance', 'Financial Controller', '2022-12-08', 'SINGAPORE', 'ACTIVE', 'client_008', 'system', 'system'),
('employee-045', 'Priya Sharma', 'SAMSUNG011', 'city-js-cz', 'country-in', 'priya.sharma@samsung.com', 'HR', 'HR Manager', '2023-05-15', 'SINGAPORE', 'ACTIVE', 'client_008', 'system', 'system'),
('employee-046', 'Kang Dong-hyun', 'SAMSUNG012', 'city-gd-zs', 'country-kr', 'kang.donghyun@samsung.com', 'Engineering', 'Chip Design Engineer', '2022-09-30', 'SINGAPORE', 'ACTIVE', 'client_008', 'system', 'system'),
('employee-047', 'Zhang Qiang', 'SAMSUNG013', 'city-zj-jx', 'country-cn', 'zhang.qiang@samsung.com', 'Operations', 'Operations Director', '2023-03-05', 'NINGXIA', 'ACTIVE', 'client_008', 'system', 'system'),
('employee-048', 'Oh Seung-min', 'SAMSUNG014', 'city-js-ntz', 'country-kr', 'oh.seungmin@samsung.com', 'Legal', 'Patent Attorney', '2022-11-22', 'SINGAPORE', 'ACTIVE', 'client_008', 'system', 'system'),
('employee-049', 'Ananya Singh', 'SAMSUNG015', 'city-gd-zh', 'country-in', 'ananya.singh@samsung.com', 'Product', 'Product Strategy Manager', '2023-04-08', 'SINGAPORE', 'ACTIVE', 'client_008', 'system', 'system'),
('employee-050', 'Lim Soo-hyun', 'SAMSUNG016', 'city-zj-huz', 'country-kr', 'lim.soohyun@samsung.com', 'R&D', 'Innovation Manager', '2022-12-15', 'SINGAPORE', 'ACTIVE', 'client_008', 'system', 'system');