package com.example.apijava.service.impl;

import com.example.apijava.exceptions.AlreadyExistsException;
import com.example.apijava.exceptions.ResourceNotFoundException;
import com.example.apijava.models.Category;
import com.example.apijava.repositorys.CategoryRepository;
import com.example.apijava.service.inteface.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Override
    public Category updateCategory(Category category, Long id) {
        return Optional.ofNullable(getCategoryById(id)).map(oldCategory -> {
            oldCategory.setCategoryName(category.getCategoryName());
            oldCategory.setCategoryStatus(category.isCategoryStatus());
            return categoryRepository.save(oldCategory);
        }) .orElseThrow(()-> new ResourceNotFoundException("Category not found!"));
    }

    @Override
    public void deleteCategoryById(Long id) {
        categoryRepository.findById(id)
                .ifPresentOrElse(categoryRepository::delete, () -> {
                    throw new ResourceNotFoundException("Category not found!");
                });
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Category not found!"));
    }

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category addCategory(Category category) {
        return Optional.of(category)
                .filter(c -> !categoryRepository.existsByCategoryName(c.getCategoryName()))
                .map(categoryRepository::save)
                .orElseThrow(() -> new AlreadyExistsException(category.getCategoryName() + " already exists"));
    }

    @Override
    public List<Category> searchCategory(String keyword) {
        return categoryRepository.searchCategory(keyword);
    }

    @Override
    public Page<Category> getAllPage(Integer pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 10);
        return categoryRepository.findAll(pageable);
    }

    @Override
    public Page<Category> searchCategory(String keyword, Integer pageNo) {
        List<Category> fullList = categoryRepository.searchCategory(keyword);

        Pageable pageable = PageRequest.of(pageNo - 1, 10);
        int start = Math.min((int) pageable.getOffset(), fullList.size());
        int end = Math.min(start + pageable.getPageSize(), fullList.size());

        List<Category> pageList = fullList.subList(start, end);
        return new PageImpl<>(pageList, pageable, fullList.size());
    }
}
