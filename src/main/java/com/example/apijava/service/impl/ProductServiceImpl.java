package com.example.apijava.service.impl;

import com.example.apijava.dto.ImageDto;
import com.example.apijava.dto.ProductDto;
import com.example.apijava.models.Category;
import com.example.apijava.models.Image;
import com.example.apijava.models.Product;
import com.example.apijava.repositorys.CategoryRepository;
import com.example.apijava.repositorys.ImageRepository;
import com.example.apijava.repositorys.ProductRepository;
import com.example.apijava.request.AddProductRequest;
import com.example.apijava.request.ProductUpdateRequest;
import com.example.apijava.service.inteface.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    private ModelMapper modelMapper;
    private final ImageRepository imageRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, ImageRepository imageRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.imageRepository = imageRepository;
        this.modelMapper = new ModelMapper();
    }

    @Override
    public Product addProduct(AddProductRequest productRequest) {
        // Validate input
        if (productRequest.getCategories() == null || productRequest.getCategories().isEmpty()) {
            throw new RuntimeException("Categories not found");
        }

        List<Category> categories = productRequest.getCategories().stream().map(categoryRequest -> {
            Category existingCategory = categoryRepository.findByCategoryName(categoryRequest.getCategoryName());
            if (existingCategory != null) {
                return existingCategory;
            } else {
                Category newCategory = new Category();
                newCategory.setCategoryName(categoryRequest.getCategoryName());
                newCategory.setCategoryStatus(true);
                return categoryRepository.save(newCategory);
            }
        }).toList();

        Product newProduct = new Product(
                productRequest.getName(),
                productRequest.getDescription(),
                productRequest.getStock(),
                productRequest.getPrice(),
                categories
        );

        return productRepository.save(newProduct);
    }

    @Override
    public Product updateProduct(ProductUpdateRequest product, Long id) {
        return productRepository.findById(id)
                .map(existingProduct -> updateProduct(existingProduct, product))
                .map(productRepository::save)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    private Product updateProduct(Product existingProduct, ProductUpdateRequest productUpdateRequest) {
        existingProduct.setName(productUpdateRequest.getName());
        existingProduct.setDescription(productUpdateRequest.getDescription());
        existingProduct.setPrice(productUpdateRequest.getPrice());
        existingProduct.setStock(productUpdateRequest.getStock());

        if (productUpdateRequest.getCategories() == null || productUpdateRequest.getCategories().isEmpty()) {
            throw new RuntimeException("Categories cannot be null or empty");
        }

        Category category = categoryRepository.findByCategoryName(productUpdateRequest.getCategories().getFirst().getCategoryName());

        if (category == null) {
            throw new RuntimeException("Category not found: ");
        }

        existingProduct.setCategories(List.of(category));
        return existingProduct;
    }


    @Override
    public void deleteProductById(Long id) {
        productRepository.findById(id)
                .ifPresentOrElse(productRepository::delete,
                        () -> {
                            throw new RuntimeException("Product not found");});
    }

    @Override
    public Page<Product> searchProduct(String keyword, Integer pageNO) {
        List<Product> fullList = productRepository.searchProducts(keyword);
        Pageable pageable = PageRequest.of(pageNO - 1, 10);
        int start = Math.min((int) pageable.getOffset(), fullList.size());
        int end = Math.min(start + pageable.getPageSize(), fullList.size());
        List<Product> pageList = fullList.subList(start, end);
        return new PageImpl<>(pageList, pageable, fullList.size());
    }

    @Override
    public Page<Product> getAllProduct(Integer pageNO) {
        Pageable pageable = PageRequest.of(pageNO - 1, 6);
        return this.productRepository.findAll(pageable);
    }

    @Override
    public Page<Product> getByCategory(Long id, Integer pageNO) {
        Pageable pageable = PageRequest.of(pageNO - 1, 6);
        return productRepository.findByCategory(id, pageable);
    }

    @Override
    public List<ProductDto> getConvertedProducts(List<Product> products) {
        return products.stream().map(this::convertToDto).toList();
    }

    @Override
    public ProductDto convertToDto(Product product) {
        ProductDto productDto = modelMapper.map(product, ProductDto.class);
        List<Image> images = imageRepository.findByProductId(product.getId());
        List<ImageDto> imageDto = images.stream()
                .map(image -> modelMapper.map(image, ImageDto.class))
                .toList();
        productDto.setImages(imageDto);
        return productDto;
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }
}
