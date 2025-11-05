package com.i0.agents.domain.enums;

/**
 * 会话状态枚举
 * 定义聊天会话的状态转换
 */
public enum SessionStatus {
    /**
     * 活跃状态
     */
    ACTIVE("活跃"),

    /**
     * 暂停状态
     */
    PAUSED("暂停"),

    /**
     * 已完成状态
     */
    COMPLETED("已完成"),

    /**
     * 已关闭状态
     */
    CLOSED("已关闭");

    private final String description;

    SessionStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 检查是否可以转换为指定状态
     */
    public boolean canTransitionTo(SessionStatus newStatus) {
        switch (this) {
            case ACTIVE:
                return newStatus == PAUSED || newStatus == COMPLETED || newStatus == CLOSED;
            case PAUSED:
                return newStatus == ACTIVE || newStatus == CLOSED;
            case COMPLETED:
                return newStatus == CLOSED;
            case CLOSED:
                return false; // 已关闭状态不能转换到其他状态
            default:
                return false;
        }
    }

    /**
     * 判断是否为活跃状态
     */
    public boolean isActive() {
        return this == ACTIVE;
    }

    /**
     * 判断是否为终止状态
     */
    public boolean isTerminal() {
        return this == COMPLETED || this == CLOSED;
    }

    /**
     * 判断是否为可交互状态
     */
    public boolean isInteractive() {
        return this == ACTIVE || this == PAUSED;
    }
}