package com.example.backend.controller;

import com.example.backend.model.PostLike;
import com.example.backend.repository.PostLikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/likes")
public class PostLikeController {

    @Autowired
    private PostLikeRepository postLikeRepository;

    @GetMapping("/post/{postId}")
    public List<PostLike> getLikesByPost(@PathVariable Integer postId) {
        return postLikeRepository.findByPostId(postId);
    }

    @PostMapping
    public PostLike createLike(@RequestBody PostLike like) {
        return postLikeRepository.save(like);
    }
}