package com.example.apijava.controller.admin;


import com.example.apijava.dto.ImageDto;
import com.example.apijava.exceptions.ResourceNotFoundException;
import com.example.apijava.models.Image;
import com.example.apijava.response.ApiResponse;
import com.example.apijava.service.inteface.ImageService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;
import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/admin/images")
public class ImageController {
    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }
    //http://localhost:8080/admin/images/upload?productId=1
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse> saveImage(@RequestParam List<MultipartFile> files,
                                                 @RequestParam Long productId) {
        try {
            List<ImageDto> imageDto = imageService.saveImages(productId, files);
            return ResponseEntity.ok(new ApiResponse("Upload success!", imageDto));

        }catch (Exception e){
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    //http://localhost:8080/admin/images/download/1
    @GetMapping("/download/{imageId}")
    public ResponseEntity<Resource> downloadImage(@PathVariable Long imageId) throws SQLException {
        Image image = imageService.getImageById(imageId);
        ByteArrayResource resource = new ByteArrayResource(image.getImage().getBytes(1, (int) image.getImage().length()));
        return  ResponseEntity.ok().contentType(MediaType.parseMediaType(image.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" +image.getFileName() + "\"")
                .body(resource);
    }

    @PutMapping("/update/{imageId}")
    public ResponseEntity<ApiResponse> updateImage(@PathVariable Long imageId, @RequestPart("files") MultipartFile file) {
        try {
            Image image = imageService.getImageById(imageId);
            if (image != null) {
                imageService.updateImage(file, imageId);
                return ResponseEntity.ok(new ApiResponse("Update success!", image));
            }
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Update image failed", null));
    }

    @DeleteMapping("/delete/{imageId}")
    public ResponseEntity<ApiResponse> deleteImage(@PathVariable Long imageId) {
        try {
            Image image = imageService.getImageById(imageId);
            if(image != null) {
                imageService.deleteImageById( imageId);
                return ResponseEntity.ok(new ApiResponse("Delete success!", null));
            }
        } catch (ResourceNotFoundException e) {
            return  ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Delete failed!", INTERNAL_SERVER_ERROR));
    }
}
