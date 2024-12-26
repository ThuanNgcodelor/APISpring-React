package com.example.apijava.service.impl;

import com.example.apijava.dto.CachedPage;
import com.example.apijava.dto.ImageDto;
import com.example.apijava.dto.ProductDto;
import com.example.apijava.exceptions.ResourceNotFoundException;
import com.example.apijava.models.Category;
import com.example.apijava.models.Image;
import com.example.apijava.models.Product;
import com.example.apijava.repositorys.CategoryRepository;
import com.example.apijava.repositorys.ImageRepository;
import com.example.apijava.repositorys.ProductRepository;
import com.example.apijava.request.AddProductRequest;
import com.example.apijava.request.ProductUpdateRequest;
import com.example.apijava.service.inteface.ProductService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String PRODUCT_KEY = "product";


    private ModelMapper modelMapper;
    private final ImageRepository imageRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, RedisTemplate<String, Object> redisTemplate, ImageRepository imageRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.redisTemplate = redisTemplate;
        this.imageRepository = imageRepository;
        this.modelMapper = new ModelMapper();
    }

    @Override
    public Product addProduct(AddProductRequest productRequest) {

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

        Product newProduct = new Product(productRequest.getName(), productRequest.getDescription(), productRequest.getStock(), productRequest.getPrice(), categories);

        //save redis
        Product saveProduct = productRepository.save(newProduct);
        String key = PRODUCT_KEY + ":" + saveProduct.getId();
        redisTemplate.opsForValue().set(key, saveProduct);

        return saveProduct;
    }

    @Override
    public Product updateProduct(ProductUpdateRequest productUpdateRequest, Long id) {
        return productRepository.findById(id).map(existingProduct -> updateProductDetails(existingProduct, productUpdateRequest)).map(productRepository::save).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    private Product updateProductDetails(Product existingProduct, ProductUpdateRequest productUpdateRequest) {
        existingProduct.setName(productUpdateRequest.getName());
        existingProduct.setDescription(productUpdateRequest.getDescription());
        existingProduct.setPrice(productUpdateRequest.getPrice());
        existingProduct.setStock(productUpdateRequest.getStock());

        if (productUpdateRequest.getCategories() == null || productUpdateRequest.getCategories().isEmpty()) {
            throw new RuntimeException("Categories cannot be null or empty");
        }

        Category category = categoryRepository.findByCategoryName(productUpdateRequest.getCategories().get(0).getCategoryName());

        if (category == null) {
            throw new RuntimeException("Category not found: " + productUpdateRequest.getCategories().get(0).getCategoryName());
        }

        existingProduct.setCategories(List.of(category));
        return existingProduct;
    }


    @Override
    public void deleteProductById(Long id) {
        productRepository.findById(id).ifPresentOrElse(product -> {
            productRepository.delete(product);
            clearCache(product.getId());
        }, () -> {
            throw new RuntimeException("Product not found");
        });
    }

    @Override
    public Page<Product> searchProduct(String keyword, Integer pageNO) {

        String key = PRODUCT_KEY + ":pageNo:" + pageNO + ":keyword:" + keyword;
        CachedPage<Product> cachedPage = (CachedPage<Product>) redisTemplate.opsForValue().get(key);
        if (cachedPage != null) {
            return new PageImpl<>(cachedPage.getContent(), PageRequest.of(pageNO - 1, 10), cachedPage.getTotalElements());
        }

        List<Product> fullList = productRepository.searchProducts(keyword);
        Pageable pageable = PageRequest.of(pageNO - 1, 10);
        int start = Math.min((int) pageable.getOffset(), pageable.getPageSize());
        int end = Math.min(start + pageable.getPageSize(), fullList.size());
        List<Product> pageList = fullList.subList(start, end);
        Page<Product> page = new PageImpl<>(pageList, pageable, fullList.size());
        redisTemplate.opsForValue().set(key, page);
        return page;
    }

    @Override
    public Page<Product> getAllProduct(Integer pageNO) {
        String key = PRODUCT_KEY + ":pageNo:" + pageNO;

        CachedPage<Product> cachedPage = (CachedPage<Product>) redisTemplate.opsForValue().get(key);
        if (cachedPage != null) {
            return new PageImpl<>(cachedPage.getContent(), PageRequest.of(pageNO - 1, 10), cachedPage.getTotalElements());
        }

        Pageable pageable = PageRequest.of(pageNO - 1, 10);
        Page<Product> page = productRepository.findAll(pageable);
        CachedPage<Product> cacheData = new CachedPage<>(page.getContent(), page.getTotalElements());

        redisTemplate.opsForValue().set(key, cacheData);
        return page;
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
//        ProductDto productDto = modelMapper.map(product, ProductDto.class);
//        List<Image> images = imageRepository.findByProductId(product.getId());
//        List<ImageDto> imageDto = images.stream().map(image -> modelMapper.map(image, ImageDto.class)).toList();
//        productDto.setImages(imageDto);
//        return productDto;
        return null;
    }

    //Get redis if false -> db
    @Override
    public Product getProductById(Long id) {
        String key = PRODUCT_KEY + ":" + id;
        Product cachedProduct = (Product) redisTemplate.opsForValue().get(key);
        if (cachedProduct != null) {
            return cachedProduct;
        }
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        redisTemplate.opsForValue().set(key, product);
        return product;
    }

    private void clearCache(Long id) {
        String key = PRODUCT_KEY + ":" + id;
        redisTemplate.delete(key);
    }
}
