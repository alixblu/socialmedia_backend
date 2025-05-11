package com.example.backend.controller;

import com.example.backend.model.Friendship;
import com.example.backend.repository.FriendshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/friendships")
@CrossOrigin(origins = "http://localhost:5173")
public class FriendshipController {

    @Autowired
    private FriendshipRepository friendshipRepository;

    // @GetMapping("/user/{userId}")
    // public List<Friendship> getFriendshipsByUser(@PathVariable Integer userId) {
    //     return friendshipRepository.findByUserId1OrUserId2(userId, userId);
    // }

    @GetMapping("/between")
    public ResponseEntity<?> getFriendshipBetweenUsers(
            @RequestParam Integer userId1,
            @RequestParam Integer userId2) {
        Optional<Friendship> friendship = friendshipRepository.findByUserIds(userId1, userId2);
        return friendship.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Friendship createFriendship(@RequestBody Friendship friendship) {
        return friendshipRepository.save(friendship);
    }
}