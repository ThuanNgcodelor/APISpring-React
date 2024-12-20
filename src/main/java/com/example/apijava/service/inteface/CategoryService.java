package com.example.apijava.service.inteface;


import com.example.apijava.models.Category;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CategoryService  {
    Category addCategory(Category category);
    Category updateCategory(Category category, Long id);
    void deleteCategoryById(Long id);
    Category getCategoryById(Long id);
    List<Category> searchCategory(String keyword);
    Page<Category> getAllPage(Integer pageNo);
    Page<Category> searchCategory(String keyword,Integer pageNo);
}
