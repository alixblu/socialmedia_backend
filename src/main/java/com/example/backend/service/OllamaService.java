package com.example.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@Service
public class OllamaService {
    private static final Logger logger = LoggerFactory.getLogger(OllamaService.class);

    @Autowired
    private RestTemplate restTemplate;

    private static final String OLLAMA_API_URL = "http://localhost:11434/api/generate";
    private static final String MODEL = "phi";
    private static final int TIMEOUT = 10000; // Increased timeout to 10 seconds

    public String generateResponse(String userMessage) {
        try {
            logger.info("Attempting to connect to Ollama at: {}", OLLAMA_API_URL);
            logger.info("Using model: {}", MODEL);
            logger.info("User message: {}", userMessage);
            
            // Configure timeout
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(TIMEOUT);
            factory.setReadTimeout(TIMEOUT);
            restTemplate.setRequestFactory(factory);

            // Prepare the request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", MODEL);
            requestBody.put("prompt", "User: " + userMessage + "\nAssistant:");
            requestBody.put("stream", false);

            logger.info("Request body: {}", requestBody);

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Create the request entity
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            // Make the API call
            logger.info("Sending request to Ollama...");
            ResponseEntity<Map> response = restTemplate.postForEntity(
                OLLAMA_API_URL,
                request,
                Map.class
            );
            
            logger.info("Ollama response status: {}", response.getStatusCode());
            logger.info("Ollama response body: {}", response.getBody());

            // Extract and return the response
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null && response.getBody().containsKey("response")) {
                String ollamaResponse = (String) response.getBody().get("response");
                logger.info("Successfully extracted response: {}", ollamaResponse);
                return ollamaResponse;
            } else {
                logger.error("Invalid response from Ollama: {}", response.getBody());
                return "I'm having trouble generating a response. Please try again.";
            }
        } catch (RestClientException e) {
            logger.error("Error connecting to Ollama: {}", e.getMessage(), e);
            // Check if Ollama is running
            try {
                ResponseEntity<String> healthCheck = restTemplate.getForEntity("http://localhost:11434/api/tags", String.class);
                logger.info("Ollama health check response: {}", healthCheck.getStatusCode());
                return "Error connecting to AI service. Please make sure Ollama is running.";
            } catch (Exception ex) {
                logger.error("Ollama health check failed: {}", ex.getMessage(), ex);
                return "Error connecting to AI service. Please make sure Ollama is running and accessible at http://localhost:11434";
            }
        } catch (Exception e) {
            logger.error("Unexpected error in Ollama service: {}", e.getMessage(), e);
            return "An unexpected error occurred. Please try again.";
        }
    }
} 