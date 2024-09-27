package com.example.imagecontainer;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;


import java.time.Duration;

@Configuration
public class Examples {

    @PostConstruct
    public void init() throws InterruptedException {
//        //Flux<Long> flux = Flux.interval(Duration.ofSeconds(1)).cache();
//        //Flux<Long> flux = Flux.interval(Duration.ofSeconds(1)).share();
//        Flux<Long> flux = Flux.interval(Duration.ofSeconds(1)).publish().autoConnect();
//
//        Thread thread = new Thread(() -> {
//            //flux.subscribe(System.out::println);
//        });
//        thread.start();
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }
//        flux.subscribe(System.out::println);


        ConnectableFlux<String> flux = Flux.interval(Duration.ofSeconds(1))
                .map(String::valueOf)
                .publish();

        flux.connect();

        Thread.sleep(5000);

        flux.subscribe(System.out::println);

    }
}
