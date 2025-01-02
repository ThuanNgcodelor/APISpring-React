package com.example.apijava.controller.admin;

import com.example.apijava.dto.ProductDto;
import com.example.apijava.exceptions.ResourceNotFoundException;
import com.example.apijava.models.Product;
import com.example.apijava.request.AddProductRequest;
import com.example.apijava.request.ProductUpdateRequest;
import com.example.apijava.response.ApiResponse;
import com.example.apijava.service.inteface.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/admin/product")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addProduct(@RequestBody AddProductRequest product) {
        try {
            Product newProduct = productService.addProduct(product);
            ProductDto productDto = productService.convertToDto(newProduct);
            return ResponseEntity.ok(new ApiResponse("Product added successfully", productDto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }

        //Test api  http://localhost:8080/admin/product/add
//            {
//                "name": "Product Name",
//                    "description": "Product Description",
//                    "stock": 50,
//                    "price": 19.99,
//                    "categories": [
//                { "categoryName": "hentai" },
//                { "categoryName": "Books" }
//      ]
//            }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse> updateProduct(@PathVariable("id") Long id,
                                                     @RequestBody ProductUpdateRequest product) {
        try {
            Product updatedProduct = productService.updateProduct(product, id);
            ProductDto productDto = productService.convertToDto(updatedProduct);
            return ResponseEntity.ok(new ApiResponse("Product updated successfully", productDto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }

        //update lỗi chưa làm được tại sao nó lại trả về 400 éo biết
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getProductById(@PathVariable Long id) {
        try {
            ProductDto productDto = productService.getProductById(id);
            return ResponseEntity.ok(new ApiResponse("success", productDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
        //http://localhost:8080/admin/product/6
        //Test ok
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProductById(id);
            return ResponseEntity.ok(new ApiResponse("Delete success", id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
        //Test ok
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse> getAllProduct(@RequestParam(defaultValue = "1") Integer pageNo){
        return ResponseEntity.ok(new ApiResponse("List of product",productService.getAllProduct(pageNo)));
//
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchProduct(@RequestParam String keyword,
                                                     @RequestParam(defaultValue = "1") Integer pageNo){
        return ResponseEntity.ok(new ApiResponse("List of product",productService.searchProduct(keyword,pageNo)));
//    http://localhost:8080/admin/product/search?keyword=Thuận&pageNo=1

    }
}
