package com.example.mailservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class ManagerListener {

    @Autowired
    private JavaMailSender javaMailSender;

    @KafkaListener(topics = "manager-topic")
    public void receiveEmail(SimpleMailMessage email) {
        try {
            javaMailSender.send(email);
            System.out.println("отправлено");
        } catch (MailException e) {
            System.out.println("не отправлено: " + e.getMessage());
        }
    }
}