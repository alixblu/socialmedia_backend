package com.example.backend.service;

import com.example.backend.config.RasaConfiguration.RasaProperties;
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
     * @return The generated response
     */
    public String generateResponse(String botRole, String userMessage) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("sender", "user");
            requestBody.put("message", userMessage);
            requestBody.put("bot_role", botRole);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            // Call Rasa endpoint
            List<Map<String, Object>> responses = restTemplate.postForObject(
                    rasaProperties.getUrl() + "/webhooks/rest/webhook", 
                    request, 
                    List.class);

            if (responses != null && !responses.isEmpty()) {
                Map<String, Object> firstResponse = responses.get(0);
                if (firstResponse.containsKey("text")) {
                    return firstResponse.get("text").toString();
                } else if (firstResponse.containsKey("message")) {
                    return firstResponse.get("message").toString();
                }
            }
            
            return "Không thể tạo phản hồi từ Rasa. Vui lòng thử lại sau.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Đã xảy ra lỗi khi kết nối với Rasa API: " + e.getMessage();
        }
    }
} 