package com.example.apijava.controller.admin;

import com.example.apijava.models.Brand;
import com.example.apijava.service.inteface.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/brand")
public class BrandController {
    private final BrandService brandService;

    @Autowired
    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping
    public ResponseEntity<List<Brand>> getAllBrands() {
        List<Brand> brands = brandService.getAllBrands();
        return new ResponseEntity<>(brands, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<Brand> addBrand(@RequestBody Brand brand) {
        Brand createdBrand = brandService.createBrand(brand);
        return new ResponseEntity<>(createdBrand, HttpStatus.CREATED);
    }
}
