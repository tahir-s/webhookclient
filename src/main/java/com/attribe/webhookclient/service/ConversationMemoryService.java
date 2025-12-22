package com.attribe.webhookclient.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Service for managing short-term conversation memory using Redis.
 * Stores the last 20 user prompts per user with a 15-minute TTL.
 */
@Service
public class ConversationMemoryService {
    private static final Logger logger = LoggerFactory.getLogger(ConversationMemoryService.class);
    
    private static final String KEY_PREFIX = "chat:short_term:";
    private static final int MAX_PROMPTS = 20;
    private static final long TTL_MINUTES = 2;
    
    @Autowired(required = false)
    private RedisTemplate<String, String> redisTemplate;
    
    /**
     * Store a user prompt in Redis.
     * 
     * @param userId The user ID (typically message.getFrom())
     * @param prompt The user's prompt text
     */
    public void storePrompt(String userId, String prompt) {
        if (redisTemplate == null) {
            logger.warn("Redis is not available, skipping prompt storage");
            return;
        }
        
        try {
            String key = buildKey(userId);
            
            // RPUSH: Add prompt to the end of the list
            redisTemplate.opsForList().rightPush(key, prompt);
            
            // LTRIM: Keep only the last 20 prompts (FIFO behavior)
            redisTemplate.opsForList().trim(key, -MAX_PROMPTS, -1);
            
            // Set TTL: Refresh expiry to 15 minutes
            redisTemplate.expire(key, TTL_MINUTES, TimeUnit.MINUTES);
            
            logger.info("Stored prompt for user: {} (key: {})", userId, key);
        } catch (Exception e) {
            // Fail gracefully - don't crash the flow
            logger.error("Failed to store prompt in Redis for user: {}", userId, e);
        }
    }
    
    /**
     * Retrieve all stored prompts for a user in chronological order.
     * 
     * @param userId The user ID
     * @return List of prompts (oldest first), or empty list if none found
     */
    public List<String> getPrompts(String userId) {
        if (redisTemplate == null) {
            logger.warn("Redis is not available, returning empty prompts");
            return List.of();
        }
        
        try {
            String key = buildKey(userId);
            List<String> prompts = redisTemplate.opsForList().range(key, 0, -1);
            
            if (prompts != null && !prompts.isEmpty()) {
                logger.info("Retrieved {} prompts for user: {}", prompts.size(), userId);
                return prompts;
            }
            
            logger.debug("No prompts found for user: {}", userId);
            return List.of();
        } catch (Exception e) {
            // Fail gracefully - return empty list
            logger.error("Failed to retrieve prompts from Redis for user: {}", userId, e);
            return List.of();
        }
    }
    
    /**
     * Build the Redis key for a user's conversation history.
     * 
     * @param userId The user ID
     * @return The Redis key
     */
    private String buildKey(String userId) {
        return KEY_PREFIX + userId;
    }
    
    /**
     * Clear all prompts for a user.
     * Useful for cleaning up when user exits chat mode.
     * 
     * @param userId The user ID
     */
    public void clearPrompts(String userId) {
        if (redisTemplate == null) {
            return;
        }
        
        try {
            String key = buildKey(userId);
            redisTemplate.delete(key);
            logger.info("Cleared prompts for user: {}", userId);
        } catch (Exception e) {
            logger.error("Failed to clear prompts for user: {}", userId, e);
        }
    }
}
