package com.i0.persistence.spring.pagination;

import com.i0.domain.core.pagination.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Spring分页适配器
 * 将org.springframework.data.domain.Page<T>转换为通用的Page<T>接口
 * 遵循Clean Architecture原则，作为基础设施层的适配器
 * 
 * 序列化输出格式符合API标准规范：
 * {
 *   "content": [...],
 *   "pagination": {
 *     "page": 1,
 *     "pageSize": 20,
 *     "total": 100,
 *     "totalPages": 5
 *   }
 * }
 * 
 * @param <T> 分页数据类型
 */
@JsonPropertyOrder({"content", "pagination"})
public class SpringPage<T> implements Pageable<T> {
    
    @JsonIgnore
    private final org.springframework.data.domain.Page<T> impl;
    
    @JsonIgnore
    private final Map<String, Object> extraData;
    
    /**
     * 构造函数
     * @param impl Spring的Page对象
     * @param extraData 额外数据
     */
    protected SpringPage(org.springframework.data.domain.Page<T> impl, Map<String, Object> extraData) {
        this.impl = impl;
        this.extraData = extraData;
    }
    
    /**
     * 从Spring Page创建SpringPage实例
     * @param page Spring的Page对象
     * @param <T> 数据类型
     * @return SpringPage实例
     */
    public static <T> SpringPage<T> from(org.springframework.data.domain.Page<T> page) {
        return new SpringPage<>(page, Maps.newHashMap());
    }
    
    /**
     * 从Spring Page创建SpringPage实例（保持向后兼容）
     * @param springPage Spring的Page对象
     * @param <T> 数据类型
     * @return SpringPage实例
     */
    public static <T> SpringPage<T> of(org.springframework.data.domain.Page<T> springPage) {
        return new SpringPage<>(springPage, Maps.newHashMap());
    }
    
    /**
     * 从Spring Page创建SpringPage实例，并转换数据类型
     * @param springPage Spring的Page对象
     * @param mapper 数据转换函数
     * @param <S> 源数据类型
     * @param <T> 目标数据类型
     * @return SpringPage实例
     */
    public static <S, T> SpringPage<T> of(org.springframework.data.domain.Page<S> springPage, Function<S, T> mapper) {
        List<T> mappedContent = springPage.getContent().stream()
                .map(mapper)
                .collect(Collectors.toList());
        
        // 创建一个新的Spring Page对象，包含转换后的数据
        org.springframework.data.domain.Page<T> mappedPage = new PageImpl<>(
                mappedContent,
                springPage.getPageable(),
                springPage.getTotalElements()
        );
        
        return new SpringPage<>(mappedPage, Maps.newHashMap());
    }
    
    /**
     * 创建SpringPage实例（从数据列表和分页信息）
     * @param content 数据列表
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @param total 总记录数
     * @param <T> 数据类型
     * @return SpringPage实例
     */
    public static <T> SpringPage<T> of(List<T> content, int page, int size, long total) {
         org.springframework.data.domain.Pageable pageable = PageRequest.of(page, size);
         org.springframework.data.domain.Page<T> springPage = new PageImpl<>(
                 content, pageable, total
         );
         return new SpringPage<>(springPage, Maps.newHashMap());
     }
    
    @Override
    @JsonProperty("content")
    public List<T> getContent() {
        return impl.getContent();
    }
    
    /**
     * 获取分页信息对象，符合API标准规范
     * @return 分页信息
     */
    @JsonProperty("pagination")
    public PaginationInfo getPagination() {
        return new PaginationInfo(
            impl.getNumber() + 1, // API规范中page从1开始
            impl.getSize(),
            impl.getTotalElements(),
            impl.getTotalPages()
        );
    }
    
    @Override
    @JsonIgnore
    public int getPage() {
        return impl.getNumber();
    }
    
    @Override
    @JsonIgnore
    public int getSize() {
        return impl.getSize();
    }
    
    @Override
    @JsonIgnore
    public long getTotal() {
        return impl.getTotalElements();
    }
    
    @Override
    @JsonIgnore
    public int getTotalPages() {
        return impl.getTotalPages();
    }
    
    @Override
    @JsonIgnore
    public boolean isFirst() {
        return impl.isFirst();
    }
    
    @Override
    @JsonIgnore
    public boolean isLast() {
        return impl.isLast();
    }
    
    @Override
    @JsonIgnore
    public boolean hasNext() {
        return impl.hasNext();
    }
    
    @Override
    @JsonIgnore
    public boolean hasPrevious() {
        return impl.hasPrevious();
    }
    
    @Override
    @JsonIgnore
    public int getNumberOfElements() {
        return impl.getNumberOfElements();
    }
    
    @Override
    @JsonIgnore
    public boolean isEmpty() {
        return impl.isEmpty();
    }
    
    @Override
    public <U> Pageable<U> map(Function<? super T, ? extends U> converter) {
        return new SpringPage<>(impl.map(converter), Maps.newHashMap(extraData));
    }
    
    @Override
    @JsonIgnore
    public void addExtraData(String key, Object value) {
        extraData.put(key, value);
    }
    
    @Override
    @JsonIgnore
    public Map<String, Object> getExtraData() {
        return extraData;
    }
    
    /**
     * 获取原始的Spring Page对象
     * 
     * 注意：此方法主要用于框架内部使用，已标记为@JsonIgnore避免序列化
     * 
     * @return Spring Page对象
     */
    @JsonIgnore
    public org.springframework.data.domain.Page<T> getSpringPage() {
        return impl;
    }
    
    /**
     * 创建空的分页结果
     * @param pageable 分页参数
     * @param <T> 数据类型
     * @return 空的SpringPage实例
     */
    public static <T> SpringPage<T> empty(org.springframework.data.domain.Pageable pageable) {
        org.springframework.data.domain.Page<T> emptyPage = new PageImpl<>(
                List.of(), pageable, 0L
        );
        return new SpringPage<>(emptyPage, Maps.newHashMap());
    }
    
    /**
     * 创建空的分页结果
     * @param <T> 数据类型
     * @return 空的SpringPage实例
     */
    public static <T> SpringPage<T> empty() {
        return new SpringPage<>(org.springframework.data.domain.Page.empty(), Maps.newHashMap());
    }
}