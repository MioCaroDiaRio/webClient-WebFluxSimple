package com.example.imagecompressor.service;

import com.example.imagecompressor.entity.Employee;
import com.example.imagecompressor.entity.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class NotificationService {
    @Autowired
    private WebClient webClient;

    public void sendNotification(Notification notification) {
        webClient.post()
                .uri("http://localhost:8082/notifications")
                .bodyValue(notification)
                .retrieve()
                .toBodilessEntity()
                .subscribe();
    }
}