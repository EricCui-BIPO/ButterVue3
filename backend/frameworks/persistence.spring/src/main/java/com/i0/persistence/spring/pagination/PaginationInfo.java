package com.i0.persistence.spring.pagination;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 分页信息类
 * 符合API标准规范的分页信息封装
 * 
 * 输出格式：
 * {
 *   "page": 1,
 *   "pageSize": 20,
 *   "total": 100,
 *   "totalPages": 5
 * }
 */
public class PaginationInfo {
    
    @JsonProperty("page")
    private final int page;
    
    @JsonProperty("pageSize")
    private final int pageSize;
    
    @JsonProperty("total")
    private final long total;
    
    @JsonProperty("totalPages")
    private final int totalPages;
    
    /**
     * 构造函数
     * @param page 当前页码（从1开始）
     * @param pageSize 每页大小
     * @param total 总记录数
     * @param totalPages 总页数
     */
    public PaginationInfo(int page, int pageSize, long total, int totalPages) {
        this.page = page;
        this.pageSize = pageSize;
        this.total = total;
        this.totalPages = totalPages;
    }
    
    /**
     * 获取当前页码（从1开始）
     * @return 当前页码
     */
    public int getPage() {
        return page;
    }
    
    /**
     * 获取每页大小
     * @return 每页大小
     */
    public int getPageSize() {
        return pageSize;
    }
    
    /**
     * 获取总记录数
     * @return 总记录数
     */
    public long getTotal() {
        return total;
    }
    
    /**
     * 获取总页数
     * @return 总页数
     */
    public int getTotalPages() {
        return totalPages;
    }
    
    @Override
    public String toString() {
        return "PaginationInfo{" +
                "page=" + page +
                ", pageSize=" + pageSize +
                ", total=" + total +
                ", totalPages=" + totalPages +
                '}';
    }
}