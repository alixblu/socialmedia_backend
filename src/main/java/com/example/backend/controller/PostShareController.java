package com.example.backend.controller;

import com.example.backend.model.Post;
import com.example.backend.model.PostShare;
import com.example.backend.model.User;
import com.example.backend.repository.PostShareRepository;
import com.example.backend.repository.PostRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    @GetMapping
    public List<PostShare> getAllShares() {
        return postShareRepository.findAll();
    }

    @GetMapping("/post/{postId}")
    public List<PostShare> getSharesByPost(@PathVariable Integer postId) {
        return postShareRepository.findByPostId(postId);
    }

    @GetMapping("/user/{userId}")
    public List<PostShare> getSharesByUser(@PathVariable Integer userId) {
        return postShareRepository.findByUserId(userId);
    }

    @PostMapping
    public ResponseEntity<?> createShare(
            @RequestParam Integer postId,
            @RequestParam Integer userId
    ) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Post not found");
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (postShareRepository.existsByPostIdAndUserId(postId, userId)) {
            return ResponseEntity.badRequest().body("Bạn đã chia sẽ post này rồi");
        }

        PostShare newShare = new PostShare();
        newShare.setPost(postOpt.get());
        newShare.setUser(userOpt.get());

        PostShare savedShare = postShareRepository.save(newShare);
        return ResponseEntity.ok(savedShare);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteShare(@RequestParam Integer postId, @RequestParam Integer userId) {
        try {
            Optional<PostShare> shareOpt = postShareRepository.findByPostIdAndUserId(postId, userId);
            if (shareOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Không tìm thấy chia sẻ");
            }

            postShareRepository.delete(shareOpt.get());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi xóa chia sẻ: " + e.getMessage());
        }
    }

}
