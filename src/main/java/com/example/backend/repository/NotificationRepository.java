package com.example.backend.repository;

import com.example.backend.model.Notification;
import com.example.backend.model.NotificationType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    // Find all notifications for a user
    List<Notification> findByUserId(Integer userId);

    // Find all unread notifications
    List<Notification> findByStatus(String status);

    boolean existsByUserIdAndPostIdAndType(Integer userId, Integer postId, NotificationType type);

}