package com.example.apijava.service.inteface;

import com.example.apijava.dto.ProductDto;
import com.example.apijava.models.Product;
import com.example.apijava.request.AddProductRequest;
import com.example.apijava.request.ProductUpdateRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {
    public Product getProductById(Long id);
    Product addProduct(AddProductRequest product);
    Product updateProduct(ProductUpdateRequest product, Long id);
    void deleteProductById(Long id);
    Page<Product> searchProduct(String keyword, Integer pageNO);
    Page<Product> getAllProduct(Integer pageNO);
    Page<Product> getByCategory(Long id, Integer pageNO);
    List<ProductDto> getConvertedProducts(List<Product> products);
    ProductDto convertToDto(Product product);
}
