package com.example.imagecontainer.service;

import com.example.imagecontainer.entity.Notification;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
public class MonitoringSystemSubscriber {
    @Autowired
    private KafkaTemplate<String, Notification> kafkaTemplate;

    @Autowired
    private Sinks.Many<Notification> notificationSink;

    @PostConstruct
    public void init() {
        notificationSink.asFlux().subscribe(notification -> {
            //А тут мы вообще просто отправим это куда-то
            kafkaTemplate.send("monitoring-topic", notification);
        });
    }

}
