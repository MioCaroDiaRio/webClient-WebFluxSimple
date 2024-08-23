package com.example.imagedownloader.controller;

import com.example.imagedownloader.service.ImageCorrectDownloadService;
import com.example.imagedownloader.service.ImageDownloadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class ImageController {

    private final ImageDownloadService imageDownloadService;
    private final ImageCorrectDownloadService imageCorrectDownloadService;

    public ImageController(ImageDownloadService imageDownloadService, ImageCorrectDownloadService imageCorrectDownloadService) {
        this.imageDownloadService = imageDownloadService;
        this.imageCorrectDownloadService = imageCorrectDownloadService;
    }

    //поскольку обращение идет "с фронта" конкретно тут возврат Mono не дает
    //никакого преимущества в перфомансе
    @GetMapping("/upload")
    public Mono<ResponseEntity<String>> downloadImages(@RequestParam String url) {
        return imageDownloadService.downloadImages(url)
                .then(Mono.just(ResponseEntity.ok("Картинки скачаны")));
    }

    @GetMapping("/correctUpload")
    public Mono<ResponseEntity<String>> downloadImagesCorrectly(@RequestParam String url) {
        return imageCorrectDownloadService.downloadImages(url)
                .then(Mono.just(ResponseEntity.ok("Картинки скачаны")));
    }
}