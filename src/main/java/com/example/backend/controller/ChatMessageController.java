package com.example.backend.controller;

import com.example.backend.model.mongo.ChatMessage;
import com.example.backend.repository.mongo.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat/messages")
@CrossOrigin(origins = "http://localhost:5173")
public class ChatMessageController {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @GetMapping("/{chatRoomId}/history")
    public ResponseEntity<?> getChatHistory(@PathVariable String chatRoomId) {
        return ResponseEntity.ok(chatMessageRepository.findByChatRoomIdOrderByTimestampAsc(chatRoomId));
    }

    @PostMapping("/{chatRoomId}/read")
    public ResponseEntity<?> markMessagesAsRead(@PathVariable String chatRoomId) {
        var unreadMessages = chatMessageRepository.findByChatRoomIdAndStatus(
            chatRoomId, 
            ChatMessage.MessageStatus.DELIVERED
        );
        
        unreadMessages.forEach(message -> {
            message.setStatus(ChatMessage.MessageStatus.READ);
            chatMessageRepository.save(message);
        });
        
        return ResponseEntity.ok().build();
    }

    @GetMapping("/conversations/{userId}")
    public ResponseEntity<?> getLatestConversations(@PathVariable Integer userId) {
        return ResponseEntity.ok(chatMessageRepository.findLatestMessagesForUser(userId));
    }
} 