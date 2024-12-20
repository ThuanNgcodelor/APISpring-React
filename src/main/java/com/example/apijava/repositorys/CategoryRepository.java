package com.example.apijava.repositorys;

import com.example.apijava.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("select c from Category c where c.categoryName like %?1%")
    List<Category> searchCategory(String keyword);

    boolean existsByCategoryName(String categoryName);
    Category findByCategoryName(String categoryName);
}

