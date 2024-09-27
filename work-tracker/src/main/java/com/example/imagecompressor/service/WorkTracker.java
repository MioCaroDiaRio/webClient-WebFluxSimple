package com.example.imagecompressor.service;

import com.example.imagecompressor.entity.Employee;
import com.example.imagecompressor.entity.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class WorkTracker {
    private long startTime;
    private long endTime;
    private boolean isWorking;
    private Employee employee;
    private long breakStartTime;
    private boolean isOnBreak;

    @Autowired
    private NotificationService notificationService;

    public void startWork(Employee employee) {
        this.employee = employee;
        startTime = System.currentTimeMillis();
        isWorking = true;
        Notification notification = new Notification();
        notification.setType("start-work");
        notification.setEmployee(employee);
        notification.setMessage("Сотрудник начал работу");
        notificationService.sendNotification(notification);
        startMonitoring();
    }

    public void endWork(Employee employee) {
        endTime = System.currentTimeMillis();
        isWorking = false;
        Notification notification = new Notification();
        notification.setType("end-work");
        notification.setEmployee(employee);
        notification.setMessage("Сотрудник закончил работу");
        notificationService.sendNotification(notification);
    }

    public void startBreak(Employee employee) {
        System.out.println(employee);
        breakStartTime = System.currentTimeMillis();
        isOnBreak = true;
        Notification notification = new Notification();
        notification.setType("start-break");
        notification.setEmployee(employee);
        notification.setMessage("Сотрудник ушел на перерыв");
        notificationService.sendNotification(notification);
    }

    public void endBreak(Employee employee) {
        isOnBreak = false;
        Notification notification = new Notification();
        notification.setType("end-break");
        notification.setEmployee(employee);
        notification.setMessage("Сотрудник закончил перерыв");
        notificationService.sendNotification(notification);
    }

    private void startMonitoring() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this::checkWorkTime, 1, 1, TimeUnit.MINUTES);
    }

    private void checkWorkTime() {
        if (isWorking) {
            long currentTime = System.currentTimeMillis();
            long workTime = currentTime - startTime;
            if (workTime > 8 * 60 * 60 * 1000) {
                Notification notification = new Notification();
                notification.setType("overwork");
                notification.setEmployee(employee);
                notification.setMessage("Сотрудник перерабатывает, остановите его");
                notificationService.sendNotification(notification);
            }
        }
        if (isOnBreak) {
            long currentTime = System.currentTimeMillis();
            long breakTime = currentTime - breakStartTime;
            if (breakTime > 60 * 60 * 1000) {
                Notification notification = new Notification();
                notification.setType("late-from-break");
                notification.setEmployee(employee);
                notification.setMessage("Сотрудник задерживается на перерыве");
                notificationService.sendNotification(notification);
            }
        }
    }
}