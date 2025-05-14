package com.example.backend.controller;

import com.example.backend.model.Notification;
import com.example.backend.model.NotificationStatus;
import com.example.backend.model.NotificationType;
import com.example.backend.model.Post;
import com.example.backend.model.User;
import com.example.backend.repository.NotificationRepository;
import com.example.backend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/notifications")
@CrossOrigin(origins = "http://localhost:5173")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }



    @PostMapping("/post-action")
    public ResponseEntity<Notification> createNotificationForPostAction(
            @RequestParam("userId") Integer userId,             // người nhận thông báo
            @RequestParam("postId") Integer postId,
            @RequestParam("message") String message,            // nội dung cơ bản của thông báo
            @RequestParam("type") String type,
            @RequestParam("currentUserId") Integer currentUserId // người gửi thông báo (ví dụ: người bày tỏ cảm xúc)
    ) {
        NotificationType notificationType;
        try {
            notificationType = NotificationType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        boolean exists = notificationRepository.existsByUserIdAndPostIdAndType(userId, postId, notificationType);
        if (exists) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        // 🔍 Lấy tên người gửi (currentUserId)
        String senderName = userRepository.findById(currentUserId)
                .map(User::getUsername)
                .orElse("Người dùng");

        Notification notification = new Notification();
        User user = new User();
        user.setId(userId); // người nhận
        Post post = new Post();
        post.setId(postId);

        notification.setUser(user);
        notification.setPost(post);
        notification.setMessage(senderName + ": " + message); // gắn tên người gửi vào đầu thông báo
        notification.setStatus(NotificationStatus.UNREAD);
        notification.setType(notificationType);
        notification.setCreatedAt(LocalDateTime.now());

        Notification saved = notificationRepository.save(notification);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }









    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsByUser(@PathVariable Integer userId) {
        List<Notification> notifications = notificationRepository.findByUserId(userId)
            .stream()
            .filter(n -> n.getStatus() != NotificationStatus.DISMISSED)
            .toList();

        if (notifications.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }


    @PutMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(@PathVariable Integer id) {
        return notificationRepository.findById(id)
            .map(notification -> {
                notification.setStatus(NotificationStatus.READ); 

                Notification updated = notificationRepository.save(notification);
                return new ResponseEntity<>(updated, HttpStatus.OK);
            })
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @PutMapping("/user/{userId}/markAllAsRead")
    public ResponseEntity<Void> markAllAsRead(@PathVariable Integer userId) {
        List<Notification> notifications = notificationRepository.findByUserId(userId);
        for (Notification notification : notifications) {
            notification.setStatus(NotificationStatus.READ); 
        }
        notificationRepository.saveAll(notifications);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PutMapping("/{id}/dismiss")
    public ResponseEntity<Notification> dismissNotification(@PathVariable Integer id) {
        return notificationRepository.findById(id)
            .map(notification -> {
                notification.setStatus(NotificationStatus.DISMISSED);
                Notification updated = notificationRepository.save(notification);
                return new ResponseEntity<>(updated, HttpStatus.OK);
            })
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
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