package com.example.backend.controller;

import com.example.backend.model.Friendship;
import com.example.backend.repository.FriendshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friendships")
public class FriendshipController {

    @Autowired
    private FriendshipRepository friendshipRepository;

    // @GetMapping("/user/{userId}")
    // public List<Friendship> getFriendshipsByUser(@PathVariable Integer userId) {
    //     return friendshipRepository.findByUserId1OrUserId2(userId, userId);
    // }

    @PostMapping
    public Friendship createFriendship(@RequestBody Friendship friendship) {
        return friendshipRepository.save(friendship);
    }
}