package com.i0.report.domain.valueobjects;

/**
 * 更新策略值对象
 */
public enum UpdateStrategy {
    REAL_TIME("real_time", "实时更新"),
    SCHEDULED("scheduled", "定时更新"),
    MANUAL("manual", "手动更新");

    private final String code;
    private final String description;

    UpdateStrategy(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 检查更新策略代码是否有效
     */
    public static boolean isValid(String strategyCode) {
        if (strategyCode == null || strategyCode.trim().isEmpty()) {
            return false;
        }

        for (UpdateStrategy strategy : UpdateStrategy.values()) {
            if (strategy.code.equals(strategyCode.toLowerCase().trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据策略代码获取枚举
     */
    public static UpdateStrategy fromCode(String strategyCode) {
        if (strategyCode == null || strategyCode.trim().isEmpty()) {
            throw new IllegalArgumentException("更新策略代码不能为空");
        }

        for (UpdateStrategy strategy : UpdateStrategy.values()) {
            if (strategy.code.equals(strategyCode.toLowerCase().trim())) {
                return strategy;
            }
        }
        throw new IllegalArgumentException("无效的更新策略代码: " + strategyCode);
    }

    /**
     * 检查是否为实时更新
     */
    public boolean isRealTime() {
        return this == REAL_TIME;
    }

    /**
     * 检查是否为定时更新
     */
    public boolean isScheduled() {
        return this == SCHEDULED;
    }

    /**
     * 检查是否为手动更新
     */
    public boolean isManual() {
        return this == MANUAL;
    }

    /**
     * 检查是否需要更新间隔
     */
    public boolean requiresInterval() {
        return this == SCHEDULED;
    }

    /**
     * 检查策略是否需要自动更新
     */
    public boolean needsAutoUpdate() {
        return this == REAL_TIME || this == SCHEDULED;
    }

    /**
     * 验证更新间隔是否有效
     */
    public boolean isValidInterval(Integer interval) {
        if (interval == null) {
            return !requiresInterval();
        }
        return interval > 0;
    }
}