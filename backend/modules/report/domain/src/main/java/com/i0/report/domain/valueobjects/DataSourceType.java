package com.i0.report.domain.valueobjects;

/**
 * 数据源类型值对象
 */
public enum DataSourceType {
    MYSQL("mysql", "MySQL"),
    POSTGRESQL("postgresql", "PostgreSQL"),
    ORACLE("oracle", "Oracle"),
    SQLSERVER("sqlserver", "SQL Server"),
    H2("h2", "H2"),
    CLICKHOUSE("clickhouse", "ClickHouse");

    private final String code;
    private final String description;

    DataSourceType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 检查数据源类型代码是否有效
     */
    public static boolean isValid(String typeCode) {
        if (typeCode == null || typeCode.trim().isEmpty()) {
            return false;
        }

        for (DataSourceType type : DataSourceType.values()) {
            if (type.code.equals(typeCode.toLowerCase().trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据类型代码获取枚举
     */
    public static DataSourceType fromCode(String typeCode) {
        if (typeCode == null || typeCode.trim().isEmpty()) {
            throw new IllegalArgumentException("数据源类型代码不能为空");
        }

        for (DataSourceType type : DataSourceType.values()) {
            if (type.code.equals(typeCode.toLowerCase().trim())) {
                return type;
            }
        }
        throw new IllegalArgumentException("无效的数据源类型代码: " + typeCode);
    }

    /**
     * 检查是否为关系型数据库
     */
    public boolean isRelational() {
        return this == MYSQL || this == POSTGRESQL || this == ORACLE
            || this == SQLSERVER || this == H2;
    }

    /**
     * 检查是否为列式数据库
     */
    public boolean isColumnar() {
        return this == CLICKHOUSE;
    }

    /**
     * 检查是否支持实时查询
     */
    public boolean supportsRealTimeQuery() {
        return this != CLICKHOUSE;
    }

    /**
     * 检查是否为内存数据库
     */
    public boolean isInMemory() {
        return this == H2;
    }
}