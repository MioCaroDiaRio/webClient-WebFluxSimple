package com.example.imagecompressor.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Notification {
    private String type;
    private Employee employee;
    private String message;
}