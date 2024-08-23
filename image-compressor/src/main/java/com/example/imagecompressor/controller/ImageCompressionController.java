package com.example.imagecompressor.controller;

import com.example.imagecompressor.service.ImageCompressionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api")
public class ImageCompressionController {

    private final ImageCompressionService imageCompressionService;

    public ImageCompressionController(ImageCompressionService imageCompressionService) {
        this.imageCompressionService = imageCompressionService;
    }

    @PostMapping("/compress")
    public Flux<Void> compressImage(@RequestBody byte[] imageBytes) {
        return imageCompressionService.compressImage(imageBytes);
    }
}