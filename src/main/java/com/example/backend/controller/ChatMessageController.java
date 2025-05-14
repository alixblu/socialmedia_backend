package com.example.backend.controller;

import com.example.backend.model.User;
import com.example.backend.model.mongo.ChatMessage;
import com.example.backend.repository.UserRepository;
import com.example.backend.repository.mongo.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat/messages")
@CrossOrigin(origins = "http://localhost:5173")
public class ChatMessageController {

    @Autowired
    private ChatMessageRepository chatMessageRepository;
    
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody Map<String, Object> payload) {
        String chatRoomId = (String) payload.get("chatRoomId");
        Integer senderId = (Integer) payload.get("senderId");
        Integer receiverId = (Integer) payload.get("receiverId");
        String content = (String) payload.get("content");
        
        // Validate required fields
        if (chatRoomId == null || senderId == null || receiverId == null || content == null) {
            return ResponseEntity.badRequest().body("Missing required fields");
        }
        
        // Create new message
        ChatMessage message = new ChatMessage();
        message.setChatRoomId(chatRoomId);
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        message.setStatus(ChatMessage.MessageStatus.SENT);
        
        // Save and return
        ChatMessage savedMessage = chatMessageRepository.save(message);
        return ResponseEntity.ok(savedMessage);
    }

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
        List<ChatMessage> latestMessages = chatMessageRepository.findLatestMessagesForUser(userId);
        List<Map<String, Object>> conversationsWithUsers = new ArrayList<>();
        
        for (ChatMessage message : latestMessages) {
            Map<String, Object> conversationData = new HashMap<>();
            
            // Add the message
            conversationData.put("message", message);
            
            // Determine the conversation partner ID (the other user)
            Integer partnerUserId;
            if (message.getSenderId().equals(userId)) {
                partnerUserId = message.getReceiverId();
            } else {
                partnerUserId = message.getSenderId();
            }
            
            // Get the user data for the conversation partner
            userRepository.findById(partnerUserId).ifPresent(user -> {
                Map<String, Object> partnerData = new HashMap<>();
                partnerData.put("id", user.getId());
                partnerData.put("username", user.getUsername());
                partnerData.put("avatarUrl", user.getAvatarUrl());
                // Add any other user fields you need
                
                conversationData.put("partner", partnerData);
            });
            
            conversationsWithUsers.add(conversationData);
        }
        
        return ResponseEntity.ok(conversationsWithUsers);
    }
} 