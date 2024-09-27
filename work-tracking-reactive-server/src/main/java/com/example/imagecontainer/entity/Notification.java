package com.example.imagecontainer.entity;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class Notification {
    private String type;
    private Employee employee;
    private String message;
}