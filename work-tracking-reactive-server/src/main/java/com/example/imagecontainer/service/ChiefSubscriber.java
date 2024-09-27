package com.example.imagecontainer.service;

import com.example.imagecontainer.entity.Notification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

@Service
public class ChiefSubscriber {
    private final KafkaTemplate<String, String> kafkaTemplate;

    private final Sinks.Many<Notification> notificationSink;
    private final ObjectMapper objectMapper;

    public ChiefSubscriber(KafkaTemplate<String, String> kafkaTemplate, Sinks.Many<Notification> notificationSink, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.notificationSink = notificationSink;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        notificationSink.asFlux().subscribe(notification -> {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setFrom("dmitry.kron20@yandex.ru");
            email.setSubject("Сообщение боссу: "+notification.getEmployee().getName());
            email.setText(notification.getEmployee().toString() + " "+notification.getMessage());
            String jsonMessage = null;
            try {
                jsonMessage = objectMapper.writeValueAsString(email);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            kafkaTemplate.send("chief-topic", jsonMessage);
        });
    }
}
