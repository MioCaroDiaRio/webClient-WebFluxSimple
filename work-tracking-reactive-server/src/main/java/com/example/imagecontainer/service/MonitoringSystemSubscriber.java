package com.example.imagecontainer.service;

import com.example.imagecontainer.entity.Notification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;

@Service
public class MonitoringSystemSubscriber {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Sinks.Many<Notification> notificationSink;
    private final ObjectMapper objectMapper;

    public MonitoringSystemSubscriber(KafkaTemplate<String, String> kafkaTemplate, Sinks.Many<Notification> notificationSink, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.notificationSink = notificationSink;
        this.objectMapper = objectMapper;
    }


    @PostConstruct
    public void init() {
        notificationSink.asFlux().subscribe(notification -> {
            //А тут мы вообще просто отправим это куда-то
            String jsonMessage = null;
            try {
                jsonMessage = objectMapper.writeValueAsString(notification);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            kafkaTemplate.send("monitoring-topic", jsonMessage);
        });
    }

}
