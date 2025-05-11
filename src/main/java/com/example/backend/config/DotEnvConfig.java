package com.example.backend.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.util.Arrays;

@Configuration
public class DotEnvConfig {

    private final Environment environment;

    public DotEnvConfig(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void init() {
        // Check if we're in a production profile
        boolean isProduction = Arrays.asList(environment.getActiveProfiles()).contains("prod");

        // Don't load .env in production as environment variables should be set properly
        if (isProduction) {
            return;
        }

        // Check if .env file exists
        File dotEnvFile = new File(".env");
        if (dotEnvFile.exists()) {
            // Load .env file
            Dotenv dotenv = Dotenv.configure().load();
            
            // Set environment variables from .env if they don't already exist in the system
            if (System.getenv("OPENAI_API_KEY") == null && dotenv.get("OPENAI_API_KEY") != null) {
                System.setProperty("OPENAI_API_KEY", dotenv.get("OPENAI_API_KEY"));
            }
        }
    }
} 