package com.example.apijava.service.impl;

import com.example.apijava.dto.ImageDto;
import com.example.apijava.exceptions.ResourceNotFoundException;
import com.example.apijava.models.Image;
import com.example.apijava.models.Product;
import com.example.apijava.repositorys.ImageRepository;
import com.example.apijava.repositorys.ProductRepository;
import com.example.apijava.service.inteface.ImageService;
import com.example.apijava.service.inteface.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImageServiceImpl implements ImageService {
    private final ImageRepository imageRepository;
    private final ProductService productService;
    private final ProductRepository productRepository;

    @Autowired
    public ImageServiceImpl(ImageRepository imageRepository, ProductService productService, ProductRepository productRepository) {
        this.imageRepository = imageRepository;
        this.productService = productService;
        this.productRepository = productRepository;
    }

    @Override
    public Image getImageById(Long id) {
       return imageRepository.findById(id)
               .orElseThrow(()-> new ResourceNotFoundException("Image not found" + id));
    }

    @Override
    public void updateImage(MultipartFile file, Long imageId) {
        Image image = getImageById(imageId);
        if (image == null) {
            throw new ResourceNotFoundException("Image not found with ID: " + imageId);
        }
        try {
            image.setFileName(file.getOriginalFilename());
            image.setFileType(file.getContentType());
            image.setImage(new SerialBlob(file.getBytes()));
            imageRepository.save(image);
        } catch (Exception e) {
            throw new RuntimeException("Error updating image: " + e.getMessage(), e);
        }
    }


    @Override
    public void deleteImageById(Long id) {
        imageRepository.findById(id)
                .ifPresentOrElse(imageRepository::delete, () -> {
                    throw new ResourceNotFoundException("Image not found");
                });
    }

    @Override
    public List<ImageDto> saveImages(Long productId, List<MultipartFile> files) {
        // Lấy đối tượng Product trực tiếp từ cơ sở dữ liệu
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        List<ImageDto> saveImageDto = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                Image image = new Image();
                image.setFileName(file.getOriginalFilename());
                image.setFileType(file.getContentType());
                image.setImage(new SerialBlob(file.getBytes()));
                image.setProduct(product);

                String buildDownloadUrl = "/admin/images/download/";
                String downloadUrl = buildDownloadUrl + image.getFileName();
                image.setDownloadUrl(downloadUrl);

                Image savedImage = imageRepository.save(image);

                savedImage.setDownloadUrl(buildDownloadUrl + savedImage.getId());
                imageRepository.save(savedImage);

                ImageDto imageDto = new ImageDto();
                imageDto.setId(savedImage.getId());
                imageDto.setFileName(savedImage.getFileName());
                imageDto.setDownloadUrl(savedImage.getDownloadUrl());
                saveImageDto.add(imageDto);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return saveImageDto;
    }

}
