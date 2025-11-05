package com.i0.talent.domain.enums;

/**
 * 员工状态枚举
 *
 * 包含状态转换逻辑，避免"哑枚举"
 */
public enum EmployeeStatus {
    
    /**
     * 在职 - 员工处于正常工作状态
     */
    ACTIVE("在职", "员工处于正常工作状态"),
    
    /**
     * 已离职 - 员工已办理离职手续
     */
    TERMINATED("已离职", "员工已办理离职手续"),
    
    /**
     * 试用期 - 员工处于试用期
     */
    PROBATION("试用期", "员工处于试用期"),
    
    /**
     * 长期休假 - 员工处于长期休假状态
     */
    ON_LEAVE("长期休假", "员工处于长期休假状态"),
    
    /**
     * 暂停 - 员工工作状态暂停
     */
    SUSPENDED("暂停", "员工工作状态暂停");
    
    private final String displayName;
    private final String description;
    
    EmployeeStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 检查是否可以转换为指定状态
     */
    public boolean canTransitionTo(EmployeeStatus newStatus) {
        switch (this) {
            case ACTIVE:
                return newStatus == TERMINATED || newStatus == ON_LEAVE || newStatus == SUSPENDED;
            case TERMINATED:
                return newStatus == ACTIVE; // 重新入职
            case PROBATION:
                return newStatus == ACTIVE || newStatus == TERMINATED || newStatus == ON_LEAVE;
            case ON_LEAVE:
                return newStatus == ACTIVE || newStatus == TERMINATED || newStatus == SUSPENDED;
            case SUSPENDED:
                return newStatus == ACTIVE || newStatus == TERMINATED;
            default:
                return false;
        }
    }
    
    /**
     * 执行状态转换
     */
    public void validateTransition(EmployeeStatus newStatus) {
        if (!canTransitionTo(newStatus)) {
            throw new IllegalStateException(String.format("无法从状态 %s 转换为 %s", this.displayName, newStatus.displayName));
        }
    }
    
    /**
     * 检查是否为在职状态
     */
    public boolean isActive() {
        return this == ACTIVE || this == PROBATION;
    }
    
    /**
     * 检查是否可以执行业务操作
     */
    public boolean canPerformBusinessOperations() {
        return this == ACTIVE || this == PROBATION;
    }
}
