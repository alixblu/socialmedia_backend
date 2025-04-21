package com.example.backend.repository;

import com.example.backend.model.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Integer> {

    // Find friendships by user ID
    List<Friendship> findByUserId1OrUserId2(Integer userId1, Integer userId2);

    // Find friendships by status
    List<Friendship> findByStatus(String status);
}