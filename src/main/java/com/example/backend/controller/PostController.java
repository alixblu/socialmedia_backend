package com.example.backend.controller;

import com.example.backend.model.Post;
import com.example.backend.model.User;
import com.example.backend.repository.PostRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/posts")
@CrossOrigin(origins = "http://localhost:5173")
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private S3Service s3Service;

    @PostMapping
    public ResponseEntity<?> createPost(
            @RequestParam("content") String content,
            @RequestParam(value = "media", required = false) List<MultipartFile> mediaFiles,
            @RequestParam("userId") Integer userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Post post = new Post();
            post.setContent(content);
            post.setUser(user);

            if (mediaFiles != null && !mediaFiles.isEmpty()) {
                List<String> mediaUrls = new ArrayList<>();
                for (MultipartFile file : mediaFiles) {
                    if (!file.isEmpty()) {
                        String mediaUrl = s3Service.uploadFile(file);
                        mediaUrls.add(mediaUrl);
                    }
                }
                post.setMediaUrls(mediaUrls);
            }

            Post savedPost = postRepository.save(post);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Đăng bài thành công!");
            response.put("data", savedPost);
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error uploading media: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating post: " + e.getMessage());
        }
    }

    @GetMapping
    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable Integer id) {
        return postRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public List<Post> getPostsByUser(@PathVariable Integer userId) {
        return postRepository.findByUserId(userId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(
            @PathVariable Integer id,
            @RequestParam("content") String content,
            @RequestParam(value = "media", required = false) List<MultipartFile> mediaFiles) {
        try {
            Post post = postRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Post not found"));

            post.setContent(content);

            if (mediaFiles != null && !mediaFiles.isEmpty()) {
                List<String> mediaUrls = new ArrayList<>();
                for (MultipartFile file : mediaFiles) {
                    if (!file.isEmpty()) {
                        String mediaUrl = s3Service.uploadFile(file);
                        mediaUrls.add(mediaUrl);
                    }
                }
                post.setMediaUrls(mediaUrls);
            }

            Post updatedPost = postRepository.save(post);
            return ResponseEntity.ok(updatedPost);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error uploading media: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating post: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Integer id) {
        try {
            postRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting post: " + e.getMessage());
        }
    }
}