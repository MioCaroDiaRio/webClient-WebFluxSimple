package com.example.imagecompressor.controller;

import com.example.imagecompressor.entity.Employee;
import com.example.imagecompressor.service.WorkTracker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/work")
public class WorkController {
    @Autowired
    private WorkTracker workTracker;

    @PostMapping("/start")
    public String startWork(@RequestBody Employee employee) {
        workTracker.startWork(employee);
        System.out.println(employee);
        return "Work started";
    }

    @PostMapping("/end")
    public String endWork(@RequestBody Employee employee) {
        workTracker.endWork(employee);
        return "Work ended";
    }

    @PostMapping("/start-break")
    public String startBreak(@RequestBody Employee employee) {
        workTracker.startBreak(employee);
        return "Break started";
    }

    @PostMapping("/end-break")
    public String endBreak(@RequestBody Employee employee) {
        workTracker.endBreak(employee);
        return "Break ended";
    }
}