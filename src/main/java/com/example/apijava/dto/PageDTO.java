package com.example.apijava.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class PageDTO<T> {
    private List<T> content;
    private int pageNo;
    private int totalPages;
    private long totalElements;

    public PageDTO(List<T> content, int pageNo, int totalPages, long totalElements) {
        this.content = content;
        this.pageNo = pageNo;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }

    public PageDTO() {}

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }
}
