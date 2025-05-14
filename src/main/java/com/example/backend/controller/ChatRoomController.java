package com.example.backend.controller;

import com.example.backend.model.Friendship;
import com.example.backend.model.FriendshipStatus;
import com.example.backend.model.mongo.ChatRoom;
import com.example.backend.repository.FriendshipRepository;
import com.example.backend.repository.mongo.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/chat/rooms")
@CrossOrigin(origins = "http://localhost:5173")
public class ChatRoomController {

    @Autowired
    private ChatRoomRepository chatRoomRepository;
    
    @Autowired
    private FriendshipRepository friendshipRepository;

    @GetMapping("/exists/{friendshipId}")
    public ResponseEntity<Boolean> chatRoomExists(@PathVariable Integer friendshipId) {
        return ResponseEntity.ok(chatRoomRepository.existsByFriendshipId(friendshipId));
    }
    
    @PostMapping("/create/{friendshipId}")
    public ResponseEntity<?> createChatRoom(@PathVariable Integer friendshipId) {
        // Check if friendship exists and is valid
        boolean friendshipValid = friendshipRepository.findById(friendshipId)
            .map(f -> f.getStatus() == FriendshipStatus.ACCEPTED)
            .orElse(false);
            
        if (!friendshipValid) {
            return ResponseEntity.badRequest().body("Friendship not found or not accepted");
        }
        
        // Check if chat room already exists
        if (chatRoomRepository.existsByFriendshipId(friendshipId)) {
            return ResponseEntity.badRequest().body("Chat room already exists for this friendship");
        }
        
        ChatRoom newRoom = new ChatRoom();
        newRoom.setFriendshipId(friendshipId);
        ChatRoom chatRoom = chatRoomRepository.save(newRoom);
        return ResponseEntity.ok(chatRoom);
    }
    
    @PutMapping("/{friendshipId}/active")
    public ResponseEntity<?> updateChatRoomActiveStatus(
            @PathVariable Integer friendshipId,
            @RequestParam boolean active) {
        
        if (!chatRoomRepository.existsByFriendshipId(friendshipId)) {
            return ResponseEntity.notFound().build();
        }
        
        chatRoomRepository.updateActiveStatusByFriendshipId(friendshipId, active);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/find")
    public ResponseEntity<?> findChatRoom(
            @RequestParam Integer userId1,
            @RequestParam Integer userId2) {

        Map<String, String> error = new HashMap<>();


        // Find friendship between the two users using the repository method
        Optional<Friendship> friendship = friendshipRepository.findByUserIds(userId1, userId2);
        
        // If friendship doesn't exist or is not accepted, return null
        if (friendship.isEmpty() || friendship.get().getStatus() != FriendshipStatus.ACCEPTED) {
            error.put("message", "friendship not available");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        
        Integer friendshipId = friendship.get().getId();
        
        // Check if chat room exists
        Optional<ChatRoom> existingRoom = chatRoomRepository.findByFriendshipId(friendshipId);
        
        if (existingRoom.isPresent()) {
            // If room exists but is not active
            if (!existingRoom.get().isActive()) {
                error.put("message", "friendship not available for chatting");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);

            }
            // Room exists and is active, return it
            return ResponseEntity.ok(existingRoom.get());
        } else {
            // No room exists but friendship is accepted, create new room
            ChatRoom newRoom = new ChatRoom();
            newRoom.setFriendshipId(friendshipId);
            return ResponseEntity.ok(chatRoomRepository.save(newRoom));
        }
    }
} 