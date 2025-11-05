package com.i0.persistence.spring.pagination;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * SpringPage JSON序列化测试
 * 验证序列化输出符合API标准规范
 */
@DisplayName("SpringPage JSON序列化测试")
class SpringPageSerializationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("应该按照API标准格式序列化分页数据")
    void shouldSerializeAccordingToApiStandards() throws Exception {
        // Given
        List<String> content = Arrays.asList("item1", "item2", "item3");
        Pageable pageable = PageRequest.of(0, 10); // 第0页，每页10条
        org.springframework.data.domain.Page<String> springPage = 
            new PageImpl<>(content, pageable, 23); // 总共23条记录
        
        SpringPage<String> page = SpringPage.from(springPage);
        
        // When
        String json = objectMapper.writeValueAsString(page);
        
        // Then
        System.out.println("序列化结果: " + json);
        
        // 验证JSON结构符合API标准
        assertThat(json).contains("\"content\":[\"item1\",\"item2\",\"item3\"]");
        assertThat(json).contains("\"pagination\":");
        assertThat(json).contains("\"page\":1"); // API标准：页码从1开始
        assertThat(json).contains("\"pageSize\":10");
        assertThat(json).contains("\"total\":23");
        assertThat(json).contains("\"totalPages\":3");
        
        // 验证不包含内部字段
        assertThat(json).doesNotContain("impl");
        assertThat(json).doesNotContain("extraData");
        assertThat(json).doesNotContain("springPage");
    }
    
    @Test
    @DisplayName("应该正确处理空页面")
    void shouldHandleEmptyPage() throws Exception {
        // Given
        List<String> content = Arrays.asList();
        Pageable pageable = PageRequest.of(0, 10);
        org.springframework.data.domain.Page<String> springPage = 
            new PageImpl<>(content, pageable, 0);
        
        SpringPage<String> page = SpringPage.from(springPage);
        
        // When
        String json = objectMapper.writeValueAsString(page);
        
        // Then
        System.out.println("空页面序列化结果: " + json);
        
        assertThat(json).contains("\"content\":[]");
        assertThat(json).contains("\"pagination\":");
        assertThat(json).contains("\"page\":1");
        assertThat(json).contains("\"pageSize\":10");
        assertThat(json).contains("\"total\":0");
        assertThat(json).contains("\"totalPages\":0");
    }
    
    @Test
    @DisplayName("应该正确处理第二页数据")
    void shouldHandleSecondPage() throws Exception {
        // Given
        List<String> content = Arrays.asList("item11", "item12");
        Pageable pageable = PageRequest.of(1, 10); // 第1页（从0开始），每页10条
        org.springframework.data.domain.Page<String> springPage = 
            new PageImpl<>(content, pageable, 12); // 总共12条记录
        
        SpringPage<String> page = SpringPage.from(springPage);
        
        // When
        String json = objectMapper.writeValueAsString(page);
        
        // Then
        System.out.println("第二页序列化结果: " + json);
        
        assertThat(json).contains("\"content\":[\"item11\",\"item12\"]");
        assertThat(json).contains("\"page\":2"); // API标准：第二页显示为2
        assertThat(json).contains("\"pageSize\":10");
        assertThat(json).contains("\"total\":12");
        assertThat(json).contains("\"totalPages\":2");
    }
}