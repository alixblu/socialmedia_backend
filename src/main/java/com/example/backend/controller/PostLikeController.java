package com.example.backend.controller;

import com.example.backend.model.Post;
import com.example.backend.model.PostLike;
import com.example.backend.model.ReactionType;
import com.example.backend.model.User;
import com.example.backend.repository.PostLikeRepository;
import com.example.backend.repository.PostRepository;
import com.example.backend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/likes")
public class PostLikeController {

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/post/{postId}")
    public List<PostLike> getLikesByPost(@PathVariable Integer postId) {
        return postLikeRepository.findByPostId(postId);
    }

   
   @PostMapping
    public ResponseEntity<?> createLike(
            @RequestParam Integer postId,
            @RequestParam Integer userId,
            @RequestParam(required = false) ReactionType  reactionType) {

        Optional<Post> postOpt = postRepository.findById(postId);
        Optional<User> userOpt = userRepository.findById(userId);

        if (postOpt.isEmpty() || userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Post hoặc User không tồn tại");
        }

        PostLike postLike = new PostLike();
        postLike.setPost(postOpt.get());
        postLike.setUser(userOpt.get());
        postLike.setReactionType(reactionType);

        PostLike saved = postLikeRepository.save(postLike);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteLikeByUserAndPost(
            @RequestParam Integer postId,
            @RequestParam Integer userId) {

        Optional<Post> postOpt = postRepository.findById(postId);
        Optional<User> userOpt = userRepository.findById(userId);

        if (postOpt.isEmpty() || userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Post hoặc User không tồn tại");
        }

        Optional<PostLike> likeOpt = postLikeRepository.findByPostAndUser(postOpt.get(), userOpt.get());

        if (likeOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Không tìm thấy like để xóa");
        }

        postLikeRepository.delete(likeOpt.get());
        return ResponseEntity.ok("Unlike thành công");
    }



}