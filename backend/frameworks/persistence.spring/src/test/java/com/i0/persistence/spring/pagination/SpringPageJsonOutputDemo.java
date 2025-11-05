package com.i0.persistence.spring.pagination;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;

/**
 * SpringPage JSON输出格式演示
 * 展示优化后的分页数据JSON结构
 */
public class SpringPageJsonOutputDemo {
    
    public static void main(String[] args) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        
        // 创建测试数据
        List<String> content = Arrays.asList("item1", "item2", "item3");
        Pageable pageable = PageRequest.of(0, 10); // Spring的页码从0开始
        PageImpl<String> springPage = new PageImpl<>(content, pageable, 25);
        
        // 创建SpringPage包装器
        SpringPage<String> wrappedPage = SpringPage.of(springPage);
        
        // 序列化为JSON
        String json = objectMapper.writeValueAsString(wrappedPage);
        
        System.out.println("=== SpringPage JSON输出格式 ===");
        System.out.println(json);
        
        // 格式化输出
        String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(wrappedPage);
        System.out.println("\n=== 格式化的JSON输出 ===");
        System.out.println(prettyJson);
        
        System.out.println("\n=== 关键特性说明 ===");
        System.out.println("1. 页码从1开始计数（API标准）");
        System.out.println("2. 分页信息封装在pagination对象中");
        System.out.println("3. 不暴露Spring内部字段（如sort、numberOfElements等）");
        System.out.println("4. 符合API标准规范的响应格式");
    }
}