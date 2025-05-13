package com.example.backend.repository;

import com.example.backend.model.Post;
import com.example.backend.model.PostLike;
import com.example.backend.model.User;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Integer> {

    // Find likes for a specific post
    List<PostLike> findByPostId(Integer postId);

    // Find likes by user ID
    List<PostLike> findByUserId(Integer userId);

    Optional<PostLike> findByPostAndUser(Post post, User user);

    @Transactional
    void deleteByPostId(Integer postId);

}