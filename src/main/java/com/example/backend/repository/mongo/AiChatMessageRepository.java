package com.example.backend.repository.mongo;

import com.example.backend.model.mongo.AiChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AiChatMessageRepository extends MongoRepository<AiChatMessage, String> {
    
    List<AiChatMessage> findByUserIdAndBotIdOrderByTimestampAsc(Integer userId, String botId);
    
    List<AiChatMessage> findTop20ByUserIdAndBotIdOrderByTimestampDesc(Integer userId, String botId);
    
    List<AiChatMessage> findDistinctBotIdByUserId(Integer userId);
    
    List<AiChatMessage> findByUserIdOrderByTimestampDesc(Integer userId);
} 