package com.example.demo.service;


import org.springframework.stereotype.Service;

@Service
public class SmsNotificationService implements NotificationService {
    
    @Override
    public String sendNotification(String message) {
        return "SMS 已發送: " + message;
    }
}
