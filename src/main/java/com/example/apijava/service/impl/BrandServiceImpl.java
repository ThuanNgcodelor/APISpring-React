package com.example.apijava.service.impl;

import com.example.apijava.models.Brand;
import com.example.apijava.repositorys.BrandRepository;
import com.example.apijava.service.inteface.BrandService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class BrandServiceImpl implements BrandService {
    private final BrandRepository brandRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String REDIS_KEY_BRAND = "brands";

    @Autowired
    public BrandServiceImpl(BrandRepository brandRepository, RedisTemplate<String, Object> redisTemplate) {
        this.brandRepository = brandRepository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Brand getBrand(Long id) {
        ValueOperations<String, Object> valueOps = redisTemplate.opsForValue();
        String key = REDIS_KEY_BRAND + ":" + id;
        System.out.println("Get Brand from Redis");
        // Check in Redis
        Brand brand = (Brand) valueOps.get(key);
        if (brand != null) {
            return brand;
        }

        // Fallback to database
        Optional<Brand> optionalBrand = brandRepository.findById(id);
        if (optionalBrand.isPresent()) {
            brand = optionalBrand.get();
            valueOps.set(key, brand); // Cache in Redis
        }
        return brand;
    }

    @Override
    public List<Brand> getAllBrands() {
        ValueOperations<String, Object> valueOps = redisTemplate.opsForValue();
        List<Brand> brands = (List<Brand>) valueOps.get(REDIS_KEY_BRAND);

        System.out.println("Get Brand from Redis");
        if (brands != null) {
            return brands;
        }
        System.out.println("Get Brand from db");

        brands = brandRepository.findAll();
        valueOps.set(REDIS_KEY_BRAND, brands); // Cache in Redis
        return brands;
    }

    @Override
    public Brand createBrand(Brand brand) {
        Brand savedBrand = brandRepository.save(brand);
        clearCache(); // Invalidate Redis cache
        return savedBrand;
    }

    @Override
    public void deleteBrand(Long id) {
        brandRepository.deleteById(id);
        clearCache(); // Invalidate Redis cache
    }

    @Override
    public Brand updateBrand(Long id, Brand brand) {
        if (!brandRepository.existsById(id)) {
            return null;
        }

        brand.setId(id);
        Brand updatedBrand = brandRepository.save(brand);
        clearCache();
        return updatedBrand;
    }

    private void clearCache() {
        redisTemplate.delete(REDIS_KEY_BRAND);
    }
}
