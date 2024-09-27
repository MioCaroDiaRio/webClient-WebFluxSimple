package com.example.imagecontainer.service;

import com.example.imagecontainer.entity.Notification;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
public class ManagerSubscriber {
    @Autowired
    private KafkaTemplate<String, SimpleMailMessage> kafkaTemplate;

    @Autowired
    private Sinks.Many<Notification> notificationSink;

    @PostConstruct
    public void init() {
        notificationSink.asFlux().subscribe(notification -> {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setFrom("dmitry.kron20@yandex.ru");
            email.setTo("dmitrykron36@gmail.com");
            email.setSubject("Сообщение менеджеру: "+notification.getEmployee().getName());
            email.setText(notification.getEmployee().toString() + " "+notification.getMessage());
            kafkaTemplate.send("manager-topic", email);
        });
    }}

