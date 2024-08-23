package com.example.imagedownloader.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ImageCorrectDownloadService {

    private final WebClient webClient;

    public ImageCorrectDownloadService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<Void> downloadImages(String url) {
        log.info("Начало работы");
        extractImageUrls(url, 1)
                .flatMapMany(Flux::fromIterable)
                .flatMap(this::downloadAndSendImageBytesToCompressionService)
                .subscribe();
        return Mono.empty();
    }

    private Mono<Void> downloadAndSendImageBytesToCompressionService(String imageUrl) {
        log.info("что-то");
        return downloadImageBytes(imageUrl)
                .flatMap(imageBytes -> Mono.when(
                        sendImageBytesToCompressionService(imageBytes)
                                .subscribeOn(Schedulers.boundedElastic())
                ));
    }
    //асинхронно читаем байты
    private Mono<byte[]> downloadImageBytes(String imageUrl) {
        return webClient.get().uri(imageUrl)
                .retrieve()
                .bodyToMono(byte[].class).retry(3);
    }


    private Mono<Void> sendImageBytesToCompressionService(byte[] imageBytes) {
        return webClient
                .post()
                .uri("http://localhost:8081/api/compress")
                .bodyValue(imageBytes)
                .retrieve()
                .bodyToMono(Void.class);
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
}
