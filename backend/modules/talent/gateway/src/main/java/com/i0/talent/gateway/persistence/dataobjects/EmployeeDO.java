package com.i0.talent.gateway.persistence.dataobjects;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 员工数据对象 (Data Object)
 *
 * 用于数据库映射，包含审计字段和持久化注解
 * 遵循 Gateway 层规范，继承 MyBatis-Plus 特性
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("employees")
public class EmployeeDO {

    /**
     * 员工唯一标识符
     */
    @TableId("id")
    private String id;

    /**
     * 员工姓名
     */
    @TableField("name")
    private String name;

    /**
     * 员工工号
     */
    @TableField("employee_number")
    private String employeeNumber;

    /**
     * 工作地点ID
     */
    @TableField("work_location_id")
    private String workLocationId;
    /**
     * 国籍ID
     */
    @TableField("nationality_id")
    private String nationalityId;

    /**
     * 邮箱地址
     */
    @TableField("email")
    private String email;

    /**
     * 部门
     */
    @TableField("department")
    private String department;

    /**
     * 岗位
     */
    @TableField("position")
    private String position;

    /**
     * 入职日期
     */
    @TableField("join_date")
    private LocalDateTime joinDate;

    /**
     * 离职日期（可选）
     */
    @TableField("leave_date")
    private LocalDateTime leaveDate;

    /**
     * 数据存储位置
     */
    @TableField("data_location")
    private String dataLocation;

    /**
     * 员工状态
     */
    @TableField("status")
    private String status;

    /**
     * 所属客户ID
     */
    @TableField("client_id")
    private String clientId;

    /**
     * 逻辑删除标记
     */
    @TableLogic(value = "false", delval = "true")
    @TableField("is_deleted")
    private Boolean isDeleted;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;

    /**
     * 创建者ID
     */
    @TableField("creator_id")
    private String creatorId;

    /**
     * 更新者ID
     */
    @TableField("updater_id")
    private String updaterId;

    /**
     * 创建者姓名
     */
    @TableField("creator")
    private String creator;

    /**
     * 更新者姓名
     */
    @TableField("updater")
    private String updater;
}