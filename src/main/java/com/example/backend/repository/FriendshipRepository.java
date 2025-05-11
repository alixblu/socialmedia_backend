package com.example.backend.repository;

import com.example.backend.model.Friendship;
import com.example.backend.model.FriendshipStatus;
import com.example.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Integer> {

    // Find friendships by user ID
    // List<Friendship> findByUserId1OrUserId2(Integer userId1, Integer userId2);

    // Find friendships by status
    List<Friendship> findByStatus(String status);
    
    // Check if a friendship exists with given ID and status
    boolean existsByIdAndStatus(Integer id, FriendshipStatus status);
    
    // Find friendship by two user IDs (regardless of order)
    @Query("SELECT f FROM Friendship f WHERE (f.user1.id = :userId1 AND f.user2.id = :userId2) OR (f.user1.id = :userId2 AND f.user2.id = :userId1)")
    Optional<Friendship> findByUserIds(@Param("userId1") Integer userId1, @Param("userId2") Integer userId2);
}