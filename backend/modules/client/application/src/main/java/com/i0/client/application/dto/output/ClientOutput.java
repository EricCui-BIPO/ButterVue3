package com.i0.client.application.dto.output;

import com.i0.client.domain.entities.Client;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 客户输出DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientOutput {
    
    /**
     * 客户ID
     */
    private String id;
    
    /**
     * 客户名称
     */
    private String name;
    
    /**
     * 客户代码
     */
    private String code;
    
    /**
     * 客户别名
     */
    private String aliasName;
    
    /**
     * 位置ID
     */
    private String locationId;

    /**
     * 位置名称
     */
    private String locationName;

    /**
     * 位置代码
     */
    private String locationCode;
    
    /**
     * 描述
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
     * 版本号
     */
    private Long version;
    
    /**
     * 显示名称（如果有别名则显示别名，否则显示名称）
     */
    private String displayName;
    
    /**
     * 从领域实体转换为输出DTO
     * @param client 客户领域实体
     * @return 客户输出DTO
     */
    public static ClientOutput from(Client client) {
        if (client == null) {
            return null;
        }

        return ClientOutput.builder()
                .id(client.getId())
                .name(client.getName())
                .code(client.getCode())
                .aliasName(client.getAliasName())
                .locationId(client.getLocationId())
                .description(client.getDescription())
                .active(client.getActive())
                .createdAt(client.getCreatedAt())
                .updatedAt(client.getUpdatedAt())
                .version(client.getVersion())
                .displayName(client.getDisplayName())
                .build();
    }

    /**
     * 从领域实体转换为输出DTO（包含位置信息）
     * @param client 客户领域实体
     * @param locationName 位置名称
     * @return 客户输出DTO
     */
    public static ClientOutput from(Client client, String locationName) {
        ClientOutput output = from(client);
        if (output != null) {
            output.setLocationName(locationName);
        }
        return output;
    }

    /**
     * 从领域实体转换为输出DTO（包含位置信息）
     * @param client 客户领域实体
     * @param locationName 位置名称
     * @param locationCode 位置代码
     * @return 客户输出DTO
     */
    public static ClientOutput from(Client client, String locationName, String locationCode) {
        ClientOutput output = from(client);
        if (output != null) {
            output.setLocationName(locationName);
            output.setLocationCode(locationCode);
        }
        return output;
    }
}