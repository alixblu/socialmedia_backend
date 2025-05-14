package com.example.backend.repository;

import com.example.backend.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    // Find posts by user ID
    List<Post> findByUserId(Integer userId);

    // Search posts by content (case insensitive)
    List<Post> findByContentContainingIgnoreCase(String keyword);

    List<Post> findAllByOrderByCreatedAtDesc();
    
    @Query("SELECT COUNT(p) FROM Post p WHERE p.user.id = :userId")
    Long countPostsByUserId(Integer userId);
    
    @Query("SELECT p FROM Post p WHERE p.user.id = :userId ORDER BY p.createdAt DESC")
    List<Post> findLatestPostByUserId(Integer userId);
    
    @Query("SELECT p FROM Post p WHERE p.user.id = :userId AND p.createdAt >= :startDate AND p.createdAt <= :endDate")
    List<Post> findPostsByUserIdAndDateRange(Integer userId, LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT p FROM Post p WHERE p.user.id = :userId ORDER BY (SELECT COUNT(pl) FROM PostLike pl WHERE pl.post = p) DESC")
    List<Post> findTopPostsByUserId(Integer userId);
    
    @Query("SELECT p FROM Post p WHERE p.user.id = :userId ORDER BY (SELECT COUNT(c) FROM Comment c WHERE c.post = p) DESC")
    List<Post> findMostCommentedPostByUserId(Integer userId);
    
    @Query("SELECT p FROM Post p WHERE p.user.id = :userId AND p.createdAt >= :startDate")
    List<Post> findPostsThisMonth(Integer userId, LocalDateTime startDate);
    
    @Query("SELECT FUNCTION('MONTH', p.createdAt) as month, COUNT(p) as count FROM Post p WHERE p.user.id = :userId GROUP BY FUNCTION('MONTH', p.createdAt) ORDER BY count DESC")
    List<Object[]> findMostActiveMonth(Integer userId);
}