package com.i0.domain.core.pagination;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 通用分页接口
 * 遵循Clean Architecture原则，提供领域层的分页抽象
 * 不依赖任何外部框架，保持架构隔离性
 * 
 * @param <T> 分页数据类型
 */
public interface Pageable<T> {
    
    /**
     * 获取当前页的数据列表
     * @return 数据列表
     */
    List<T> getContent();
    
    /**
     * 获取当前页码（从0开始）
     * @return 当前页码
     */
    int getPage();
    
    /**
     * 获取每页大小
     * @return 每页大小
     */
    int getSize();
    
    /**
     * 获取总记录数
     * @return 总记录数
     */
    long getTotal();
    
    /**
     * 获取总页数
     * @return 总页数
     */
    int getTotalPages();
    
    /**
     * 是否为第一页
     * @return true如果是第一页
     */
    boolean isFirst();
    
    /**
     * 是否为最后一页
     * @return true如果是最后一页
     */
    boolean isLast();
    
    /**
     * 是否有下一页
     * @return true如果有下一页
     */
    boolean hasNext();
    
    /**
     * 是否有上一页
     * @return true如果有上一页
     */
    boolean hasPrevious();
    
    /**
     * 获取当前页的记录数
     * @return 当前页的记录数
     */
    int getNumberOfElements();
    
    /**
     * 检查是否为空页
     * @return true如果没有数据
     */
    boolean isEmpty();
    
    /**
     * 将当前页的数据转换为另一种类型的分页结果
     * @param converter 数据转换函数
     * @param <U> 目标数据类型
     * @return 转换后的分页结果
     */
    <U> Pageable<U> map(Function<? super T, ? extends U> converter);
    
    /**
     * 添加额外数据
     * @param key 键
     * @param value 值
     */
    void addExtraData(String key, Object value);
    
    /**
     * 获取额外数据
     * @return 额外数据映射
     */
    Map<String, Object> getExtraData();
}