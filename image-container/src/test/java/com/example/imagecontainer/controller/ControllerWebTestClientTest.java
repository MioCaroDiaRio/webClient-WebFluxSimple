package com.example.imagecontainer.controller;

import com.example.imagecontainer.entity.ImageEntity;
import com.example.imagecontainer.repository.ImageRepository;
import com.example.imagecontainer.service.ImageSaveService;
import com.example.imagecontainer.util.ImageCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
@WebFluxTest
@Import(ImageSaveService.class)
class ControllerWebTestClientTest {

    @MockBean
    private ImageRepository imageRepositoryMock;
    @Autowired
    private WebTestClient webTestClient;

    //добавить проверку blockHound
    private final ImageEntity image = ImageCreator.createImageEntity();

    @BeforeEach
    public void setUp(){
        BDDMockito.when(imageRepositoryMock.findImagesInRange(any(Long.class), any(Long.class)))
                .thenReturn(Flux.just(image));
        BDDMockito.when(imageRepositoryMock.saveImage(any(byte[].class), any(Instant.class)))
                .thenReturn(Mono.just(image));
    }

    @Test
    void findImagesShouldReturnFluxOfOneImage(){
        webTestClient
                .get()
                .uri("/api/imagesInRange?startId=1&endId=1")
                .exchange() //возвращает ответ контроллера
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.[0].id").isEqualTo(image.getId())
                .jsonPath("$.[0].createdAt").isEqualTo(image.getCreatedAt().toString());
    }

    @Test
    void saveImageShouldReturnSavedImage(){
        webTestClient
                .post()
                .uri("/api/saveImages")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(BodyInserters.fromValue(image.getImageData()))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(ImageEntity.class)
                .isEqualTo(image);
    }
}
