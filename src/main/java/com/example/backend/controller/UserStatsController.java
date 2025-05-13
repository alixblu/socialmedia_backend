package com.example.backend.controller;

import com.example.backend.model.*;
import com.example.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
public class UserStatsController {

    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private PostLikeRepository postLikeRepository;
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private FriendshipRepository friendshipRepository;
    
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/user/{userId}/post-count")
    public ResponseEntity<Long> getPostCount(@PathVariable Integer userId) {
        return ResponseEntity.ok(postRepository.countPostsByUserId(userId));
    }

    @GetMapping("/user/{userId}/likes-received")
    public ResponseEntity<Long> getLikesReceived(@PathVariable Integer userId) {
        return ResponseEntity.ok(postLikeRepository.countTotalLikesReceived(userId));
    }

    @GetMapping("/user/{userId}/comments-written")
    public ResponseEntity<Long> getCommentsWritten(@PathVariable Integer userId) {
        return ResponseEntity.ok(commentRepository.countCommentsByUserId(userId));
    }

    @GetMapping("/user/{userId}/last-post")
    public ResponseEntity<Post> getLastPost(@PathVariable Integer userId) {
        List<Post> posts = postRepository.findLatestPostByUserId(userId);
        return posts.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(posts.get(0));
    }

    @GetMapping("/user/{userId}/most-liked-post")
    public ResponseEntity<Post> getMostLikedPost(@PathVariable Integer userId) {
        List<Post> posts = postLikeRepository.findMostLikedPost(userId);
        return posts.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(posts.get(0));
    }

    @GetMapping("/user/{userId}/friend-count")
    public ResponseEntity<Long> getFriendCount(@PathVariable Integer userId) {
        return ResponseEntity.ok(friendshipRepository.countFriendsByUserId(userId));
    }

    @GetMapping("/user/{userId}/recent-friends")
    public ResponseEntity<List<Friendship>> getRecentFriends(@PathVariable Integer userId) {
        return ResponseEntity.ok(friendshipRepository.findRecentFriendships(userId));
    }

    @GetMapping("/user/{userId}/last-week-posts")
    public ResponseEntity<List<Post>> getLastWeekPosts(@PathVariable Integer userId) {
        LocalDateTime weekAgo = LocalDateTime.now().minusWeeks(1);
        return ResponseEntity.ok(postRepository.findPostsByUserIdAndDateRange(userId, weekAgo, LocalDateTime.now()));
    }

    @GetMapping("/user/{userId}/top-posts")
    public ResponseEntity<List<Post>> getTopPosts(@PathVariable Integer userId) {
        return ResponseEntity.ok(postRepository.findTopPostsByUserId(userId));
    }

    @GetMapping("/user/{userId}/monthly-posts")
    public ResponseEntity<List<Post>> getMonthlyPosts(@PathVariable Integer userId) {
        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        return ResponseEntity.ok(postRepository.findPostsThisMonth(userId, monthStart));
    }

    @GetMapping("/user/{userId}/most-commented-post")
    public ResponseEntity<Post> getMostCommentedPost(@PathVariable Integer userId) {
        List<Post> posts = postRepository.findMostCommentedPostByUserId(userId);
        return posts.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(posts.get(0));
    }

    @GetMapping("/user/{userId}/profile")
    public ResponseEntity<Map<String, Object>> getUserProfile(@PathVariable Integer userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> profile = new HashMap<>();
        profile.put("bio", user.getBio());
        profile.put("joinDate", user.getCreatedAt());
        
        return ResponseEntity.ok(profile);
    }
    
    @GetMapping("/user/{userId}/most-active-month")
    public ResponseEntity<Map<String, Object>> getMostActiveMonth(@PathVariable Integer userId) {
        List<Object[]> results = postRepository.findMostActiveMonth(userId);
        if (results.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Object[] mostActive = results.get(0);
        Integer month = (Integer) mostActive[0];
        Long count = (Long) mostActive[1];
        
        Map<String, Object> response = new HashMap<>();
        response.put("month", month);
        response.put("count", count);
        
        return ResponseEntity.ok(response);
    }
} 