package com.example.imagecontainer.util;

import com.example.imagecontainer.entity.ImageEntity;

import java.time.Instant;

public class ImageCreator {
    public static ImageEntity createImageEntity() {
        Instant createdAt = Instant.now();
        return ImageEntity.builder().imageData(new byte[]{1, 2, 3})
                .createdAt(createdAt)
                .id(1L)
                .build();
    }
}
