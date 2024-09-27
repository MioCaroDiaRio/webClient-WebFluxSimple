package com.example.mailservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class ChiefListener {

    private final JavaMailSender javaMailSender;
    private final ObjectMapper objectMapper;

    public ChiefListener(JavaMailSender javaMailSender, ObjectMapper objectMapper) {
        this.javaMailSender = javaMailSender;
        this.objectMapper = objectMapper;
    }


    @KafkaListener(topics = "chief-topic")
    public void receiveEmail(String message) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(message);
        SimpleMailMessage email = objectMapper.treeToValue(jsonNode, SimpleMailMessage.class);

        try {
            email.setTo("dmitrykron36@gmail.com");
            javaMailSender.send(email);
            System.out.println("отправлено");
        } catch (MailException e) {
            System.out.println("не отправлено: " + e.getMessage());
        }
    }
}