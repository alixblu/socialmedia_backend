package com.example.backend.controller;

import com.example.backend.model.AiBot;
import com.example.backend.model.AiChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiChatController {



    @Autowired
    private RestTemplate restTemplate;
    
    private final String statsBaseUrl = "http://localhost:8080/api/stats";

    @PostMapping("/chat")
    public ResponseEntity<AiChatMessage> handleChat(@RequestBody AiChatMessage message) {
        // Save the incoming message
        message.setTimestamp(java.time.LocalDateTime.now());

        // Process the message and determine intent
        String intent = determineIntent(message.getMessage());
        message.setIntent(intent);
        
        // Get response based on intent
        String response = generateResponse(message);
        message.setResponse(response);
        
        // Save the response

        return ResponseEntity.ok(message);
    }
    
    private String determineIntent(String message) {
        // Simple intent detection based on keywords
        message = message.toLowerCase();
        
        if (message.contains("how many posts") || message.contains("post count")) {
            return "post_count";
        } else if (message.contains("last post") || message.contains("when did i post")) {
            return "last_post";
        } else if (message.contains("last week") || message.contains("recent posts")) {
            return "last_week_posts";
        } else if (message.contains("top posts") || message.contains("best posts")) {
            return "top_posts";
        } else if (message.contains("this month") || message.contains("monthly posts")) {
            return "monthly_posts";
        } else if (message.contains("most active") || message.contains("active month")) {
            return "most_active_month";
        } else if (message.contains("likes received") || message.contains("total likes")) {
            return "likes_received";
        } else if (message.contains("most liked") || message.contains("best liked")) {
            return "most_liked_post";
        } else if (message.contains("comments written") || message.contains("total comments")) {
            return "comments_written";
        } else if (message.contains("most comments") || message.contains("commented post")) {
            return "most_commented_post";
        } else if (message.contains("how many friends") || message.contains("friend count")) {
            return "friend_count";
        } else if (message.contains("recent friends") || message.contains("new friends")) {
            return "recent_friends";
        } else if (message.contains("my bio") || message.contains("about me")) {
            return "user_bio";
        } else if (message.contains("when did i join") || message.contains("join date")) {
            return "join_date";
        }
        
        return "unknown";
    }
    
    private String generateResponse(AiChatMessage message) {
        String userId = message.getUserId();
        String intent = message.getIntent();
        
        try {
            switch (intent) {
                case "post_count":
                    Long postCount = restTemplate.getForObject(statsBaseUrl + "/user/" + userId + "/post-count", Long.class);
                    return "You have made " + postCount + " posts.";
                    
                case "last_post":
                    Map<String, Object> lastPost = restTemplate.getForObject(statsBaseUrl + "/user/" + userId + "/last-post", Map.class);
                    return "Your last post was: " + lastPost.get("content");
                    
                case "last_week_posts":
                    Object[] lastWeekPosts = restTemplate.getForObject(statsBaseUrl + "/user/" + userId + "/last-week-posts", Object[].class);
                    return "You made " + lastWeekPosts.length + " posts last week.";
                    
                case "top_posts":
                    Object[] topPosts = restTemplate.getForObject(statsBaseUrl + "/user/" + userId + "/top-posts", Object[].class);
                    return "Here are your top " + Math.min(3, topPosts.length) + " posts.";
                    
                case "monthly_posts":
                    Object[] monthlyPosts = restTemplate.getForObject(statsBaseUrl + "/user/" + userId + "/monthly-posts", Object[].class);
                    return "You have made " + monthlyPosts.length + " posts this month.";
                    
                case "most_active_month":
                    Map<String, Object> activeMonth = restTemplate.getForObject(statsBaseUrl + "/user/" + userId + "/most-active-month", Map.class);
                    return "Your most active posting month was " + getMonthName((Integer)activeMonth.get("month")) + 
                           " with " + activeMonth.get("count") + " posts!";
                    
                case "likes_received":
                    Long likesCount = restTemplate.getForObject(statsBaseUrl + "/user/" + userId + "/likes-received", Long.class);
                    return "You have received " + likesCount + " likes in total.";
                    
                case "most_liked_post":
                    Map<String, Object> mostLiked = restTemplate.getForObject(statsBaseUrl + "/user/" + userId + "/most-liked-post", Map.class);
                    return "Your most liked post has " + mostLiked.get("likes") + " likes.";
                    
                case "comments_written":
                    Long commentsCount = restTemplate.getForObject(statsBaseUrl + "/user/" + userId + "/comments-written", Long.class);
                    return "You have written " + commentsCount + " comments.";
                    
                case "most_commented_post":
                    Map<String, Object> mostCommented = restTemplate.getForObject(statsBaseUrl + "/user/" + userId + "/most-commented-post", Map.class);
                    return "Your post with the most comments has " + mostCommented.get("comments") + " comments.";
                    
                case "friend_count":
                    Long friendCount = restTemplate.getForObject(statsBaseUrl + "/user/" + userId + "/friend-count", Long.class);
                    return "You have " + friendCount + " friends.";
                    
                case "recent_friends":
                    Object[] recentFriends = restTemplate.getForObject(statsBaseUrl + "/user/" + userId + "/recent-friends", Object[].class);
                    return "You have " + recentFriends.length + " recent friend connections.";
                    
                case "user_bio":
                    Map<String, Object> profile = restTemplate.getForObject(statsBaseUrl + "/user/" + userId + "/profile", Map.class);
                    return "Your bio: " + profile.get("bio");
                    
                case "join_date":
                    profile = restTemplate.getForObject(statsBaseUrl + "/user/" + userId + "/profile", Map.class);
                    return "You joined on " + profile.get("joinDate");
                    
                default:
                    return "I'm not sure how to help with that. Try asking about your posts, likes, comments, or friends.";
            }
        } catch (Exception e) {
            return "I'm having trouble accessing that information right now. Please try again later.";
        }
    }
    
    private String getMonthName(int month) {
        String[] months = {"January", "February", "March", "April", "May", "June", 
                          "July", "August", "September", "October", "November", "December"};
        return months[month - 1];
    }
} 