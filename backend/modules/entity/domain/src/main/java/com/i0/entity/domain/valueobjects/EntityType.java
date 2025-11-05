package com.i0.entity.domain.valueobjects;

/**
 * Entity类型枚举
 * 定义系统中支持的实体类型
 */
public enum EntityType {
    /**
     * BIPO实体 - BIPO公司内部实体
     */
    BIPO_ENTITY("BIPO Entity", "BIPO公司内部实体"),
    
    /**
     * 客户实体 - 外部客户公司实体
     */
    CLIENT_ENTITY("Client Entity", "外部客户公司实体"),
    
    /**
     * 供应商实体 - 外部供应商实体
     */
    VENDOR_ENTITY("Vendor Entity", "外部供应商实体");
    
    private final String displayName;
    private final String description;
    
    EntityType(String displayName, String description) {
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
     * 根据显示名称获取枚举值
     * @param displayName 显示名称
     * @return EntityType枚举值
     * @throws IllegalArgumentException 如果找不到对应的枚举值
     */
    public static EntityType fromDisplayName(String displayName) {
        for (EntityType type : values()) {
            if (type.displayName.equals(displayName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown entity type: " + displayName);
    }
    
    /**
     * 检查是否为BIPO内部实体
     * @return true如果是BIPO实体
     */
    public boolean isBipoEntity() {
        return this == BIPO_ENTITY;
    }
    
    /**
     * 检查是否为客户实体
     * @return true如果是客户实体
     */
    public boolean isClientEntity() {
        return this == CLIENT_ENTITY;
    }
    
    /**
     * 检查是否为供应商实体
     * @return true如果是供应商实体
     */
    public boolean isVendorEntity() {
        return this == VENDOR_ENTITY;
    }
    
    /**
     * 检查是否为外部实体
     * @return true如果是外部实体（客户或供应商）
     */
    public boolean isExternalEntity() {
        return this == CLIENT_ENTITY || this == VENDOR_ENTITY;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}