package com.i0.talent.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 员工分页查询参数
 *
 * 在Domain层定义，用于Repository接口的分页查询方法
 * 避免Domain层依赖Application层的DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeePageQuery {

    /**
     * 页码（从0开始）
     */
    private Integer page;

    /**
     * 每页大小
     */
    private Integer size;

    /**
     * 搜索关键词
     */
    private String keyword;

    /**
     * 部门筛选
     */
    private String department;

    /**
     * 工作地点筛选
     */
    private String workLocation;

    /**
     * 国籍筛选
     */
    private String nationality;

    /**
     * 员工状态筛选
     */
    private String status;

    /**
     * 数据存储位置筛选
     */
    private String dataLocation;

    /**
     * 是否只查询激活的员工
     */
    private Boolean activeOnly;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序方向（ASC/DESC）
     */
    private String sortDirection;

    /**
     * 入职日期范围筛选 - 开始日期
     */
    private LocalDate joinDateFrom;

    /**
     * 入职日期范围筛选 - 结束日期
     */
    private LocalDate joinDateTo;

    /**
     * 离职日期范围筛选 - 开始日期
     */
    private LocalDate leaveDateFrom;

    /**
     * 离职日期范围筛选 - 结束日期
     */
    private LocalDate leaveDateTo;

    /**
     * 职位筛选
     */
    private String position;

    /**
     * 员工类型筛选（正式、实习、外包等）
     */
    private String employeeType;

    /**
     * 是否排除已离职员工
     */
    private Boolean excludeTerminated;

    /**
     * 邮箱域名筛选
     */
    private String emailDomain;

    /**
     * 工号前缀筛选
     */
    private String employeeNumberPrefix;

    /**
     * 所属客户ID筛选
     */
    private String clientId;
}