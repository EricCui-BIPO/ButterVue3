package com.i0.domain.core.pagination;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 简单的分页实现
 *
 * 提供基础的分页功能，不依赖任何外部框架
 * 遵循Clean Architecture原则，作为领域层的分页实现
 *
 * @param <T> 分页数据类型
 */
public class SimplePageable<T> implements Pageable<T> {

    private final List<T> content;
    private final int page;
    private final int size;
    private final long total;
    private final Map<String, Object> extraData;

    /**
     * 创建分页结果
     */
    public SimplePageable(List<T> content, int page, int size, long total) {
        this.content = content != null ? content : Collections.emptyList();
        this.page = page;
        this.size = size;
        this.total = total;
        this.extraData = new HashMap<>();
    }

    /**
     * 工厂方法：创建分页结果
     */
    public static <T> SimplePageable<T> of(List<T> content, int page, int size, long total) {
        return new SimplePageable<>(content, page, size, total);
    }

    /**
     * 工厂方法：创建空分页结果
     */
    public static <T> SimplePageable<T> empty(int page, int size) {
        return new SimplePageable<>(Collections.emptyList(), page, size, 0);
    }

    @Override
    public List<T> getContent() {
        return Collections.unmodifiableList(content);
    }

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public long getTotal() {
        return total;
    }

    @Override
    public int getTotalPages() {
        return size > 0 ? (int) Math.ceil((double) total / size) : 0;
    }

    @Override
    public boolean isFirst() {
        return page == 0;
    }

    @Override
    public boolean isLast() {
        return page >= getTotalPages() - 1;
    }

    @Override
    public boolean hasNext() {
        return !isLast();
    }

    @Override
    public boolean hasPrevious() {
        return !isFirst();
    }

    @Override
    public int getNumberOfElements() {
        return content.size();
    }

    @Override
    public boolean isEmpty() {
        return content.isEmpty();
    }

    @Override
    public <U> Pageable<U> map(Function<? super T, ? extends U> converter) {
        List<U> convertedContent = content.stream()
                .map(converter)
                .collect(Collectors.toList());
        return new SimplePageable<>(convertedContent, page, size, total);
    }

    @Override
    public void addExtraData(String key, Object value) {
        extraData.put(key, value);
    }

    @Override
    public Map<String, Object> getExtraData() {
        return Collections.unmodifiableMap(extraData);
    }

    @Override
    public String toString() {
        return "SimplePageable{" +
                "content.size=" + content.size() +
                ", page=" + page +
                ", size=" + size +
                ", total=" + total +
                ", totalPages=" + getTotalPages() +
                ", first=" + isFirst() +
                ", last=" + isLast() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimplePageable<?> that = (SimplePageable<?>) o;
        return page == that.page &&
                size == that.size &&
                total == that.total &&
                content.equals(that.content);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(content, page, size, total);
    }
}