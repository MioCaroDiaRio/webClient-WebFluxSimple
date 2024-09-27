package com.example.imagecontainer.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@Data
public class Employee {
    @Id
    private Long id;
    private String name;
    private String surname;
}