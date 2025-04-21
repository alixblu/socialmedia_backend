package com.example.backend.controller;

import com.example.backend.model.Comment;
import com.example.backend.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @GetMapping
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    @GetMapping("/post/{postId}")
    public List<Comment> getCommentsByPost(@PathVariable Integer postId) {
        return commentRepository.findByPostId(postId);
    }

    @PostMapping
    public Comment createComment(@RequestBody Comment comment) {
        return commentRepository.save(comment);
    }
}