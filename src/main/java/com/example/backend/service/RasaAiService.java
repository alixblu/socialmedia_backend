package com.example.backend.service;

import com.example.backend.config.RasaConfiguration.RasaProperties;
import com.example.backend.model.mongo.AiChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Profile("rasa")
public class RasaAiService {

    private final RestTemplate restTemplate;
    private final RasaProperties rasaProperties;

    @Autowired
    public RasaAiService(RestTemplate restTemplate, RasaProperties rasaProperties) {
        this.restTemplate = restTemplate;
        this.rasaProperties = rasaProperties;
    }

    /**
     * Generates a response using Rasa NLU API
     * 
     * @param botRole The system role message that defines the bot's personality
     * @param userMessage The message from the user
     * @param conversationHistory Previous messages for context
     * @return The generated response
     */
    public String generateResponse(String botRole, String userMessage, List<AiChatMessage> conversationHistory) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("sender", "user");
            requestBody.put("message", userMessage);
            
            // Add conversation context if needed
            if (!conversationHistory.isEmpty()) {
                StringBuilder context = new StringBuilder();
                int historyLimit = Math.min(conversationHistory.size(), 5);
                for (int i = conversationHistory.size() - historyLimit; i < conversationHistory.size(); i++) {
                    AiChatMessage message = conversationHistory.get(i);
                    String role = message.getRole() == AiChatMessage.MessageRole.USER ? "User" : "Bot";
                    context.append(role).append(": ").append(message.getContent()).append("\n");
                }
                requestBody.put("context", context.toString());
            }

            // Add bot personality/role information
            requestBody.put("bot_role", botRole);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            // Call Rasa endpoint
            Map<String, Object> response = restTemplate.postForObject(
                    rasaProperties.getUrl() + "/webhooks/rest/webhook", 
                    request, 
                    Map.class);

            if (response != null && response.containsKey("text")) {
                return response.get("text").toString();
            } else if (response != null && response.containsKey("message")) {
                return response.get("message").toString();
            } else {
                return "Không thể tạo phản hồi từ Rasa. Vui lòng thử lại sau.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Đã xảy ra lỗi khi kết nối với Rasa API: " + e.getMessage();
        }
    }
} 