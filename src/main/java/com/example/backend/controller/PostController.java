package com.example.backend.controller;

import com.example.backend.model.Post;
import com.example.backend.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @GetMapping
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @GetMapping("/{id}")
    public Post getPostById(@PathVariable Integer id) {
        return postRepository.findById(id).orElse(null);
    }

    @GetMapping("/user/{userId}")
    public List<Post> getPostsByUser(@PathVariable Integer userId) {
        return postRepository.findByUserId(userId);
    }

    @PostMapping
    public Post createPost(@RequestBody Post post) {
        return postRepository.save(post);
    }

    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Integer id) {
        postRepository.deleteById(id);
    }
}