package com.example.apijava.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class CachedPage<T> implements Serializable {
    private List<T> content; // Nội dung của page
    private long totalElements; // Tổng số phần tử

    @JsonCreator
    public CachedPage(@JsonProperty("content") List<T> content,
                      @JsonProperty("totalElements") long totalElements) {
        this.content = content;
        this.totalElements = totalElements;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }



    public CachedPage() {}
}
