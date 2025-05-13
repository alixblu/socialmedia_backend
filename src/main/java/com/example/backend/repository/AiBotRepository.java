package com.example.backend.repository;

import com.example.backend.model.AiBot;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AiBotRepository extends MongoRepository<AiBot, String> {
} 