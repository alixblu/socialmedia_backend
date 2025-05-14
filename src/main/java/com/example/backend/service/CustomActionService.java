package com.example.backend.service;

import com.example.backend.model.*;
import com.example.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CustomActionService {
    private static final Logger logger = LoggerFactory.getLogger(CustomActionService.class);

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

    public String handlePostCount(Integer userId) {
        try {
            Long count = postRepository.countPostsByUserId(userId);
            return String.format("Rasa: You have %d posts in total.", count);
        } catch (Exception e) {
            logger.error("Error getting post count: {}", e.getMessage());
            return "Rasa: I couldn't retrieve your post count at the moment.";
        }
    }

    public String handleFriendCount(Integer userId) {
        try {
            Long count = friendshipRepository.countFriendsByUserId(userId);
            return String.format("Rasa: You have %d friends in your network.", count);
        } catch (Exception e) {
            logger.error("Error getting friend count: {}", e.getMessage());
            return "Rasa: I couldn't retrieve your friend count at the moment.";
        }
    }

    public String handleLikesReceived(Integer userId) {
        try {
            Long count = postLikeRepository.countTotalLikesReceived(userId);
            return String.format("Rasa: You have received %d likes in total.", count);
        } catch (Exception e) {
            logger.error("Error getting likes count: {}", e.getMessage());
            return "Rasa: I couldn't retrieve your likes count at the moment.";
        }
    }

    public String handleCommentsWritten(Integer userId) {
        try {
            Long count = commentRepository.countCommentsByUserId(userId);
            return String.format("Rasa: You have written %d comments in total.", count);
        } catch (Exception e) {
            logger.error("Error getting comments count: {}", e.getMessage());
            return "Rasa: I couldn't retrieve your comments count at the moment.";
        }
    }

    public String handleLastPost(Integer userId) {
        try {
            List<Post> posts = postRepository.findLatestPostByUserId(userId);
            if (posts.isEmpty()) {
                return "Rasa: You haven't made any posts yet.";
            }
            Post lastPost = posts.get(0);
            return String.format("Rasa: Your last post was: '%s' on %s", 
                lastPost.getContent(), 
                lastPost.getCreatedAt().toString());
        } catch (Exception e) {
            logger.error("Error getting last post: {}", e.getMessage());
            return "Rasa: I couldn't retrieve your last post at the moment.";
        }
    }

    public String handleMostLikedPost(Integer userId) {
        try {
            List<Post> posts = postLikeRepository.findMostLikedPost(userId);
            if (posts.isEmpty()) {
                return "Rasa: You haven't made any posts yet.";
            }
            Post mostLiked = posts.get(0);
            return String.format("Rasa: Your most liked post was: '%s' with %d likes", 
                mostLiked.getContent(), 
                postLikeRepository.countLikesByPostId(mostLiked.getId()));
        } catch (Exception e) {
            logger.error("Error getting most liked post: {}", e.getMessage());
            return "Rasa: I couldn't retrieve your most liked post at the moment.";
        }
    }

    public String handleMostActiveMonth(Integer userId) {
        try {
            List<Object[]> results = postRepository.findMostActiveMonth(userId);
            if (results.isEmpty()) {
                return "Rasa: You haven't made any posts yet.";
            }
            Object[] mostActive = results.get(0);
            Integer month = (Integer) mostActive[0];
            Long count = (Long) mostActive[1];
            return String.format("Rasa: Your most active month was %s with %d posts.", 
                getMonthName(month), count);
        } catch (Exception e) {
            logger.error("Error getting most active month: {}", e.getMessage());
            return "Rasa: I couldn't retrieve your most active month at the moment.";
        }
    }

    private String getMonthName(int month) {
        return switch (month) {
            case 1 -> "January";
            case 2 -> "February";
            case 3 -> "March";
            case 4 -> "April";
            case 5 -> "May";
            case 6 -> "June";
            case 7 -> "July";
            case 8 -> "August";
            case 9 -> "September";
            case 10 -> "October";
            case 11 -> "November";
            case 12 -> "December";
            default -> "Unknown";
        };
    }
} 