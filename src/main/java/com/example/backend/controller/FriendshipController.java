package com.example.backend.controller;

import com.example.backend.dto.FriendshipDTO;
import com.example.backend.model.*;
import com.example.backend.repository.FriendshipRepository;
import com.example.backend.repository.NotificationRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Optional;

@RestController
@RequestMapping("/friendships")
@CrossOrigin(origins = "http://localhost:5173")
public class FriendshipController {

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    // Gửi lời mời kết bạn
    @GetMapping("/between")
    public ResponseEntity<?> getFriendshipBetweenUsers(
            @RequestParam Integer userId1,
            @RequestParam Integer userId2) {
        Optional<Friendship> friendship = friendshipRepository.findByUserIds(userId1, userId2);
        return friendship.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createFriendship(@RequestBody FriendshipDTO request) {
        User user1 = userRepository.findById(request.getUser1().getId())
                .orElseThrow(() -> new IllegalArgumentException("User1 not found"));
        User user2 = userRepository.findById(request.getUser2().getId())
                .orElseThrow(() -> new IllegalArgumentException("User2 not found"));

        // Kiểm tra trạng thái user
        if (user1.getStatus() != UserStatus.ACTIVE || user2.getStatus() != UserStatus.ACTIVE) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Cannot create friendship with banned user");
        }

        // Kiểm tra trùng lặp quan hệ bạn bè
        List<Friendship> existingFriendships = friendshipRepository
                .findByUser1_IdOrUser2_Id(user1.getId(), user2.getId());
        if (existingFriendships.stream().anyMatch(f ->
                (f.getUser1().getId().equals(user1.getId()) && f.getUser2().getId().equals(user2.getId())) ||
                        (f.getUser1().getId().equals(user2.getId()) && f.getUser2().getId().equals(user1.getId())))) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Friendship already exists");
        }

        Friendship friendship = new Friendship();
        friendship.setUser1(user1);
        friendship.setUser2(user2);
        friendship.setStatus(FriendshipStatus.PENDING);
        Friendship savedFriendship = friendshipRepository.save(friendship);

        // Tạo thông báo cho user2
        Notification notification = new Notification();
        notification.setUser(user2);
        notification.setMessage(user1.getUsername() + " sent you a friend request");
        notification.setStatus(NotificationStatus.UNREAD);
        notification.setType(NotificationType.FRIEND_REQUEST);
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);

        return ResponseEntity.ok(new FriendshipDTO(
                savedFriendship.getId(),
                savedFriendship.getCreatedAt(),
                savedFriendship.getStatus(),
                savedFriendship.getUser1(),
                savedFriendship.getUser2(),
                getMutualFriends(user1.getId(), user2.getId())
        ));
    }

    // Chấp nhận lời mời kết bạn
    @PutMapping("/{id}/accept")
    public ResponseEntity<?> acceptFriendship(@PathVariable Integer id) {
        Friendship friendship = friendshipRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Friendship not found"));

        // Kiểm tra trạng thái user
        if (friendship.getUser1().getStatus() != UserStatus.ACTIVE ||
                friendship.getUser2().getStatus() != UserStatus.ACTIVE) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Cannot accept friendship with banned user");
        }

        // Kiểm tra trạng thái PENDING
        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Friendship is not pending");
        }

        friendship.setStatus(FriendshipStatus.ACCEPTED);
        Friendship updatedFriendship = friendshipRepository.save(friendship);

        // Tạo thông báo cho user1
        Notification notification = new Notification();
        notification.setUser(friendship.getUser1());
        notification.setMessage(friendship.getUser2().getUsername() + " accepted your friend request");
        notification.setStatus(NotificationStatus.UNREAD);
        notification.setType(NotificationType.FRIEND_REQUEST);
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);

        return ResponseEntity.ok(new FriendshipDTO(
                updatedFriendship.getId(),
                updatedFriendship.getCreatedAt(),
                updatedFriendship.getStatus(),
                updatedFriendship.getUser1(),
                updatedFriendship.getUser2(),
                getMutualFriends(updatedFriendship.getUser1().getId(), updatedFriendship.getUser2().getId())
        ));
    }

    // Từ chối/Xóa lời mời kết bạn hoặc hủy kết bạn
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFriendship(@PathVariable Integer id) {
        Friendship friendship = friendshipRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Friendship not found"));

        friendshipRepository.delete(friendship);
        return ResponseEntity.noContent().build();
    }

    // Chặn user
    @PutMapping("/{id}/block")
    public ResponseEntity<?> blockFriendship(@PathVariable Integer id) {
        Friendship friendship = friendshipRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Friendship not found"));

        // Kiểm tra trạng thái user
        if (friendship.getUser1().getStatus() != UserStatus.ACTIVE ||
                friendship.getUser2().getStatus() != UserStatus.ACTIVE) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Cannot block banned user");
        }

        friendship.setStatus(FriendshipStatus.BLOCKED);
        Friendship updatedFriendship = friendshipRepository.save(friendship);

        return ResponseEntity.ok(new FriendshipDTO(
                updatedFriendship.getId(),
                updatedFriendship.getCreatedAt(),
                updatedFriendship.getStatus(),
                updatedFriendship.getUser1(),
                updatedFriendship.getUser2(),
                getMutualFriends(updatedFriendship.getUser1().getId(), updatedFriendship.getUser2().getId())
        ));
    }

    // Lấy danh sách bạn bè hoặc lời mời theo trạng thái và user
    @GetMapping("/status/{status}")
    public List<FriendshipDTO> getFriendshipsByStatus(@PathVariable FriendshipStatus status,
                                                     @RequestParam Integer userId) {
        List<Friendship> friendships = friendshipRepository.findByUser1_IdOrUser2_Id(userId, userId);
        return friendships.stream()
                .filter(f -> f.getStatus() == status &&
                        f.getUser1().getStatus() == UserStatus.ACTIVE &&
                        f.getUser2().getStatus() == UserStatus.ACTIVE &&
                        (status != FriendshipStatus.PENDING || f.getUser2().getId().equals(userId))) // Chỉ lấy lời mời nhận được
                .map(friendship -> new FriendshipDTO(
                        friendship.getId(),
                        friendship.getCreatedAt(),
                        friendship.getStatus(),
                        friendship.getUser1(),
                        friendship.getUser2(),
                        getMutualFriends(friendship.getUser1().getId(), friendship.getUser2().getId())
                ))
                .collect(Collectors.toList());
    }

    // Lấy danh sách bạn bè hoặc lời mời của một user
    @GetMapping("/user/{userId}")
    public List<FriendshipDTO> getFriendshipsByUser(@PathVariable Integer userId) {
        List<Friendship> friendships = friendshipRepository.findByUser1_IdOrUser2_Id(userId, userId);
        return friendships.stream()
                .filter(f -> f.getUser1().getStatus() == UserStatus.ACTIVE &&
                        f.getUser2().getStatus() == UserStatus.ACTIVE)
                .map(friendship -> new FriendshipDTO(
                        friendship.getId(),
                        friendship.getCreatedAt(),
                        friendship.getStatus(),
                        friendship.getUser1(),
                        friendship.getUser2(),
                        getMutualFriends(friendship.getUser1().getId(), friendship.getUser2().getId())
                ))
                .collect(Collectors.toList());
    }

    // Lấy danh sách gợi ý kết bạn
    @GetMapping("/suggestions/{userId}")
    public List<User> getFriendSuggestions(@PathVariable Integer userId) {
        List<Friendship> friendships = friendshipRepository.findByUser1_IdOrUser2_Id(userId, userId);
        List<Integer> relatedUserIds = friendships.stream()
                .flatMap(f -> List.of(f.getUser1().getId(), f.getUser2().getId()).stream())
                .distinct()
                .collect(Collectors.toList());

        return userRepository.findAll().stream()
                .filter(user -> !relatedUserIds.contains(user.getId()) &&
                        !user.getId().equals(userId) &&
                        user.getStatus() == UserStatus.ACTIVE)
                .collect(Collectors.toList());
    }

    // Tính số bạn chung
    private long getMutualFriends(Integer userId1, Integer userId2) {
        List<Friendship> user1Friends = friendshipRepository.findByUser1_IdOrUser2_Id(userId1, userId1)
                .stream()
                .filter(f -> f.getStatus() == FriendshipStatus.ACCEPTED &&
                        f.getUser1().getStatus() == UserStatus.ACTIVE &&
                        f.getUser2().getStatus() == UserStatus.ACTIVE)
                .collect(Collectors.toList());
        List<Friendship> user2Friends = friendshipRepository.findByUser1_IdOrUser2_Id(userId2, userId2)
                .stream()
                .filter(f -> f.getStatus() == FriendshipStatus.ACCEPTED &&
                        f.getUser1().getStatus() == UserStatus.ACTIVE &&
                        f.getUser2().getStatus() == UserStatus.ACTIVE)
                .collect(Collectors.toList());

        Set<Integer> user1FriendIds = user1Friends.stream()
                .flatMap(f -> Stream.of(f.getUser1().getId(), f.getUser2().getId()))
                .filter(id -> !id.equals(userId1))
                .collect(Collectors.toSet());
        Set<Integer> user2FriendIds = user2Friends.stream()
                .flatMap(f -> Stream.of(f.getUser1().getId(), f.getUser2().getId()))
                .filter(id -> !id.equals(userId2))
                .collect(Collectors.toSet());

        user1FriendIds.retainAll(user2FriendIds);
        return user1FriendIds.size();
    }
}