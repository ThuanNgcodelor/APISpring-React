package com.example.apijava.controller.admin;


import com.example.apijava.exceptions.AlreadyExistsException;
import com.example.apijava.exceptions.ResourceNotFoundException;
import com.example.apijava.models.Category;
import com.example.apijava.response.ApiResponse;
import com.example.apijava.service.inteface.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/admin/category")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String home() {
        return "Hello World";
    }

    //http://localhost:8080/admin/category/add
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addCategory(@RequestBody Category categoryName) {
        try {
            Category newCategory = categoryService.addCategory(categoryName);
            return ResponseEntity.ok(new ApiResponse("Category added successfully", newCategory));
        } catch (AlreadyExistsException e) {
            return ResponseEntity.status(CONFLICT).body(new ApiResponse(e.getMessage(), null));
        }
    }

    //    http://localhost:8080/admin/category/delete/1
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategoryById(id);
            return ResponseEntity.ok(new ApiResponse("Found", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    //    http://localhost:8080/admin/category/1
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getCategoryById(@PathVariable Long id) {
        try {
            Category theCategory = categoryService.getCategoryById(id);
            return ResponseEntity.ok(new ApiResponse("Found", theCategory));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    //    http://localhost:8080/admin/category/update/1
    @PatchMapping("/update/{id}")
    public ResponseEntity<ApiResponse> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        try {
            Category updatedCategory = categoryService.updateCategory(category, id);
            return ResponseEntity.ok(new ApiResponse("Update success!", updatedCategory));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    //    http://localhost:8080/admin/category/list?pageNo=1
    @GetMapping("/list")
    public ResponseEntity<ApiResponse> getAllCategory(@RequestParam(defaultValue = "1") Integer pageNo) {
        return ResponseEntity.ok(new ApiResponse("List of categories retrieved successfully", categoryService.getAllPage(pageNo)));
    }


    //    http://localhost:8080/admin/category/search?keyword=Thuận&pageNo=1
    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchCategory(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer pageNo
    ) {
        return ResponseEntity.ok(new ApiResponse("List of categories retrieved successfully", categoryService.searchCategory(keyword, pageNo)));
    }
}
