package com.i0.talent.application.dto.input;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

/**
 * 员工分页查询输入DTO
 *
 * 用于员工列表分页查询的参数封装
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeePageInput {

    /**
     * 页码（从0开始）
     */
    @Builder.Default
    @Min(value = 0, message = "页码不能小于0")
    private Integer page = 0;

    /**
     * 每页大小
     */
    @Builder.Default
    @Min(value = 1, message = "每页大小不能小于1")
    @Max(value = 100, message = "每页大小不能大于100")
    private Integer size = 20;

    /**
     * 搜索关键词（按姓名、邮箱、工号搜索）
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
    @Pattern(regexp = "ASC|DESC", message = "排序方向只能是ASC或DESC")
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
    @Builder.Default
    private Boolean excludeTerminated = false;

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