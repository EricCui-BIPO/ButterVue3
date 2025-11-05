-- 更新员工报表中国籍显示方式
-- 将 nationality_id 转换为可读的国籍名称

-- 更新员工国籍统计数据集的SQL查询
UPDATE datasets
SET sql_query = 'SELECT
    COALESCE(l.name, ''未知国籍'') as nationality_name,
    e.nationality_id,
    COUNT(*) as total_employees,
    COUNT(CASE WHEN e.status = ''ACTIVE'' THEN 1 END) as active_employees,
    COUNT(DISTINCT e.work_location_id) as work_locations_count,
    COUNT(DISTINCT e.department) as departments_count
FROM employees e
LEFT JOIN locations l ON e.nationality_id = l.id AND l.is_deleted = 0 AND l.location_type = ''COUNTRY''
WHERE e.is_deleted = 0 AND e.nationality_id IS NOT NULL
GROUP BY e.nationality_id, l.name',
    updated_at = CURRENT_TIMESTAMP,
    updater = 'system'
WHERE id = 'dataset-employee-nationality-stats';

-- 更新国籍维度指标的维度字段
UPDATE indicators
SET dimensions = '["nationality_name"]',
    updated_at = CURRENT_TIMESTAMP,
    updater = 'system'
WHERE id = 'indicator-nationality-stats';

-- 更新员工国籍分布图的维度字段
UPDATE charts
SET dimension = 'nationality_name',
    updated_at = CURRENT_TIMESTAMP,
    updater = 'system'
WHERE id = 'chart-nationality-pie';