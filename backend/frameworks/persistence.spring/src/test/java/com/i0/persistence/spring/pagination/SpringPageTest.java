package com.i0.persistence.spring.pagination;

import com.i0.domain.core.pagination.Pageable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * SpringPage单元测试
 * 验证Spring分页适配器的功能
 */
@DisplayName("SpringPage单元测试")
class SpringPageTest {
    
    @Test
    @DisplayName("应该正确转换Spring Page对象")
    void should_ConvertSpringPage_When_ValidPageProvided() {
        // Given
        List<String> content = List.of("item1", "item2", "item3");
        org.springframework.data.domain.Pageable pageable = PageRequest.of(0, 10);
        org.springframework.data.domain.Page<String> springPage = new PageImpl<>(content, pageable, 23L);
        
        // When
        SpringPage<String> page = SpringPage.of(springPage);
        
        // Then
        assertThat(page.getContent()).isEqualTo(content);
        assertThat(page.getPage()).isEqualTo(0);
        assertThat(page.getSize()).isEqualTo(10);
        assertThat(page.getTotal()).isEqualTo(23L);
        assertThat(page.getTotalPages()).isEqualTo(3);
        assertThat(page.getNumberOfElements()).isEqualTo(3);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.isLast()).isFalse();
        assertThat(page.hasNext()).isTrue();
        assertThat(page.hasPrevious()).isFalse();
        assertThat(page.isEmpty()).isFalse();
    }
    
    @Test
    @DisplayName("应该正确处理最后一页")
    void should_HandleLastPage_When_LastPageProvided() {
        // Given
        List<String> content = List.of("item1", "item2", "item3");
        org.springframework.data.domain.Pageable pageable = PageRequest.of(2, 10); // 第3页
        org.springframework.data.domain.Page<String> springPage = new PageImpl<>(content, pageable, 23L);
        
        // When
        SpringPage<String> page = SpringPage.of(springPage);
        
        // Then
        assertThat(page.getPage()).isEqualTo(2);
        assertThat(page.getTotalPages()).isEqualTo(3);
        assertThat(page.isFirst()).isFalse();
        assertThat(page.isLast()).isTrue();
        assertThat(page.hasNext()).isFalse();
        assertThat(page.hasPrevious()).isTrue();
    }
    
    @Test
    @DisplayName("应该正确处理空页面")
    void should_HandleEmptyPage_When_NoDataProvided() {
        // Given
        List<String> content = List.of();
        org.springframework.data.domain.Pageable pageable = PageRequest.of(0, 10);
        org.springframework.data.domain.Page<String> springPage = new PageImpl<>(content, pageable, 0L);
        
        // When
        SpringPage<String> page = SpringPage.of(springPage);
        
        // Then
        assertThat(page.getContent()).isEmpty();
        assertThat(page.getTotal()).isEqualTo(0L);
        assertThat(page.getTotalPages()).isEqualTo(0);
        assertThat(page.getNumberOfElements()).isEqualTo(0);
        assertThat(page.isEmpty()).isTrue();
    }
    
    @Test
    @DisplayName("应该正确转换数据类型")
    void should_ConvertDataType_When_MapperProvided() {
        // Given
        List<Integer> content = List.of(1, 2, 3);
        org.springframework.data.domain.Pageable pageable = PageRequest.of(0, 10);
        org.springframework.data.domain.Page<Integer> springPage = new PageImpl<>(content, pageable, 3L);
        
        // When
        SpringPage<String> page = SpringPage.of(springPage, Object::toString);
        
        // Then
        assertThat(page.getContent()).containsExactly("1", "2", "3");
        assertThat(page.getTotal()).isEqualTo(3L);
        assertThat(page.getNumberOfElements()).isEqualTo(3);
    }
    
    @Test
    @DisplayName("应该创建空的分页结果")
    void should_CreateEmptyPage_When_EmptyMethodCalled() {
        // Given
        org.springframework.data.domain.Pageable pageable = PageRequest.of(0, 10);
        
        // When
        SpringPage<String> page = SpringPage.empty(pageable);
        
        // Then
        assertThat(page.getContent()).isEmpty();
        assertThat(page.getPage()).isEqualTo(0);
        assertThat(page.getSize()).isEqualTo(10);
        assertThat(page.getTotal()).isEqualTo(0L);
        assertThat(page.getTotalPages()).isEqualTo(0);
        assertThat(page.isEmpty()).isTrue();
    }
    
    @Test
    @DisplayName("应该实现Page接口")
    void should_ImplementPageInterface_When_SpringPageCreated() {
        // Given
        List<String> content = List.of("item1");
        org.springframework.data.domain.Pageable pageable = PageRequest.of(0, 10);
        org.springframework.data.domain.Page<String> springPage = new PageImpl<>(content, pageable, 1L);
        
        // When
        SpringPage<String> springPageAdapter = SpringPage.of(springPage);
        
        // Then
        assertThat(springPageAdapter).isInstanceOf(Pageable.class);
        
        // 验证可以作为Page接口使用
        Pageable<String> page = springPageAdapter;
        assertThat(page.getContent()).containsExactly("item1");
        assertThat(page.getTotal()).isEqualTo(1L);
    }
    
    @Test
    @DisplayName("应该提供原始Spring Page访问")
    void should_ProvideOriginalSpringPage_When_GetSpringPageCalled() {
        // Given
        List<String> content = List.of("item1");
        org.springframework.data.domain.Pageable pageable = PageRequest.of(0, 10);
        org.springframework.data.domain.Page<String> originalSpringPage = new PageImpl<>(content, pageable, 1L);
        
        // When
        SpringPage<String> page = SpringPage.of(originalSpringPage);
        
        // Then
        assertThat(page.getSpringPage()).isSameAs(originalSpringPage);
    }
}