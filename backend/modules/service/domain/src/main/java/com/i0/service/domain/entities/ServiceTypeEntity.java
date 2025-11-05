package com.i0.service.domain.entities;

import com.i0.service.domain.valueobjects.ServiceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * 服务类型实体类
 * 管理系统中的服务类型信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceTypeEntity {
    private String id;
    private String name;
    private ServiceType serviceType;
    private String description;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long version;

    /**
     * 创建新的服务类型实体
     * @param name 服务类型名称
     * @param serviceType 服务类型枚举
     * @param description 描述
     * @return 创建的服务类型实体
     */
    public static ServiceTypeEntity create(String name, ServiceType serviceType, String description) {
        validateName(name);
        validateServiceType(serviceType);

        return ServiceTypeEntity.builder()
                .id(UUID.randomUUID().toString())
                .name(name)
                .serviceType(serviceType)
                .description(description)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .version(0L)
                .build();
    }

    /**
     * 更新服务类型信息
     * @param name 新的名称
     * @param description 新的描述
     */
    public void update(String name, String description) {
        validateName(name);

        if (!Objects.equals(this.name, name) || !Objects.equals(this.description, description)) {
            this.name = name;
            this.description = description;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * 激活服务类型
     */
    public void activate() {
        if (!Boolean.TRUE.equals(this.active)) {
            this.active = true;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * 停用服务类型
     */
    public void deactivate() {
        if (Boolean.TRUE.equals(this.active)) {
            this.active = false;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * 检查服务类型是否活跃
     * @return 如果活跃返回true
     */
    public boolean isActive() {
        return Boolean.TRUE.equals(this.active);
    }

    /**
     * 获取服务类型代码
     * @return 服务类型代码
     */
    public String getCode() {
        return serviceType != null ? serviceType.getCode() : null;
    }

    /**
     * 获取服务类型显示名称
     * @return 服务类型显示名称
     */
    public String getServiceTypeDisplayName() {
        return serviceType != null ? serviceType.getDisplayName() : null;
    }

    /**
     * 验证名称
     * @param name 待验证的名称
     */
    private static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Service type name cannot be null or empty");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("Service type name cannot exceed 100 characters");
        }
    }

    /**
     * 验证服务类型
     * @param serviceType 待验证的服务类型
     */
    private static void validateServiceType(ServiceType serviceType) {
        if (serviceType == null) {
            throw new IllegalArgumentException("Service type cannot be null");
        }
    }

    /**
     * 检查是否为EOR服务类型
     * @return 如果是EOR返回true
     */
    public boolean isEOR() {
        return serviceType != null && serviceType.isEOR();
    }

    /**
     * 检查是否为GPO服务类型
     * @return 如果是GPO返回true
     */
    public boolean isGPO() {
        return serviceType != null && serviceType.isGPO();
    }

    /**
     * 检查是否为Contractor服务类型
     * @return 如果是Contractor返回true
     */
    public boolean isContractor() {
        return serviceType != null && serviceType.isContractor();
    }

    /**
     * 检查是否为SELF服务类型
     * @return 如果是SELF返回true
     */
    public boolean isSELF() {
        return serviceType != null && serviceType.isSELF();
    }

    /**
     * 检查是否为外包类服务
     * @return 如果是外包类服务返回true
     */
    public boolean isOutsourcingService() {
        return serviceType != null && serviceType.isOutsourcingService();
    }

    /**
     * 检查是否为管理类服务
     * @return 如果是管理类服务返回true
     */
    public boolean isManagementService() {
        return serviceType != null && serviceType.isManagementService();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceTypeEntity that = (ServiceTypeEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ServiceTypeEntity{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", serviceType=" + (serviceType != null ? serviceType.getCode() : "null") +
                ", active=" + active +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}