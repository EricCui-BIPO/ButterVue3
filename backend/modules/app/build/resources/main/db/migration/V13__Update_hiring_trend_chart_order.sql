-- 更新入职趋势图表的排序方式
-- 将时间排序从降序改为升序，使图表按时间顺序显示

-- 更新员工入职趋势数据集的SQL查询
UPDATE datasets
SET sql_query = 'SELECT
    DATE_FORMAT(join_date, ''%Y-%m'') as hire_month,
    COUNT(*) as new_hires,
    COUNT(CASE WHEN status = ''ACTIVE'' THEN 1 END) as active_hires,
    COUNT(DISTINCT department) as departments_count
FROM employees
WHERE is_deleted = 0 AND join_date IS NOT NULL
GROUP BY DATE_FORMAT(join_date, ''%Y-%m'')
ORDER BY hire_month ASC
LIMIT 12',
    updated_at = CURRENT_TIMESTAMP,
    updater = 'system'
WHERE id = 'dataset-employee-hiring-trend';