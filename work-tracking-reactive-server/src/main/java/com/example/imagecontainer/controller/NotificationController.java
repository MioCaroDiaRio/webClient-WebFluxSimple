package com.example.imagecontainer.controller;

import com.example.imagecontainer.entity.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Sinks;

@RestController
public class NotificationController {

    @Autowired
    private Sinks.Many<Notification> sink;

    public NotificationController() {
        sink = Sinks.many().multicast().onBackpressureBuffer();
    }

    @PostMapping("/notifications")
    public void handleNotification(@RequestBody Notification notification) {
        sink.tryEmitNext(notification);
    }


}