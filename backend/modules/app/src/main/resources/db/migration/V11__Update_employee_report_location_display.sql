-- 更新员工报表中地点显示方式
-- 将 work_location_id 转换为可读的地点名称

-- 更新员工工作地点统计数据集的SQL查询
UPDATE datasets
SET sql_query = 'SELECT
    COALESCE(l.name, ''未知地点'') as location_name,
    e.work_location_id,
    e.data_location,
    COUNT(*) as total_employees,
    COUNT(CASE WHEN e.status = ''ACTIVE'' THEN 1 END) as active_employees,
    COUNT(DISTINCT e.nationality_id) as nationalities_count,
    COUNT(DISTINCT e.department) as departments_count
FROM employees e
LEFT JOIN locations l ON e.work_location_id = l.id AND l.is_deleted = 0
WHERE e.is_deleted = 0 AND e.work_location_id IS NOT NULL
GROUP BY e.work_location_id, e.data_location, l.name',
    updated_at = CURRENT_TIMESTAMP,
    updater = 'system'
WHERE id = 'dataset-employee-location-stats';

-- 更新工作地点维度指标的维度字段
UPDATE indicators
SET dimensions = '["location_name", "data_location"]',
    updated_at = CURRENT_TIMESTAMP,
    updater = 'system'
WHERE id = 'indicator-location-stats';

-- 更新工作地点分布图的维度字段
UPDATE charts
SET dimension = 'location_name',
    updated_at = CURRENT_TIMESTAMP,
    updater = 'system'
WHERE id = 'chart-location-distribution-bar';