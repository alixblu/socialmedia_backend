package com.example.backend.repository;

import com.example.backend.model.Comment;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    // Find comments for a specific post
    List<Comment> findByPostId(Integer postId);

    @Transactional
    void deleteByPostId(Integer postId);

    // Find comments by user ID
    List<Comment> findByUserId(Integer userId);
}