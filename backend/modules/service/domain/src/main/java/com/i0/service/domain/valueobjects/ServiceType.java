package com.i0.service.domain.valueobjects;

import lombok.Getter;

/**
 * 服务类型枚举
 * 定义系统支持的四种服务类型
 */
@Getter
public enum ServiceType {

    /**
     * EOR - Employer of Record Service
     * 名义雇主服务
     */
    EOR("EOR", "Employer of Record Service", "名义雇主服务，提供合法雇佣关系管理"),

    /**
     * GPO - Global Payroll Outsourcing
     * 全球薪酬外包服务
     */
    GPO("GPO", "Global Payroll Outsourcing", "全球薪酬外包服务，提供多国薪酬计算和发放"),

    /**
     * Contractor - Independent Contractor Management
     * 独立承包商管理服务
     */
    CONTRACTOR("CONTRACTOR", "Independent Contractor Management", "独立承包商管理服务，提供合同和合规管理"),

    /**
     * SELF - Self-Employment Management
     * 自雇管理服务
     */
    SELF("SELF", "Self-Employment Management", "自雇管理服务，提供自雇人士的合规和税务管理");

    private final String code;
    private final String displayName;
    private final String description;

    ServiceType(String code, String displayName, String description) {
        this.code = code;
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * 根据代码查找服务类型
     * @param code 服务类型代码
     * @return 对应的服务类型
     * @throws IllegalArgumentException 如果代码无效
     */
    public static ServiceType fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Service type code cannot be null or empty");
        }

        for (ServiceType type : values()) {
            if (type.code.equals(code.trim().toUpperCase())) {
                return type;
            }
        }

        throw new IllegalArgumentException("Unknown service type code: " + code);
    }

    /**
     * 根据显示名称查找服务类型
     * @param displayName 显示名称
     * @return 对应的服务类型
     * @throws IllegalArgumentException 如果显示名称无效
     */
    public static ServiceType fromDisplayName(String displayName) {
        if (displayName == null || displayName.trim().isEmpty()) {
            throw new IllegalArgumentException("Service type display name cannot be null or empty");
        }

        for (ServiceType type : values()) {
            if (type.displayName.equals(displayName.trim())) {
                return type;
            }
        }

        throw new IllegalArgumentException("Unknown service type display name: " + displayName);
    }

    /**
     * 检查是否为EOR服务类型
     * @return 如果是EOR返回true
     */
    public boolean isEOR() {
        return this == EOR;
    }

    /**
     * 检查是否为GPO服务类型
     * @return 如果是GPO返回true
     */
    public boolean isGPO() {
        return this == GPO;
    }

    /**
     * 检查是否为Contractor服务类型
     * @return 如果是Contractor返回true
     */
    public boolean isContractor() {
        return this == CONTRACTOR;
    }

    /**
     * 检查是否为SELF服务类型
     * @return 如果是SELF返回true
     */
    public boolean isSELF() {
        return this == SELF;
    }

    /**
     * 检查是否为外包类服务（EOR、GPO）
     * @return 如果是外包类服务返回true
     */
    public boolean isOutsourcingService() {
        return this == EOR || this == GPO;
    }

    /**
     * 检查是否为管理类服务（Contractor、SELF）
     * @return 如果是管理类服务返回true
     */
    public boolean isManagementService() {
        return this == CONTRACTOR || this == SELF;
    }

    /**
     * 获取所有活跃的服务类型
     * @return 所有服务类型数组
     */
    public static ServiceType[] getAllActiveTypes() {
        return values();
    }

    /**
     * 验证代码是否有效
     * @param code 待验证的代码
     * @return 如果代码有效返回true
     */
    public static boolean isValidCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return false;
        }

        for (ServiceType type : values()) {
            if (type.code.equals(code.trim().toUpperCase())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return code + " - " + displayName;
    }
}