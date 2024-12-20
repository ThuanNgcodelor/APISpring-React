package com.example.apijava.repositorys;

import com.example.apijava.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("select p from Product p join p.categories c where c.categoryName like %?1%")
    List<Product> findByCategoryName(String categoryName);

    @Query("select p from Product p where p.name like %?1%")
    List<Product> searchProduct(String keyword, Pageable pageable);

    @Query("select p from Product p join p.categories c where c.categoryId = :categoryId")
    Page<Product> findByCategory(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("select p from Product p where p.name like %?1%")
    List<Product> searchProducts(String keyword);

}
