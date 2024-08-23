package com.example.imagecontainer.service;

import com.example.imagecontainer.entity.ImageEntity;
import com.example.imagecontainer.repository.ImageRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
public class ImageSaveService {
    private final ImageRepository imageRepository;

    public ImageSaveService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public Mono<ImageEntity> saveImage(byte[] imageData) {
        Instant createdAt = Instant.now();
        return imageRepository.saveImage(imageData, createdAt);
    }
    public Flux<ImageEntity> findImagesInRange(Long startId, Long endId) {
        Long offset = startId - 1;
        Long limit = endId - startId + 1;
        return imageRepository.findImagesInRange(offset, limit);
    }
}
