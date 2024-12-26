package com.example.apijava.service.inteface;

import com.example.apijava.models.Brand;

import java.util.List;

public interface BrandService {
    Brand getBrand(Long id);
    List<Brand> getAllBrands();
    Brand createBrand(Brand brand);
    void deleteBrand(Long id);
    Brand updateBrand(Long id, Brand brand);
}
