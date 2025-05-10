package com.example.backend.repository;

import com.example.backend.model.PostShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostShareRepository extends JpaRepository<PostShare, Integer> {

    // Find shares for a specific post
    List<PostShare> findByPostId(Integer postId);

    // Find shares by user ID
    List<PostShare> findByUserId(Integer userId);

    // Check if a user has already shared a post
    boolean existsByPostIdAndUserId(Integer postId, Integer userId);
} 