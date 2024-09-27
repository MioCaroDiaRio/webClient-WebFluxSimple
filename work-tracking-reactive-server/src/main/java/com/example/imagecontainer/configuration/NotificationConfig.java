package com.example.imagecontainer.configuration;

import com.example.imagecontainer.entity.Notification;
import com.example.imagecontainer.repository.EmployeeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Configuration
public class NotificationConfig {
    @Bean
    public Sinks.Many<Notification> notificationSink() {
        return Sinks.many().multicast().onBackpressureBuffer();
    }
}