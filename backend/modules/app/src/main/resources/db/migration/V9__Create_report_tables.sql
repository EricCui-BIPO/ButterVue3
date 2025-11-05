-- 创建报表相关数据表
-- 报表模块数据库表结构

-- 创建数据集表
CREATE TABLE datasets (
    id VARCHAR(36) PRIMARY KEY COMMENT '数据集唯一标识符',
    name VARCHAR(100) NOT NULL COMMENT '数据集名称',
    description TEXT COMMENT '数据集描述',
    sql_query TEXT NOT NULL COMMENT '原始SQL查询语句',
    filters TEXT COMMENT '数据集过滤条件（JSON格式）',
    data_source_type VARCHAR(20) NOT NULL DEFAULT 'mysql' COMMENT '数据源类型',
    update_strategy VARCHAR(20) NOT NULL DEFAULT 'real_time' COMMENT '更新策略',
    update_interval INT COMMENT '更新间隔（分钟）',
    enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用',

    -- 审计字段
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    creator_id VARCHAR(36) COMMENT '创建者ID',
    updater_id VARCHAR(36) COMMENT '更新者ID',
    creator VARCHAR(100) COMMENT '创建者姓名',
    updater VARCHAR(100) COMMENT '更新者姓名',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标记 (0=未删除, 1=已删除)'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据集配置表';

-- 创建数据集表索引
CREATE INDEX idx_dataset_name ON datasets (name);
CREATE INDEX idx_dataset_enabled ON datasets (enabled);
CREATE INDEX idx_dataset_data_source_type ON datasets (data_source_type);
CREATE INDEX idx_dataset_created_at ON datasets (created_at);
CREATE INDEX idx_dataset_is_deleted ON datasets (is_deleted);

-- 创建指标表
CREATE TABLE indicators (
    id VARCHAR(36) PRIMARY KEY COMMENT '指标唯一标识符',
    name VARCHAR(100) NOT NULL COMMENT '指标名称',
    description TEXT COMMENT '指标描述',
    dataset_id VARCHAR(36) NOT NULL COMMENT '引用的数据集ID',
    calculation VARCHAR(200) NOT NULL COMMENT '计算表达式',
    dimensions TEXT COMMENT '维度字段列表（JSON格式）',
    filters TEXT COMMENT '指标过滤条件（JSON格式）',
    type VARCHAR(20) NOT NULL COMMENT '指标类型',
    unit VARCHAR(20) COMMENT '数据单位',
    format_pattern VARCHAR(50) COMMENT '数据格式化模式',
    enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用',

    -- 审计字段
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    creator_id VARCHAR(36) COMMENT '创建者ID',
    updater_id VARCHAR(36) COMMENT '更新者ID',
    creator VARCHAR(100) COMMENT '创建者姓名',
    updater VARCHAR(100) COMMENT '更新者姓名',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标记 (0=未删除, 1=已删除)'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='指标配置表';

-- 创建指标表索引
CREATE INDEX idx_indicator_name ON indicators (name);
CREATE INDEX idx_indicator_dataset_id ON indicators (dataset_id);
CREATE INDEX idx_indicator_type ON indicators (type);
CREATE INDEX idx_indicator_enabled ON indicators (enabled);
CREATE INDEX idx_indicator_created_at ON indicators (created_at);
CREATE INDEX idx_indicator_is_deleted ON indicators (is_deleted);

-- 创建指标表外键约束
ALTER TABLE indicators ADD CONSTRAINT fk_indicators_datasets
    FOREIGN KEY (dataset_id) REFERENCES datasets(id) ON DELETE CASCADE;

-- 创建图表表
CREATE TABLE charts (
    id VARCHAR(36) PRIMARY KEY COMMENT '图表唯一标识符',
    name VARCHAR(100) NOT NULL COMMENT '图表名称',
    description TEXT COMMENT '图表描述',
    type VARCHAR(20) NOT NULL COMMENT '图表类型',
    indicator_id VARCHAR(36) NOT NULL COMMENT '绑定的指标ID',
    dimension VARCHAR(100) COMMENT '主维度字段',
    filters TEXT COMMENT '图表过滤条件（JSON格式）',
    title VARCHAR(200) COMMENT '图表标题',
    x_axis_label VARCHAR(50) COMMENT 'X轴标签',
    y_axis_label VARCHAR(50) COMMENT 'Y轴标签',
    width INT COMMENT '图表宽度',
    height INT COMMENT '图表高度',
    config TEXT COMMENT '图表配置（JSON格式）',
    enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用',

    -- 审计字段
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    creator_id VARCHAR(36) COMMENT '创建者ID',
    updater_id VARCHAR(36) COMMENT '更新者ID',
    creator VARCHAR(100) COMMENT '创建者姓名',
    updater VARCHAR(100) COMMENT '更新者姓名',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标记 (0=未删除, 1=已删除)'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图表配置表';

-- 创建图表表索引
CREATE INDEX idx_chart_name ON charts (name);
CREATE INDEX idx_chart_type ON charts (type);
CREATE INDEX idx_chart_indicator_id ON charts (indicator_id);
CREATE INDEX idx_chart_enabled ON charts (enabled);
CREATE INDEX idx_chart_created_at ON charts (created_at);
CREATE INDEX idx_chart_is_deleted ON charts (is_deleted);

-- 创建图表表外键约束
ALTER TABLE charts ADD CONSTRAINT fk_charts_indicators
    FOREIGN KEY (indicator_id) REFERENCES indicators(id) ON DELETE CASCADE;

-- 创建报表表
CREATE TABLE reports (
    id VARCHAR(36) PRIMARY KEY COMMENT '报表唯一标识符',
    name VARCHAR(100) NOT NULL COMMENT '报表名称',
    description TEXT COMMENT '报表描述',
    charts TEXT COMMENT '报表包含的图表列表（JSON格式）',
    filters TEXT COMMENT '报表全局过滤条件（JSON格式）',
    layout TEXT COMMENT '报表布局配置（JSON格式）',
    refresh_interval INT COMMENT '刷新间隔（分钟）',
    status VARCHAR(20) NOT NULL DEFAULT 'draft' COMMENT '报表状态',
    theme VARCHAR(20) NOT NULL DEFAULT 'light' COMMENT '报表主题',
    tags VARCHAR(200) COMMENT '报表标签',
    category VARCHAR(50) COMMENT '报表分类',
    public_access TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否公开访问',
    creator VARCHAR(100) COMMENT '创建者',
    enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用',

    -- 审计字段
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    creator_id VARCHAR(36) COMMENT '创建者ID',
    updater_id VARCHAR(36) COMMENT '更新者ID',
    updater VARCHAR(100) COMMENT '更新者姓名',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标记 (0=未删除, 1=已删除)'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='报表配置表';

-- 创建报表表索引
CREATE INDEX idx_report_name ON reports (name);
CREATE INDEX idx_report_status ON reports (status);
CREATE INDEX idx_report_category ON reports (category);
CREATE INDEX idx_report_enabled ON reports (enabled);
CREATE INDEX idx_report_public_access ON reports (public_access);
CREATE INDEX idx_report_created_at ON reports (created_at);
CREATE INDEX idx_report_is_deleted ON reports (is_deleted);

-- 创建报表图表关联表（多对多关系）
CREATE TABLE report_charts (
    id VARCHAR(36) PRIMARY KEY COMMENT '关联记录唯一标识符',
    report_id VARCHAR(36) NOT NULL COMMENT '报表ID',
    chart_id VARCHAR(36) NOT NULL COMMENT '图表ID',
    position_x INT DEFAULT 0 COMMENT '图表X坐标位置',
    position_y INT DEFAULT 0 COMMENT '图表Y坐标位置',
    width INT DEFAULT 4 COMMENT '图表宽度（栅格单位）',
    height INT DEFAULT 3 COMMENT '图表高度（栅格单位）',

    -- 审计字段
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标记 (0=未删除, 1=已删除)'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='报表图表关联表';

-- 创建报表图表关联表索引
CREATE INDEX idx_report_charts_report_id ON report_charts (report_id);
CREATE INDEX idx_report_charts_chart_id ON report_charts (chart_id);
CREATE INDEX idx_report_charts_is_deleted ON report_charts (is_deleted);

-- 创建报表图表关联表外键约束
ALTER TABLE report_charts ADD CONSTRAINT fk_report_charts_reports
    FOREIGN KEY (report_id) REFERENCES reports(id) ON DELETE CASCADE;

ALTER TABLE report_charts ADD CONSTRAINT fk_report_charts_charts
    FOREIGN KEY (chart_id) REFERENCES charts(id) ON DELETE CASCADE;

-- 创建报表图表关联表唯一约束
ALTER TABLE report_charts ADD CONSTRAINT uk_report_chart
    UNIQUE (report_id, chart_id, is_deleted);

-- 插入员工报表相关的示例数据

-- 1. 员工基础数据集
INSERT INTO datasets (id, name, description, sql_query, creator, updater) VALUES
('dataset-employee-basic', '员工基础数据集', '员工基础信息查询，包含员工的基本信息用于统计分析',
'SELECT
    id,
    name,
    employee_number,
    department,
    position,
    nationality_id,
    work_location_id,
    status,
    join_date,
    leave_date,
    client_id,
    data_location
FROM employees
WHERE is_deleted = 0', 'system', 'system');

-- 2. 员工部门统计数据集
INSERT INTO datasets (id, name, description, sql_query, creator, updater) VALUES
('dataset-employee-dept-stats', '员工部门统计数据集', '按部门统计员工数量、入职离职等数据',
'SELECT
    department,
    COUNT(*) as total_employees,
    COUNT(CASE WHEN status = ''ACTIVE'' THEN 1 END) as active_employees,
    COUNT(CASE WHEN status = ''INACTIVE'' THEN 1 END) as inactive_employees,
    COUNT(CASE WHEN join_date >= DATE_SUB(NOW(), INTERVAL 30 DAY) THEN 1 END) as new_hires_30d,
    COUNT(CASE WHEN leave_date >= DATE_SUB(NOW(), INTERVAL 30 DAY) THEN 1 END) as departures_30d,
    COUNT(CASE WHEN join_date >= DATE_SUB(NOW(), INTERVAL 90 DAY) THEN 1 END) as new_hires_90d
FROM employees
WHERE is_deleted = 0 AND department IS NOT NULL
GROUP BY department', 'system', 'system');

-- 3. 员工工作地点统计数据集
INSERT INTO datasets (id, name, description, sql_query, creator, updater) VALUES
('dataset-employee-location-stats', '员工工作地点统计数据集', '按工作地点统计员工分布情况',
'SELECT
    work_location_id,
    data_location,
    COUNT(*) as total_employees,
    COUNT(CASE WHEN status = ''ACTIVE'' THEN 1 END) as active_employees,
    COUNT(DISTINCT nationality_id) as nationalities_count,
    COUNT(DISTINCT department) as departments_count
FROM employees
WHERE is_deleted = 0 AND work_location_id IS NOT NULL
GROUP BY work_location_id, data_location', 'system', 'system');

-- 4. 员工国籍统计数据集
INSERT INTO datasets (id, name, description, sql_query, creator, updater) VALUES
('dataset-employee-nationality-stats', '员工国籍统计数据集', '按国籍统计员工分布情况',
'SELECT
    nationality_id,
    COUNT(*) as total_employees,
    COUNT(CASE WHEN status = ''ACTIVE'' THEN 1 END) as active_employees,
    COUNT(DISTINCT work_location_id) as work_locations_count,
    COUNT(DISTINCT department) as departments_count
FROM employees
WHERE is_deleted = 0 AND nationality_id IS NOT NULL
GROUP BY nationality_id', 'system', 'system');

-- 5. 员工入职趋势数据集
INSERT INTO datasets (id, name, description, sql_query, creator, updater) VALUES
('dataset-employee-hiring-trend', '员工入职趋势数据集', '按月份统计员工入职趋势',
'SELECT
    DATE_FORMAT(join_date, ''%Y-%m'') as hire_month,
    COUNT(*) as new_hires,
    COUNT(CASE WHEN status = ''ACTIVE'' THEN 1 END) as active_hires,
    COUNT(DISTINCT department) as departments_count
FROM employees
WHERE is_deleted = 0 AND join_date IS NOT NULL
GROUP BY DATE_FORMAT(join_date, ''%Y-%m'')
ORDER BY hire_month DESC
LIMIT 12', 'system', 'system');

-- 6. 员工状态分布数据集
INSERT INTO datasets (id, name, description, sql_query, creator, updater) VALUES
('dataset-employee-status-distribution', '员工状态分布数据集', '按状态统计员工分布情况',
'SELECT
    status,
    COUNT(*) as total_employees,
    COUNT(CASE WHEN join_date >= DATE_SUB(NOW(), INTERVAL 30 DAY) THEN 1 END) as new_hires_30d,
    COUNT(DISTINCT department) as departments_count,
    COUNT(DISTINCT work_location_id) as work_locations_count
FROM employees
WHERE is_deleted = 0
GROUP BY status', 'system', 'system');

-- 7. 员工数据位置分布数据集
INSERT INTO datasets (id, name, description, sql_query, creator, updater) VALUES
('dataset-employee-data-location-stats', '员工数据位置分布数据集', '按数据存储位置统计员工分布情况',
'SELECT
    data_location,
    COUNT(*) as total_employees,
    COUNT(CASE WHEN status = ''ACTIVE'' THEN 1 END) as active_employees,
    COUNT(DISTINCT department) as departments_count,
    COUNT(DISTINCT work_location_id) as work_locations_count
FROM employees
WHERE is_deleted = 0 AND data_location IS NOT NULL
GROUP BY data_location', 'system', 'system');

-- 插入指标数据 - 侧重维度统计分析

-- 1. 员工总数指标
INSERT INTO indicators (id, name, description, dataset_id, calculation, type, unit, creator, updater) VALUES
('indicator-total-employees', '员工总数', '当前在职员工总数', 'dataset-employee-basic', 'COUNT(*)', 'count', '人', 'system', 'system');

-- 2. 部门维度指标
INSERT INTO indicators (id, name, description, dataset_id, calculation, dimensions, type, unit, creator, updater) VALUES
('indicator-dept-stats', '部门员工统计', '按部门统计员工分布情况', 'dataset-employee-dept-stats', 'total_employees', '["department"]', 'count', '人', 'system', 'system');

-- 3. 部门活跃员工指标
INSERT INTO indicators (id, name, description, dataset_id, calculation, dimensions, type, unit, creator, updater) VALUES
('indicator-dept-active-stats', '部门活跃员工统计', '按部门统计活跃员工分布情况', 'dataset-employee-dept-stats', 'active_employees', '["department"]', 'count', '人', 'system', 'system');

-- 4. 工作地点维度指标
INSERT INTO indicators (id, name, description, dataset_id, calculation, dimensions, type, unit, creator, updater) VALUES
('indicator-location-stats', '工作地点员工统计', '按工作地点统计员工分布情况', 'dataset-employee-location-stats', 'total_employees', '["work_location_id", "data_location"]', 'count', '人', 'system', 'system');

-- 5. 国籍维度指标
INSERT INTO indicators (id, name, description, dataset_id, calculation, dimensions, type, unit, creator, updater) VALUES
('indicator-nationality-stats', '国籍员工统计', '按国籍统计员工分布情况', 'dataset-employee-nationality-stats', 'total_employees', '["nationality_id"]', 'count', '人', 'system', 'system');

-- 6. 员工状态分布指标
INSERT INTO indicators (id, name, description, dataset_id, calculation, dimensions, type, unit, creator, updater) VALUES
('indicator-status-distribution', '员工状态分布统计', '按状态统计员工分布情况', 'dataset-employee-status-distribution', 'total_employees', '["status"]', 'count', '人', 'system', 'system');

-- 7. 数据位置分布指标
INSERT INTO indicators (id, name, description, dataset_id, calculation, dimensions, type, unit, creator, updater) VALUES
('indicator-data-location-stats', '数据位置分布统计', '按数据存储位置统计员工分布情况', 'dataset-employee-data-location-stats', 'total_employees', '["data_location"]', 'count', '人', 'system', 'system');

-- 8. 入职趋势指标
INSERT INTO indicators (id, name, description, dataset_id, calculation, dimensions, type, unit, creator, updater) VALUES
('indicator-hiring-trend', '入职趋势统计', '按月份统计入职趋势', 'dataset-employee-hiring-trend', 'new_hires', '["hire_month"]', 'count', '人', 'system', 'system');

-- 9. 部门数量指标
INSERT INTO indicators (id, name, description, dataset_id, calculation, type, unit, creator, updater) VALUES
('indicator-departments-count', '部门数量', '公司部门总数', 'dataset-employee-dept-stats', 'COUNT(*)', 'count', '个', 'system', 'system');

-- 10. 平均部门人数指标
INSERT INTO indicators (id, name, description, dataset_id, calculation, type, unit, creator, updater) VALUES
('indicator-avg-dept-size', '平均部门人数', '各部门平均员工数量', 'dataset-employee-dept-stats', 'AVG(total_employees)', 'avg', '人', 'system', 'system');

-- 插入图表数据 - 与指标正确关联

-- 1. 员工状态分布饼图
INSERT INTO charts (id, name, description, type, indicator_id, dimension, title, config, creator, updater) VALUES
('chart-employee-status-pie', '员工状态分布', '显示员工状态分布的饼图', 'pie', 'indicator-status-distribution', 'status', '员工状态分布',
'{"colors": ["#5470c6", "#91cc75", "#fac858", "#ee6666", "#73c0de"], "legend": {"position": "right"}, "series": {"radius": ["40%", "70%"]}}', 'system', 'system');

-- 2. 部门人数柱状图
INSERT INTO charts (id, name, description, type, indicator_id, dimension, title, x_axis_label, y_axis_label, config, creator, updater) VALUES
('chart-department-size-bar', '部门人数分布', '显示各部门员工数量的柱状图', 'bar', 'indicator-dept-stats', 'department', '部门人数分布', '部门', '人数',
'{"color": "#5470c6", "grid": {"left": "3%", "right": "4%", "bottom": "3%", "containLabel": true}}', 'system', 'system');

-- 3. 工作地点分布图
INSERT INTO charts (id, name, description, type, indicator_id, dimension, title, x_axis_label, y_axis_label, config, creator, updater) VALUES
('chart-location-distribution-bar', '工作地点分布', '显示各工作地点员工分布情况', 'bar', 'indicator-location-stats', 'work_location_id', '工作地点分布', '工作地点', '员工数',
'{"color": "#91cc75", "grid": {"left": "3%", "right": "4%", "bottom": "3%", "containLabel": true}}', 'system', 'system');

-- 4. 员工国籍分布饼图
INSERT INTO charts (id, name, description, type, indicator_id, dimension, title, config, creator, updater) VALUES
('chart-nationality-pie', '员工国籍分布', '显示员工国籍分布的饼图', 'pie', 'indicator-nationality-stats', 'nationality_id', '员工国籍分布',
'{"colors": ["#5470c6", "#91cc75", "#fac858", "#ee6666", "#73c0de", "#3ba272", "#fc8452", "#9a60b4"], "legend": {"position": "bottom"}}', 'system', 'system');

-- 5. 入职趋势折线图
INSERT INTO charts (id, name, description, type, indicator_id, dimension, title, x_axis_label, y_axis_label, config, creator, updater) VALUES
('chart-hiring-trend-line', '入职趋势分析', '显示近12个月入职趋势的折线图', 'line', 'indicator-hiring-trend', 'hire_month', '入职趋势分析', '月份', '入职人数',
'{"color": "#5470c6", "smooth": true, "grid": {"left": "3%", "right": "4%", "bottom": "3%", "containLabel": true}}', 'system', 'system');

-- 6. 数据位置分布饼图
INSERT INTO charts (id, name, description, type, indicator_id, dimension, title, config, creator, updater) VALUES
('chart-data-location-pie', '数据位置分布', '显示数据存储位置分布的饼图', 'pie', 'indicator-data-location-stats', 'data_location', '数据位置分布',
'{"colors": ["#5470c6", "#91cc75", "#fac858", "#ee6666"], "legend": {"position": "bottom"}}', 'system', 'system');

-- 7. 部门活跃员工柱状图
INSERT INTO charts (id, name, description, type, indicator_id, dimension, title, x_axis_label, y_axis_label, config, creator, updater) VALUES
('chart-dept-active-bar', '部门活跃员工分布', '显示各部门活跃员工数量的柱状图', 'bar', 'indicator-dept-active-stats', 'department', '部门活跃员工分布', '部门', '活跃人数',
'{"color": "#73c0de", "grid": {"left": "3%", "right": "4%", "bottom": "3%", "containLabel": true}}', 'system', 'system');

-- 插入报表数据

-- 1. 员工概览报表
INSERT INTO reports (id, name, description, status, category, creator, enabled) VALUES
('report-employee-overview', '员工概览报表', '公司员工整体情况概览，包含员工总数、状态分布、部门结构等关键指标', 'published', '人力资源', 'system', 1);

-- 2. 员工分布报表
INSERT INTO reports (id, name, description, status, category, creator, enabled) VALUES
('report-employee-distribution', '员工分布报表', '员工在不同维度上的分布情况，包括地域、部门、国籍等分布统计', 'published', '人力资源', 'system', 1);

-- 3. 员工趋势报表
INSERT INTO reports (id, name, description, status, category, creator, enabled) VALUES
('report-employee-trends', '员工趋势报表', '员工入职、离职等趋势分析，帮助了解人员流动情况', 'published', '人力资源', 'system', 1);

-- 插入报表图表关联数据

-- 员工概览报表的图表
INSERT INTO report_charts (id, report_id, chart_id, position_x, position_y, width, height) VALUES
('rc-001', 'report-employee-overview', 'chart-employee-status-pie', 0, 0, 6, 4);
INSERT INTO report_charts (id, report_id, chart_id, position_x, position_y, width, height) VALUES
('rc-002', 'report-employee-overview', 'chart-department-size-bar', 6, 0, 12, 4);
INSERT INTO report_charts (id, report_id, chart_id, position_x, position_y, width, height) VALUES
('rc-003', 'report-employee-overview', 'chart-location-distribution-bar', 0, 4, 9, 4);
INSERT INTO report_charts (id, report_id, chart_id, position_x, position_y, width, height) VALUES
('rc-004', 'report-employee-overview', 'chart-nationality-pie', 9, 4, 9, 4);

-- 员工分布报表的图表
INSERT INTO report_charts (id, report_id, chart_id, position_x, position_y, width, height) VALUES
('rc-005', 'report-employee-distribution', 'chart-department-size-bar', 0, 0, 12, 5);
INSERT INTO report_charts (id, report_id, chart_id, position_x, position_y, width, height) VALUES
('rc-006', 'report-employee-distribution', 'chart-location-distribution-bar', 0, 5, 12, 5);
INSERT INTO report_charts (id, report_id, chart_id, position_x, position_y, width, height) VALUES
('rc-007', 'report-employee-distribution', 'chart-nationality-pie', 0, 10, 9, 4);
INSERT INTO report_charts (id, report_id, chart_id, position_x, position_y, width, height) VALUES
('rc-009', 'report-employee-distribution', 'chart-data-location-pie', 9, 10, 9, 4);

-- 员工趋势报表的图表
INSERT INTO report_charts (id, report_id, chart_id, position_x, position_y, width, height) VALUES
('rc-008', 'report-employee-trends', 'chart-hiring-trend-line', 0, 0, 18, 6);

-- 员工状态报表的图表
INSERT INTO reports (id, name, description, status, category, creator, enabled) VALUES
('report-employee-status', '员工状态报表', '员工状态分布统计，包含活跃度分析和数据位置分布', 'published', '人力资源', 'system', 1);

INSERT INTO report_charts (id, report_id, chart_id, position_x, position_y, width, height) VALUES
('rc-010', 'report-employee-status', 'chart-employee-status-pie', 0, 0, 9, 5);
INSERT INTO report_charts (id, report_id, chart_id, position_x, position_y, width, height) VALUES
('rc-011', 'report-employee-status', 'chart-dept-active-bar', 9, 0, 9, 5);
INSERT INTO report_charts (id, report_id, chart_id, position_x, position_y, width, height) VALUES
('rc-012', 'report-employee-status', 'chart-data-location-pie', 0, 5, 18, 4);