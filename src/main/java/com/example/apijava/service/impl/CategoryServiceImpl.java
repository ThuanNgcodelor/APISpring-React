package com.example.apijava.service.impl;

import com.example.apijava.dto.CachedPage;
import com.example.apijava.exceptions.AlreadyExistsException;
import com.example.apijava.exceptions.ResourceNotFoundException;
import com.example.apijava.models.Category;
import com.example.apijava.repositorys.CategoryRepository;
import com.example.apijava.service.inteface.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String CATEGORY_KEY = "category";

    public CategoryServiceImpl(CategoryRepository categoryRepository, RedisTemplate<String, Object> redisTemplate) {
        this.categoryRepository = categoryRepository;
        this.redisTemplate = redisTemplate;
    }

    private static int count = 0;
    private static int count2 = 0;

    //Get all category form redis
    //If redis = null --> get form db

    //echo "get http://localhost:8080/admin/category/2" | vegeta attack -name=2000qps -rate=100 -duration=10s | vegeta report
    //vegeta attack -targets=targets.txt -name=2000qps -rate=100 -duration=10s | vegeta report
    @Override
    public Category getCategoryById(Long id) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        String key = CATEGORY_KEY + ":" + id;
        Category category = (Category) ops.get(key);
        if (category != null) {
            count2++;
            System.out.println("Lấy trong ở redis HEHEHE:" + count2);
            return category;
        }

        try {
            Optional<Category> optionalCategory = categoryRepository.findById(id);
            count++;
            Thread.sleep(1000);
            if (optionalCategory.isPresent()) {
                category = optionalCategory.get();
                ops.set(key, category);
            }
            System.out.println("Lấy ở database rồi HUHU " + count);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return category;
    }

    @Override
    public Category addCategory(Category category) {
        return Optional.of(category)
                .filter(c -> !categoryRepository.existsByCategoryName(c.getCategoryName()))
                .map(categoryRepository::save)
                .map(saveCategory -> {
                    // Set cache khi cập nhật thành công
                    ValueOperations<String, Object> ops = redisTemplate.opsForValue();
                    String key = CATEGORY_KEY + ":" + saveCategory.getCategoryId();

                    ops.set(key, saveCategory);
//                     Xóa cache của trang cuối cùng
                    updateCacheCategory();

                    return saveCategory;
                })
                .orElseThrow(() -> new AlreadyExistsException(category.getCategoryName() + " already exists"));
    }

    //Get id va update -> clear cache
    // 1 đầu tiên khi update thành công -> clear cache với id
    // 2 sẽ set cache voi categoryId
    // 3 clear cache với pageNo gần
    @Override
    public Category updateCategory(Category category, Long id) {
        return Optional.ofNullable(getCategoryById(id))
                .map(oldCategory -> {
                    oldCategory.setCategoryName(category.getCategoryName());
                    oldCategory.setCategoryStatus(category.isCategoryStatus());
                    Category updatedCategory = categoryRepository.save(oldCategory);
                    clearCache(id);

                    ValueOperations<String, Object> ops = redisTemplate.opsForValue();
                    String key = CATEGORY_KEY + ":" + id;
                    ops.set(key, updatedCategory);

                    updateCacheCategory();

                    return updatedCategory;
                })
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    //dùng để update lại pageNo gần nhất và set lại cache của nó
    private void updateCacheCategory(){
        long totalCategories = categoryRepository.count();
        int pageSize = 10;
        long totalPages = (totalCategories + pageSize - 1) / pageSize;

        String lastPageKey = CATEGORY_KEY + ":pageNo:" + totalPages;
        redisTemplate.delete(lastPageKey);

        Pageable pageable = PageRequest.of((int) totalPages - 1, pageSize);
        Page<Category> page = categoryRepository.findAll(pageable);
        CachedPage<Category> cacheData = new CachedPage<>(page.getContent(), page.getTotalElements());
        redisTemplate.opsForValue().set(lastPageKey, cacheData);
    }


    @Override
    public void deleteCategoryById(Long id) {
        categoryRepository.findById(id).ifPresentOrElse(category -> {
            categoryRepository.delete(category);
            clearCache(id);

            updateCacheCategory();
        }, () -> {
            throw new ResourceNotFoundException("Category not found");
        });
    }

    @Override
    public List<Category> searchCategory(String keyword) {
        return categoryRepository.searchCategory(keyword);
    }

    @Override
    public Page<Category> getAllPage(Integer pageNo) {
        String key = CATEGORY_KEY + ":pageNo:" + pageNo;

        // Kiểm tra dữ liệu cache
        CachedPage<Category> cachedPage = (CachedPage<Category>) redisTemplate.opsForValue().get(key);
        if (cachedPage != null) {
            // Lấy từ Redis
            return new PageImpl<>(cachedPage.getContent(), PageRequest.of(pageNo - 1, 10), cachedPage.getTotalElements());
        }

        // Query dữ liệu từ database
        Pageable pageable = PageRequest.of(pageNo - 1, 10);
        Page<Category> page = categoryRepository.findAll(pageable);

        // Chuyển đổi sang CachedPage và lưu vào Redis
        CachedPage<Category> cacheData = new CachedPage<>(page.getContent(), page.getTotalElements());
        redisTemplate.opsForValue().set(key, cacheData);

        return page;
    }


    @Override
    public Page<Category> searchCategory(String keyword, Integer pageNo) {
        String key = CATEGORY_KEY + ":pageNo:" + pageNo + ":keyword:" + keyword;

        //Query vào redis
        CachedPage<Category> cachedPage = (CachedPage<Category>) redisTemplate.opsForValue().get(key);
        if (cachedPage != null) {
            return new PageImpl<>(cachedPage.getContent(), PageRequest.of(pageNo - 1, 10), cachedPage.getTotalElements());
        }

        //Query vào db
        List<Category> fullList = categoryRepository.searchCategory(keyword);
        Pageable pageable = PageRequest.of(pageNo - 1, 10);
        int start = Math.min((int) pageable.getOffset(), pageable.getPageSize());
        int end = Math.min(start + pageable.getPageSize(), fullList.size());

        List<Category> pageList = fullList.subList(start, end);
        Page<Category> page = new PageImpl<>(pageList, pageable, fullList.size());

        CachedPage<Category> cacheData = new CachedPage<>(page.getContent(), page.getTotalElements());
        redisTemplate.opsForValue().set(key, cacheData);
        return page;
    }

    private void clearCache(Long id) {
        String key = CATEGORY_KEY + ":" + id;
        redisTemplate.delete(key);
        System.out.println("clear cache");
    }
}
