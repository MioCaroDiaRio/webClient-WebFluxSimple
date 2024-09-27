package com.example.imagecontainer.controller;

import com.example.imagecontainer.entity.Notification;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Sinks;

@RestController
public class NotificationController {

    private final Sinks.Many<Notification> sink;

    public NotificationController(Sinks.Many<Notification> sink) {
        this.sink = sink;
    }

    @PostMapping("/notifications")
    public void handleNotification(@RequestBody Notification notification) {
        sink.tryEmitNext(notification);
    }


}