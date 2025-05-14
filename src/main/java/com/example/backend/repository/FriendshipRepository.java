package com.example.backend.repository;

import com.example.backend.model.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.backend.model.FriendshipStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Integer> {

    // Tìm tình bạn theo user ID1 hoặc user ID2
    List<Friendship> findByUser1_IdOrUser2_Id(Integer userId1, Integer userId2);


    // Find friendships by status
    List<Friendship> findByStatus(String status);

    // Tìm tình bạn theo trạng thái (FriendshipStatus)
    List<Friendship> findByStatus(FriendshipStatus status);
    // Check if a friendship exists with given ID and status
    boolean existsByIdAndStatus(Integer id, FriendshipStatus status);

    // Find friendship by two user IDs (regardless of order)
    @Query("SELECT f FROM Friendship f WHERE (f.user1.id = :userId1 AND f.user2.id = :userId2) OR (f.user1.id = :userId2 AND f.user2.id = :userId1)")
    Optional<Friendship> findByUserIds(@Param("userId1") Integer userId1, @Param("userId2") Integer userId2);

    @Query("SELECT COUNT(f) FROM Friendship f WHERE (f.user1.id = :userId OR f.user2.id = :userId) AND f.status = 'ACCEPTED'")
    Long countFriendsByUserId(Integer userId);
    
    @Query("SELECT f FROM Friendship f WHERE (f.user1.id = :userId OR f.user2.id = :userId) AND f.status = 'ACCEPTED' ORDER BY f.createdAt DESC")
    List<Friendship> findRecentFriendships(Integer userId);
}