package com.example.backend.repository;

import com.example.backend.model.Friendship;
import com.example.backend.model.FriendshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Integer> {

    // Tìm tình bạn theo user ID1 hoặc user ID2
    List<Friendship> findByUser1_IdOrUser2_Id(Integer userId1, Integer userId2);

    // Tìm tình bạn theo trạng thái (FriendshipStatus)
    List<Friendship> findByStatus(FriendshipStatus status);
}