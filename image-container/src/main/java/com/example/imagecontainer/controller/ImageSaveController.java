package com.example.imagecontainer.controller;

import com.example.imagecontainer.entity.ImageEntity;
import com.example.imagecontainer.service.ImageSaveService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class ImageSaveController {
    private final ImageSaveService imageSaveService;

    public ImageSaveController(ImageSaveService imageSaveService) {
        this.imageSaveService = imageSaveService;
    }

    @PostMapping("/saveImages")
    public Mono<ImageEntity> saveImage(@RequestBody byte[] imageData) {
        return imageSaveService.saveImage(imageData);
    }
    @GetMapping("/imagesInRange")
    public Flux<ImageEntity> findImagesInRange(@RequestParam Long startId, @RequestParam Long endId) {
        return imageSaveService.findImagesInRange(startId, endId);
    }
}