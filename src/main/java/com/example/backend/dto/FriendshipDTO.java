package com.example.backend.dto;

import com.example.backend.model.FriendshipStatus;
import com.example.backend.model.User;

import java.time.LocalDateTime;

public class FriendshipDTO {
    private Integer id;
    private LocalDateTime createdAt;
    private FriendshipStatus status;
    private User user1;
    private User user2;
    private long mutualFriends;

    // Constructor
    public FriendshipDTO(Integer id, LocalDateTime createdAt, FriendshipStatus status, User user1, User user2, long mutualFriends) {
        this.id = id;
        this.createdAt = createdAt;
        this.status = status;
        this.user1 = user1;
        this.user2 = user2;
        this.mutualFriends = mutualFriends;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public FriendshipStatus getStatus() {
        return status;
    }

    public void setStatus(FriendshipStatus status) {
        this.status = status;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    public long getMutualFriends() {
        return mutualFriends;
    }

    public void setMutualFriends(long mutualFriends) {
        this.mutualFriends = mutualFriends;
    }
}