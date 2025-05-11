package com.example.backend.repository.mongo;

import com.example.backend.model.mongo.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
    // Check if a chat room exists by friendshipId
    boolean existsByFriendshipId(Integer friendshipId);
    
    // Find a chat room by friendshipId
    Optional<ChatRoom> findByFriendshipId(Integer friendshipId);
    
    // Update active status by friendshipId
    @Query("{ 'friendshipId' : ?0 }")
    @Update("{ '$set' : { 'active' : ?1 } }")
    void updateActiveStatusByFriendshipId(Integer friendshipId, boolean active);
} 