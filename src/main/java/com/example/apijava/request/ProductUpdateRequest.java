package com.example.apijava.request;

import com.example.apijava.dto.ImageDto;
import com.example.apijava.models.Category;
import lombok.Data;

import java.util.List;

@Data
public class ProductUpdateRequest {
    private Long id;
    private String name;
    private String description;
    private Integer stock;
    private double price;
    private List<ImageDto> images;
    private List<Category> categories;

    public ProductUpdateRequest(Long id, String name, String description, Integer stock, double price, List<ImageDto> images, List<Category> categories) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.stock = stock;
        this.price = price;
        this.images = images;
        this.categories = categories;
    }

    public ProductUpdateRequest() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<ImageDto> getImages() {
        return images;
    }

    public void setImages(List<ImageDto> images) {
        this.images = images;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
