package com.i0.talent.application.dto.output;

import com.i0.talent.domain.valueobjects.WorkLocation;
import com.i0.talent.domain.valueobjects.Nationality;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

/**
 * 员工输出DTO
 *
 * 用于员工列表和详情输出的数据传输对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeOutput {

    /**
     * 员工唯一标识符
     */
    private String id;

    /**
     * 员工姓名
     */
    private String name;

    /**
     * 员工工号
     */
    private String employeeNumber;

    /**
     * 工作地点ID
     */
    private String workLocationId;

    /**
     * 工作地点名称
     */
    private String workLocationName;

    /**
     * 工作地点类型
     */
    private String workLocationType;

    /**
     * 国籍ID
     */
    private String nationalityId;

    /**
     * 国籍名称
     */
    private String nationalityName;

    /**
     * 国籍ISO代码
     */
    private String nationalityIsoCode;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 部门
     */
    private String department;

    /**
     * 岗位
     */
    private String position;

    /**
     * 入职日期
     */
    private LocalDate joinDate;

    /**
     * 离职日期（可选）
     */
    private LocalDate leaveDate;

    /**
     * 数据存储位置
     */
    private String dataLocation;

    /**
     * 员工状态
     */
    private String status;

    /**
     * 所属客户ID
     */
    private String clientId;

    /**
     * 是否激活
     */
    private Boolean active;

    /**
     * 创建时间
     */
    private String createdAt;

    /**
     * 更新时间
     */
    private String updatedAt;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 更新者
     */
    private String updater;

    /**
     * 从领域实体创建输出DTO
     *
     * @param employee 员工领域实体
     * @return 员工输出DTO
     */
    public static EmployeeOutput fromEntity(com.i0.talent.domain.entities.Employee employee) {
        if (employee == null) {
            return null;
        }

        WorkLocation workLocation = employee.getWorkLocation();
        Nationality nationality = employee.getNationality();

        return EmployeeOutput.builder()
                .id(employee.getId())
                .name(employee.getName())
                .employeeNumber(employee.getEmployeeNumber())
                .workLocationId(workLocation != null ? workLocation.getLocationId() : null)
                .workLocationName(workLocation != null ? workLocation.getLocationName() : null)
                .workLocationType(workLocation != null ? workLocation.getLocationType() : null)
                .nationalityId(nationality != null ? nationality.getCountryId() : null)
                .nationalityName(nationality != null ? nationality.getCountryName() : null)
                .nationalityIsoCode(nationality != null ? nationality.getIsoCode() : null)
                .email(employee.getEmail())
                .department(employee.getDepartment())
                .position(employee.getPosition())
                .joinDate(employee.getJoinDate())
                .leaveDate(employee.getLeaveDate())
                .dataLocation(employee.getDataLocation().name())
                .status(employee.getStatus().name())
                .clientId(employee.getClientId())
                .active(employee.isActive())
                .build();
    }

    /**
     * 从领域实体创建输出DTO（保持向后兼容）
     *
     * @param employee 员工领域实体
     * @return 员工输出DTO
     */
    public static EmployeeOutput from(com.i0.talent.domain.entities.Employee employee) {
        return fromEntity(employee);
    }
}