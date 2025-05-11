package com.example.backend.controller;

import com.example.backend.model.mongo.ChatMessage;
import com.example.backend.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:5173")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @MessageMapping("/send")
    public void sendMessage(@Payload ChatMessage message) {
        chatService.sendMessage(message);
    }

    @GetMapping("/history/{senderId}/{receiverId}")
    public List<ChatMessage> getChatHistory(
            @PathVariable String senderId,
            @PathVariable String receiverId) {
        return chatService.getChatHistory(senderId, receiverId);
    }

    @PostMapping("/read/{receiverId}")
    public void markMessagesAsRead(@PathVariable String receiverId) {
        chatService.markMessagesAsRead(receiverId);
    }

    @DeleteMapping("/history/{senderId}/{receiverId}")
    public void deleteChatHistory(
            @PathVariable String senderId,
            @PathVariable String receiverId) {
        chatService.deleteChatHistory(senderId, receiverId);
    }
} 