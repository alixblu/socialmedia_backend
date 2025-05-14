package com.example.backend.repository.mongo;

import com.example.backend.model.mongo.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    
    List<ChatMessage> findBySenderIdAndReceiverIdOrderByTimestampAsc(Integer senderId, Integer receiverId);
    
    List<ChatMessage> findByReceiverIdAndStatus(Integer receiverId, ChatMessage.MessageStatus status);
    
    void deleteBySenderIdAndReceiverId(Integer senderId, Integer receiverId);

    List<ChatMessage> findByChatRoomIdOrderByTimestampAsc(String chatRoomId);
    
    List<ChatMessage> findByChatRoomIdAndStatus(String chatRoomId, ChatMessage.MessageStatus status);
    
    @Aggregation(pipeline = {
        "{ $match: { $or: [ { senderId: ?0 }, { receiverId: ?0 } ] } }",
        "{ $sort: { timestamp: -1 } }",
        "{ $group: { _id: { $cond: [ { $eq: ['$senderId', ?0] }, '$receiverId', '$senderId' ] }, latestMessage: { $first: '$$ROOT' } } }",
        "{ $replaceRoot: { newRoot: '$latestMessage' } }",
        "{ $sort: { timestamp: -1 } }"
    })
    List<ChatMessage> findLatestMessagesForUser(Integer userId);
} 