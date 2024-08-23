package com.example.imagedownloader.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ImageDownloadService {

    private final WebClient webClient;

    public ImageDownloadService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<Void> downloadImages(String url) {
        log.info("Начало работы");

        return extractImageUrls(url, 1)
                .flatMapMany(Flux::fromIterable)
                .parallel(10)
                .flatMap(this::downloadImageBytes)
                .sequential()
                .collectList()
                .doOnNext(this::sendImageBytesToCompressionService)
                .then();
    }

    //Игнорируйте, просто попытка асинхронно скрейпить страницу, есть масса вариантов
    //реализации, в т.ч. и через CompletableFuture. Сделано просто как пример
    //возможной работы, в целом логичнее было бы добавить параллелизм
    private Mono<List<String>> extractImageUrls(String url, int page) {
        String apiUrl = url + "&page=" + page;
        return webClient
                .get()
                .uri(apiUrl)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(html -> {
                    JsonNode jsonNode = null;
                    try {
                        jsonNode = new ObjectMapper().readTree(html);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    JsonNode resultsNode = jsonNode.get("results");
                    List<String> imageUrls = new ArrayList<>();
                    for (JsonNode resultNode : resultsNode) {
                        String imgUrl = resultNode.get("urls").get("regular").asText();
                        imageUrls.add(imgUrl);
                    }
                    if (page < 30) {
                        //вернем первые 30 страниц поиска
                        return extractImageUrls(url, page + 1)
                                .map(nextPageImageUrls -> {
                                    imageUrls.addAll(nextPageImageUrls);
                                    return imageUrls;
                                });
                    } else {
                        return Mono.just(imageUrls);
                    }
                });
    }

    //асинхронно читаем байты
    private Mono<byte[]> downloadImageBytes(String imageUrl) {
        return webClient.get().uri(imageUrl)
                .retrieve()
                .bodyToMono(byte[].class);
    }

    private void sendImageBytesToCompressionService(List<byte[]> imageBytes) {
        Flux.fromIterable(imageBytes)
                .flatMap(imageByte -> sendImageBytesToCompressionService(imageByte))
                .subscribe();
    }

    private Mono<Void> sendImageBytesToCompressionService(byte[] imageBytes) {
        return webClient
                .post()
                .uri("http://localhost:8081/api/compress")
                .bodyValue(imageBytes)
                .retrieve()
                .bodyToMono(Void.class);
    }
}
