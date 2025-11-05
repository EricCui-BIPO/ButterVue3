package com.i0.talent.application.dto.output;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

/**
 * 员工详情输出DTO
 *
 * 用于员工详情查看的输出数据封装
 * 包含完整的员工信息和相关统计数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDetailOutput {

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
     * 员工ID
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
     * 职位
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
     * 是否敏感数据
     */
    private Boolean sensitive;

    /**
     * 法律法规提示
     */
    private String legalNotice;

    /**
     * 工作年限（计算得出）
     */
    private Integer workYears;

    
    /**
     * 从领域实体转换为输出DTO
     *
     * @param employee 员工领域实体
     * @return 员工详情输出DTO
     */
    public static EmployeeDetailOutput fromEntity(com.i0.talent.domain.entities.Employee employee) {
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

        EmployeeDetailOutputBuilder builder = EmployeeDetailOutput.builder()
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
                .active(employee.isActive());

        // 计算工作年限
        if (employee.getJoinDate() != null) {
            LocalDate endDate = employee.getLeaveDate() != null ? employee.getLeaveDate() : LocalDate.now();
            builder.workYears(calculateWorkYears(employee.getJoinDate(), endDate));
        }

        // 判断是否为敏感信息
        builder.sensitive(isSensitiveData(employee.getDataLocation()));

        // 添加法律法规提示
        builder.legalNotice(getLegalNotice(employee.getDataLocation()));

        return builder.build();
    }

    /**
     * 计算工作年限
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 工作年限
     */
    private static Integer calculateWorkYears(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return 0;
        }
        return java.time.Period.between(startDate, endDate).getYears();
    }

    /**
     * 判断是否为敏感数据
     *
     * @param dataLocation 数据存储位置
     * @return 是否敏感
     */
    private static Boolean isSensitiveData(com.i0.talent.domain.enums.DataLocation dataLocation) {
        if (dataLocation == null) {
            return false;
        }
        // 根据数据存储位置判断敏感级别
        switch (dataLocation) {
            case GERMANY:
                return true; // 德国数据通常有更严格的隐私保护要求
            case SINGAPORE:
                return false; // 新加坡数据相对开放
            case NINGXIA:
                return false; // 宁夏数据为标准级别
            default:
                return false;
        }
    }

    /**
     * 获取法律法规提示信息
     *
     * @param dataLocation 数据存储位置
     * @return 法律法规提示信息
     */
    private static String getLegalNotice(com.i0.talent.domain.enums.DataLocation dataLocation) {
        if (dataLocation == null) {
            return "";
        }

        switch (dataLocation) {
            case GERMANY:
                return "注意：此员工数据存储在德国，受GDPR法规保护。处理此类数据需遵守欧盟数据保护条例，确保数据主体的知情权和被遗忘权。";
            case SINGAPORE:
                return "注意：此员工数据存储在新加坡，受PDPA法规保护。处理时需遵循新加坡个人数据保护法案的相关要求。";
            case NINGXIA:
                return "注意：此员工数据存储在中国宁夏，受中国个人信息保护法保护。处理时需确保符合国内数据安全和个人信息保护相关法规。";
            default:
                return "";
        }
    }
}