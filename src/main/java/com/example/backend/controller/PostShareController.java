package com.example.backend.controller;

import com.example.backend.model.PostShare;
import com.example.backend.repository.PostShareRepository;
import com.example.backend.repository.PostRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shares")
@CrossOrigin(origins = "http://localhost:5173")
public class PostShareController {

    @Autowired
    private PostShareRepository postShareRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/post/{postId}")
    public List<PostShare> getSharesByPost(@PathVariable Integer postId) {
        return postShareRepository.findByPostId(postId);
    }

    @GetMapping("/user/{userId}")
    public List<PostShare> getSharesByUser(@PathVariable Integer userId) {
        return postShareRepository.findByUserId(userId);
    }

    @PostMapping
    public ResponseEntity<?> createShare(@RequestBody PostShare share) {
        // Check if post exists
        if (!postRepository.existsById(share.getPost().getId())) {
            return ResponseEntity.badRequest().body("Post not found");
        }

        // Check if user exists
        if (!userRepository.existsById(share.getUser().getId())) {
            return ResponseEntity.badRequest().body("User not found");
        }

        // Check if user has already shared this post
        if (postShareRepository.existsByPostIdAndUserId(
                share.getPost().getId(), 
                share.getUser().getId())) {
            return ResponseEntity.badRequest().body("User has already shared this post");
        }

        PostShare savedShare = postShareRepository.save(share);
        return ResponseEntity.ok(savedShare);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteShare(@PathVariable Integer id) {
        try {
            postShareRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting share: " + e.getMessage());
        }
    }
} 