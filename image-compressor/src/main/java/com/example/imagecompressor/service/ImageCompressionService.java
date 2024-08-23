package com.example.imagecompressor.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
@Slf4j
public class ImageCompressionService {

    private final WebClient webClient;
    private final ExecutorService executorService;

    public ImageCompressionService(WebClient webClient, ExecutorService executorService) {
        this.webClient = webClient;
        this.executorService = executorService;
    }

    //Сжимаем картинки асинхронно
    public Flux<Void> compressImage(byte[] imageBytes) {
        return Flux.defer(() -> {
            CompletableFuture<byte[]> compressionFuture = CompletableFuture.supplyAsync(() -> {
                try (ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
                     ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                    BufferedImage bufferedImage = ImageIO.read(bais);
                    int width = bufferedImage.getWidth();
                    int height = bufferedImage.getHeight();
                    int maxWidth = 150;
                    int maxHeight = 150;
                    double aspectRatio = (double) width / height;
                    int newWidth = (int) (maxWidth * aspectRatio);
                    int newHeight = maxHeight;
                    if (newWidth > maxWidth) {
                        newWidth = maxWidth;
                        newHeight = (int) (maxHeight / aspectRatio);
                    }
                    BufferedImage compressedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
                    Graphics2D graphics = compressedImage.createGraphics();
                    graphics.drawImage(bufferedImage, 0, 0, newWidth, newHeight, null);
                    graphics.dispose();
                    ImageIO.write(compressedImage, "jpg", bos);
                    //Вообще так лучше не делать, под асинхронный код логгеры настраиваются иначе
                    log.info("Сжато");
                    return bos.toByteArray();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }, executorService);

            return Mono.fromFuture(compressionFuture)
                    .flatMap(this::uploadCompressedImage);
        });
    }

    //Отправляем сжатые картинки на реактивное сохранение в микросервис сохранения
    private Mono<Void> uploadCompressedImage(byte[] compressedImageBytes) {
        return webClient.post().uri("http://localhost:8082/api/saveImages")
                .bodyValue(compressedImageBytes)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> Mono.error(new RuntimeException("какая-то ошибка клиента")))
                .onStatus(HttpStatusCode::is5xxServerError, response -> Mono.error(new RuntimeException("Какая-то ошибка сервера")))
                .bodyToMono(Void.class);

    }
}
