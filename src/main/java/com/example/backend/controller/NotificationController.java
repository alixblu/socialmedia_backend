package com.example.backend.controller;

import com.example.backend.model.Notification;
import com.example.backend.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @GetMapping
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    @GetMapping("/{userId}")
    public List<Notification> getNotificationsByUser(@PathVariable Integer userId) {
        return notificationRepository.findByUserId(userId);
    }

    @PostMapping
    public Notification createNotification(@RequestBody Notification notification) {
        return notificationRepository.save(notification);
    }

    @DeleteMapping("/{id}")
    public void deleteNotification(@PathVariable Integer id) {
        notificationRepository.deleteById(id);
    }
}