package com.example.backend.service;

import com.example.backend.model.AiBot;
import com.example.backend.repository.AiBotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AiChatService {
    @Autowired
    private AiBotRepository aiBotRepository;

    public List<AiBot> getAllBots() {
        return aiBotRepository.findAll();
    }
    
    public Optional<AiBot> getBotById(String botId) {
        return aiBotRepository.findById(botId);
    }

    public String getBotRoleDescription(String botId) {
        return aiBotRepository.findById(botId)
                .map(AiBot::getDescription)
                .orElse("A helpful AI assistant");
    }
} 