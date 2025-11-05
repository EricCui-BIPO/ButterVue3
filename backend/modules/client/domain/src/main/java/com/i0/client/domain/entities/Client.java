package com.i0.client.domain.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Client实体类
 * 代表系统中的客户实体，包含客户的基本信息和注册国家信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Client {
    
    /**
     * 客户唯一标识
     */
    private String id;
    
    /**
     * 客户名称（必填）
     */
    private String name;
    
    /**
     * 客户代码（唯一标识符）
     */
    private String code;
    
    /**
     * 客户别名（可选）
     */
    private String aliasName;
    
    /**
     * 公司注册所在位置ID（关联到location模块）
     */
    private String locationId;
    
    /**
     * 客户描述
     */
    private String description;
    
    /**
     * 是否激活
     */
    private Boolean active;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 版本号（用于乐观锁）
     */
    private Long version;

    /**
     * 创建者ID
     */
    private String creatorId;

    /**
     * 更新者ID
     */
    private String updaterId;

    /**
     * 创建者姓名
     */
    private String creator;

    /**
     * 更新者姓名
     */
    private String updater;
    
    /**
     * 创建新的Client实例
     * @param name 客户名称
     * @param code 客户代码
     * @param aliasName 客户别名
     * @param locationId 注册位置ID
     * @param description 客户描述
     * @return 新的Client实例
     */
    public static Client create(String name, String code, String aliasName, String locationId, String description) {
        validateName(name);
        validateCode(code);
        validateLocationId(locationId);

        return Client.builder()
                .name(name)
                .code(code)
                .aliasName(aliasName)
                .locationId(locationId)
                .description(description)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .version(0L)
                .creatorId("system") // 临时设置为system，实际应该从上下文获取
                .creator("system")
                .updaterId("system")
                .updater("system")
                .build();
    }
    
    /**
     * 更新客户信息
     * @param name 新的客户名称
     * @param aliasName 新的客户别名
     * @param locationId 新的注册位置ID
     * @param description 新的客户描述
     */
    public void update(String name, String aliasName, String locationId, String description) {
        validateName(name);
        validateLocationId(locationId);

        this.name = name;
        this.aliasName = aliasName;
        this.locationId = locationId;
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 更新客户代码
     * @param code 新的客户代码
     */
    public void updateCode(String code) {
        validateCode(code);
        this.code = code;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 激活客户
     */
    public void activate() {
        this.active = true;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 停用客户
     */
    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 检查客户是否激活
     * @return true如果客户激活
     */
    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }
    
    /**
     * 检查是否有别名
     * @return true如果有别名
     */
    public boolean hasAliasName() {
        return aliasName != null && !aliasName.trim().isEmpty();
    }
    
    /**
     * 获取显示名称（优先使用别名，如果没有别名则使用名称）
     * @return 显示名称
     */
    public String getDisplayName() {
        return hasAliasName() ? aliasName : name;
    }
    
    /**
     * 验证客户完整性
     * 检查客户的所有必要字段是否有效
     */
    public void validate() {
        validateName(this.name);
        validateCode(this.code);
        validateLocationId(this.locationId);
    }
    
    /**
     * 验证客户名称
     * @param name 客户名称
     */
    private static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("客户名称不能为空");
        }
        if (name.length() > 200) {
            throw new IllegalArgumentException("客户名称不能超过200个字符");
        }
    }
    
    /**
     * 验证客户代码
     * @param code 客户代码
     */
    private static void validateCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("客户代码不能为空");
        }
        if (code.length() > 50) {
            throw new IllegalArgumentException("客户代码不能超过50个字符");
        }
        // 客户代码只能包含字母、数字、下划线和连字符
        if (!code.matches("^[a-zA-Z0-9_-]+$")) {
            throw new IllegalArgumentException("客户代码只能包含字母、数字、下划线和连字符");
        }
    }
    
    /**
     * 验证注册位置ID
     * @param locationId 注册位置ID
     */
    private static void validateLocationId(String locationId) {
        if (locationId == null || locationId.trim().isEmpty()) {
            throw new IllegalArgumentException("注册位置不能为空");
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(id, client.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Client{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", aliasName='" + aliasName + '\'' +
                ", locationId='" + locationId + '\'' +
                ", active=" + active +
                '}';
    }
}