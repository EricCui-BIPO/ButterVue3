package com.i0.talent.application.dto.output;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * 员工分页输出DTO
 *
 * 专门用于分页查询的输出DTO，只包含实体字段，不包含分页元数据
 * 遵循架构规范：分页查询必须创建专门的{Entity}PageOutput类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeePageOutput {

    /**
     * 工作地点输出DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkLocationOutput {
        /**
         * 工作地点ID
         */
        private String id;

        /**
         * 工作地点名称
         */
        private String name;

        /**
         * 工作地点类型
         */
        private String type;

        /**
         * 工作地点ISO代码
         */
        private String isoCode;
    }

    /**
     * 国籍输出DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NationalityOutput {
        /**
         * 国籍ID
         */
        private String id;

        /**
         * 国籍名称
         */
        private String name;

        /**
         * 国籍ISO代码
         */
        private String isoCode;
    }

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
     * 工作地点信息
     */
    private WorkLocationOutput workLocation;

    /**
     * 国籍信息
     */
    private NationalityOutput nationality;

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
     * 从EmployeeOutput转换为PageOutput
     */
    public static EmployeePageOutput from(EmployeeOutput employeeOutput) {
        if (employeeOutput == null) {
            return null;
        }

        // 构建工作地点对象
        WorkLocationOutput workLocation = null;
        if (employeeOutput.getWorkLocationId() != null) {
            workLocation = WorkLocationOutput.builder()
                    .id(employeeOutput.getWorkLocationId())
                    .name(employeeOutput.getWorkLocationName())
                    .type(employeeOutput.getWorkLocationType())
                    .build();
        }

        // 构建国籍对象
        NationalityOutput nationality = null;
        if (employeeOutput.getNationalityId() != null) {
            nationality = NationalityOutput.builder()
                    .id(employeeOutput.getNationalityId())
                    .name(employeeOutput.getNationalityName())
                    .isoCode(employeeOutput.getNationalityIsoCode())
                    .build();
        }

        return EmployeePageOutput.builder()
                .id(employeeOutput.getId())
                .name(employeeOutput.getName())
                .employeeNumber(employeeOutput.getEmployeeNumber())
                .workLocation(workLocation)
                .nationality(nationality)
                .email(employeeOutput.getEmail())
                .department(employeeOutput.getDepartment())
                .position(employeeOutput.getPosition())
                .joinDate(employeeOutput.getJoinDate())
                .leaveDate(employeeOutput.getLeaveDate())
                .dataLocation(employeeOutput.getDataLocation())
                .status(employeeOutput.getStatus())
                .clientId(employeeOutput.getClientId())
                .active(employeeOutput.getActive())
                .build();
    }

    /**
     * 从领域实体直接创建分页输出DTO
     */
    public static EmployeePageOutput fromEntity(com.i0.talent.domain.entities.Employee employee) {
        if (employee == null) {
            return null;
        }

        com.i0.talent.domain.valueobjects.WorkLocation domainWorkLocation = employee.getWorkLocation();
        com.i0.talent.domain.valueobjects.Nationality domainNationality = employee.getNationality();

        // 构建工作地点对象
        WorkLocationOutput workLocation = null;
        if (domainWorkLocation != null) {
            workLocation = WorkLocationOutput.builder()
                    .id(domainWorkLocation.getLocationId())
                    .name(domainWorkLocation.getLocationName())
                    .type(domainWorkLocation.getLocationType())
                    .isoCode(domainWorkLocation.getIsoCode())
                    .build();
        }

        // 构建国籍对象
        NationalityOutput nationality = null;
        if (domainNationality != null) {
            nationality = NationalityOutput.builder()
                    .id(domainNationality.getCountryId())
                    .name(domainNationality.getCountryName())
                    .isoCode(domainNationality.getIsoCode())
                    .build();
        }

        return EmployeePageOutput.builder()
                .id(employee.getId())
                .name(employee.getName())
                .employeeNumber(employee.getEmployeeNumber())
                .workLocation(workLocation)
                .nationality(nationality)
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
     * 批量转换列表
     */
    public static List<EmployeePageOutput> fromEntities(List<com.i0.talent.domain.entities.Employee> employees) {
        if (employees == null || employees.isEmpty()) {
            return List.of();
        }

        return employees.stream()
                .map(EmployeePageOutput::fromEntity)
                .collect(java.util.stream.Collectors.toList());
    }
}