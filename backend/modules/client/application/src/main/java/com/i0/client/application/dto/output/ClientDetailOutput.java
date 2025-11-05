package com.i0.client.application.dto.output;

import com.i0.client.domain.entities.Client;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 客户详情输出DTO
 * 包含完整的客户信息和关联的位置信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientDetailOutput {

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
     * 位置信息
     */
    private LocationOutput location;

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
     * 从领域实体转换为输出DTO（包含位置信息）
     * @param client 客户领域实体
     * @param locationName 位置名称
     * @param locationCode 位置代码
     * @return 客户详情输出DTO
     */
    public static ClientDetailOutput from(Client client, String locationName, String locationCode) {
        if (client == null) {
            return null;
        }

        return ClientDetailOutput.builder()
                .id(client.getId())
                .name(client.getName())
                .code(client.getCode())
                .aliasName(client.getAliasName())
                .locationId(client.getLocationId())
                .location(LocationOutput.of(client.getLocationId(), locationName, locationCode))
                .description(client.getDescription())
                .active(client.getActive())
                .createdAt(client.getCreatedAt())
                .updatedAt(client.getUpdatedAt())
                .version(client.getVersion())
                .displayName(client.getDisplayName())
                .build();
    }

    /**
     * 从领域实体转换为输出DTO（包含位置名称）
     * @param client 客户领域实体
     * @param locationName 位置名称
     * @return 客户详情输出DTO
     */
    public static ClientDetailOutput from(Client client, String locationName) {
        if (client == null) {
            return null;
        }

        return ClientDetailOutput.builder()
                .id(client.getId())
                .name(client.getName())
                .code(client.getCode())
                .aliasName(client.getAliasName())
                .locationId(client.getLocationId())
                .location(LocationOutput.of(client.getLocationId(), locationName))
                .description(client.getDescription())
                .active(client.getActive())
                .createdAt(client.getCreatedAt())
                .updatedAt(client.getUpdatedAt())
                .version(client.getVersion())
                .displayName(client.getDisplayName())
                .build();
    }

    /**
     * 从领域实体转换为输出DTO（不包含位置信息）
     * @param client 客户领域实体
     * @return 客户详情输出DTO
     */
    public static ClientDetailOutput from(Client client) {
        if (client == null) {
            return null;
        }

        return ClientDetailOutput.builder()
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
     * 位置信息输出DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationOutput {

        /**
         * 位置ID
         */
        private String id;

        /**
         * 位置名称
         */
        private String name;

        /**
         * 位置代码
         */
        private String isoCode;

        /**
         * 创建位置信息对象
         * @param id 位置ID
         * @param name 位置名称
         * @param isoCode 位置代码
         * @return 位置信息对象
         */
        public static LocationOutput of(String id, String name, String isoCode) {
            return LocationOutput.builder()
                    .id(id)
                    .name(name)
                    .isoCode(isoCode)
                    .build();
        }

        /**
         * 创建位置信息对象（不包含代码）
         * @param id 位置ID
         * @param name 位置名称
         * @return 位置信息对象
         */
        public static LocationOutput of(String id, String name) {
            return LocationOutput.builder()
                    .id(id)
                    .name(name)
                    .build();
        }
    }
}