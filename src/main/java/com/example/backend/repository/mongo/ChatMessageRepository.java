package com.example.backend.repository.mongo;

import com.example.backend.model.mongo.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    
    List<ChatMessage> findBySenderIdAndReceiverIdOrderByTimestampAsc(String senderId, String receiverId);
    
    List<ChatMessage> findByReceiverIdAndStatus(String receiverId, ChatMessage.MessageStatus status);
    
    void deleteBySenderIdAndReceiverId(String senderId, String receiverId);
} 