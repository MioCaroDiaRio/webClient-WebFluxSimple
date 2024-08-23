package com.example.imagecontainer.service;

import com.example.imagecontainer.entity.ImageEntity;
import com.example.imagecontainer.repository.ImageRepository;
import com.example.imagecontainer.util.ImageCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class StepVerifierTest {
    @InjectMocks
    public ImageSaveService imageSaveService;

    @Mock
    public ImageRepository imageRepositoryMock;
    private final ImageEntity image = ImageCreator.createImageEntity();

    @BeforeEach
    public void setUp(){
        BDDMockito.when(imageRepositoryMock.findImagesInRange(any(Long.class), any(Long.class)))
                .thenReturn(Flux.just(image));
        BDDMockito.when(imageRepositoryMock.saveImage(any(byte[].class), any(Instant.class)))
                .thenReturn(Mono.just(image));
    }


    @Test
    void findImagesInRangeShouldReturnFluxOfImages(){
        StepVerifier.create(imageSaveService.findImagesInRange(1L, 10L))
                .expectSubscription()
                .expectNext(image)
                .verifyComplete();
    }

    @Test
    void saveImageShouldCreateImage(){
        StepVerifier.create(imageSaveService.saveImage(image.getImageData()))
                .expectSubscription()
                .expectNext(image)
                .verifyComplete();
    }

}
