package com.example.apijava.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;
    private String categoryName;
    private boolean categoryStatus;

    public Category(Long categoryId, String categoryName, boolean categoryStatus) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryStatus = categoryStatus;
    }

    public Category() {
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public boolean isCategoryStatus() {
        return categoryStatus;
    }

    public void setCategoryStatus(boolean categoryStatus) {
        this.categoryStatus = categoryStatus;
    }
}
