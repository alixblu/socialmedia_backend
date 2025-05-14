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

    private static final String OLLAMA_BASE_URL = "http://localhost:11434";
    private static final String OLLAMA_GENERATE_URL = OLLAMA_BASE_URL + "/api/generate";
    private static final String OLLAMA_TAGS_URL = OLLAMA_BASE_URL + "/api/tags";
    private static final String MODEL = "tinyllama";
    private static final int TIMEOUT = 30000; // Increased timeout to 30 seconds

    public String generateResponse(String userMessage) {
        try {
            // First, verify Ollama is running and model is available
            logger.info("Checking Ollama availability...");
            ResponseEntity<Map> tagsResponse = restTemplate.getForEntity(OLLAMA_TAGS_URL, Map.class);
            logger.info("Ollama tags response: {}", tagsResponse.getBody());

            if (tagsResponse.getStatusCode() != HttpStatus.OK) {
                logger.error("Ollama tags endpoint returned non-OK status: {}", tagsResponse.getStatusCode());
                return MODEL+ ": Error connecting to AI service. Please make sure Ollama is running.";
            }

            logger.info("Attempting to connect to Ollama at: {}", OLLAMA_GENERATE_URL);
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
                OLLAMA_GENERATE_URL,
                request,
                Map.class
            );
            
            logger.info("Ollama response status: {}", response.getStatusCode());
            logger.info("Ollama response body: {}", response.getBody());

            // Extract and return the response
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null && response.getBody().containsKey("response")) {
                String ollamaResponse = (String) response.getBody().get("response");
                logger.info("Successfully extracted response: {}", ollamaResponse);
                return MODEL+ ": " + ollamaResponse;
            } else {
                logger.error("Invalid response from Ollama: {}", response.getBody());
                return MODEL+ ": I'm having trouble generating a response. Please try again.";
            }
        } catch (RestClientException e) {
            logger.error("Error connecting to Ollama: {}", e.getMessage(), e);
            return MODEL+ ": Error connecting to AI service. Please make sure Ollama is running and accessible at " + OLLAMA_BASE_URL;
        } catch (Exception e) {
            logger.error("Unexpected error in Ollama service: {}", e.getMessage(), e);
            return MODEL+ ": An unexpected error occurred. Please try again.";
        }
    }
} 