package com.example.backend.repository;

import com.example.backend.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    // Find posts by user ID
    List<Post> findByUserId(Integer userId);

    // Search posts by content (case insensitive)
    List<Post> findByContentContainingIgnoreCase(String keyword);

    List<Post> findAllByOrderByCreatedAtDesc();
    


}