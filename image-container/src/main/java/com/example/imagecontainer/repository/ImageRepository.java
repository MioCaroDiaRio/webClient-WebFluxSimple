package com.example.imagecontainer.repository;

import com.example.imagecontainer.entity.ImageEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Repository
public interface ImageRepository extends R2dbcRepository<ImageEntity, Long> {
    @Query("INSERT INTO images (image_data, created_at) VALUES ($1, $2) RETURNING *")
    Mono<ImageEntity> saveImage(byte[] imageData, Instant createdAt);
    @Query("SELECT * FROM images ORDER BY id LIMIT $2 OFFSET $1")
    Flux<ImageEntity> findImagesInRange(Long offset, Long limit);}