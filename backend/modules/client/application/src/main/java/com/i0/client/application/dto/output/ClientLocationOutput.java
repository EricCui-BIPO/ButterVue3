package com.i0.client.application.dto.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 客户位置选择输出DTO
 * 用于客户选择时展示可用位置信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientLocationOutput {

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
    private String code;

    /**
     * 从位置信息创建输出对象
     * @param id 位置ID
     * @param name 位置名称
     * @param code 位置代码
     * @return 客户位置输出对象
     */
    public static ClientLocationOutput of(String id, String name, String code) {
        return ClientLocationOutput.builder()
                .id(id)
                .name(name)
                .code(code)
                .build();
    }
}