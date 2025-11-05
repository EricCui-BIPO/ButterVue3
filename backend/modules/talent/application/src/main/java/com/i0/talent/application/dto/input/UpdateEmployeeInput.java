package com.i0.talent.application.dto.input;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

/**
 * 更新员工输入DTO
 *
 * 用于员工更新操作的参数封装
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEmployeeInput {

    /**
     * 员工ID
     */
    @NotBlank(message = "员工ID不能为空")
    private String id;

    /**
     * 员工姓名
     */
    @NotBlank(message = "员工姓名不能为空")
    @Size(max = 100, message = "员工姓名不能超过100个字符")
    private String name;

    /**
     * 员工工号
     */
    @NotBlank(message = "员工工号不能为空")
    @Size(max = 50, message = "员工工号不能超过50个字符")
    private String employeeNumber;

    /**
     * 工作地点ID
     */
    private String workLocationId;

    /**
     * 国籍ID
     */
    private String nationalityId;

    /**
     * 邮箱地址
     */
    @NotBlank(message = "邮箱地址不能为空")
    @Email(message = "邮箱地址格式不正确")
    @Size(max = 100, message = "邮箱地址不能超过100个字符")
    private String email;

    /**
     * 部门
     */
    @NotBlank(message = "部门不能为空")
    @Size(max = 100, message = "部门不能超过100个字符")
    private String department;

    /**
     * 职位
     */
    @NotBlank(message = "职位不能为空")
    @Size(max = 100, message = "职位不能超过100个字符")
    private String position;

    /**
     * 入职日期
     */
    @NotNull(message = "入职日期不能为空")
    private LocalDate joinDate;

    /**
     * 离职日期（可选）
     */
    private LocalDate leaveDate;

    /**
     * 数据存储位置
     */
    @NotBlank(message = "数据存储位置不能为空")
    @Pattern(regexp = "NINGXIA|SINGAPORE|GERMANY", message = "数据存储位置只能是NINGXIA、SINGAPORE或GERMANY")
    private String dataLocation;

    /**
     * 所属客户ID
     */
    private String clientId;
}