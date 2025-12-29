package com.attribe.webhookclient.service.handle;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.attribe.webhookclient.entity.Client;
import com.attribe.webhookclient.pojo.whatsapp.Message;
import com.attribe.webhookclient.pojo.whatsapp.Metadata;
import com.attribe.webhookclient.repository.ClientRepository;
import com.attribe.webhookclient.service.ConversationMemoryService;
import com.attribe.webhookclient.service.WhatsAppSendMessageService;
import com.attribe.webhookclient.service.openai.OpenAIException;
import com.attribe.webhookclient.service.openai.OpenAIService;

@Component("default")
public class DefaultHandler implements ClientHandle {

	private static final Logger logger = LoggerFactory.getLogger(DefaultHandler.class);

	@Autowired
	private WhatsAppSendMessageService messageService;

	@Autowired
	private OpenAIService openAIService;

	@Autowired
	private ClientRepository clientRepository;

	@Autowired(required = false)
	private ConversationMemoryService conversationMemoryService;

	@Override
	public void handleInbondMessage(Metadata metadata, Message message) {
		try {
			// Get user message from text
			logger.debug("------------------------------------------- [Agentic-AI Default]");
			String userMessage = message.getText() != null ? message.getText().getBody() : "Hello";
			logger.info("Processing default handler request from: {} with message: {}", message.getFrom(), userMessage);
			
			// Store the user prompt in Redis for conversation memory
			if (conversationMemoryService != null && !userMessage.isEmpty()) {
				conversationMemoryService.storePrompt(message.getFrom(), userMessage);
				logger.info("Stored user prompt in conversation memory for: {}", message.getFrom());
			}
			
			// Build conversation context from Redis
			String conversationContext = buildConversationContext(message.getFrom(), userMessage);
			
			// Fetch Client by phone number ID and prepend chatPrefix if available
			String finalContext = conversationContext;
			try {
				Optional<Client> clientOptional = clientRepository.findByPhoneNumberId(metadata.getPhone_number_id());
				if (clientOptional.isPresent()) {
					Client client = clientOptional.get();
					String chatPrefix = client.getChatPrefix();
					
					// Prepend chatPrefix to conversation context if it exists and is not empty
					if (chatPrefix != null && !chatPrefix.trim().isEmpty()) {
						finalContext = chatPrefix + "\n\n" + conversationContext;
						logger.info("Prepended chatPrefix for client: {}", metadata.getPhone_number_id());
					}
				}
			} catch (Exception e) {
				logger.warn("Failed to fetch chatPrefix for client: {}, proceeding with original context", metadata.getPhone_number_id(), e);
			}
			
			try {
				// Get response from OpenAI API with conversation context
				String aiResponse = openAIService.getResponse(finalContext);
				logger.info("Request Text  for OpenAI API with finalContext :" + finalContext);
				logger.info("Successfully received response from OpenAI API :" +aiResponse);
				
				// Send OpenAI response to WhatsApp user with footer
				messageService.sendOpenAiMessage(metadata.getPhone_number_id(), message.getFrom(), aiResponse);
			} catch (OpenAIException e) {
				logger.error("OpenAI API error: {}", e.getMessage(), e);
				String errorMessage = "I apologize, I'm temporarily unavailable. Please try again later.";
				messageService.sendOpenAiMessage(metadata.getPhone_number_id(), message.getFrom(), errorMessage);
			}
		} catch (Exception e) {
			logger.error("Error in DefaultHandler: {}", e.getMessage(), e);
		}
	}
	
	/**
	 * Build conversation context from stored prompts and the current message.
	 * Format: User: <message1>\nUser: <message2>\n...\nUser: <currentMessage>
	 * 
	 * @param userId The user ID
	 * @param currentMessage The current user message
	 * @return The formatted conversation context
	 */
	private String buildConversationContext(String userId, String currentMessage) {
		StringBuilder context = new StringBuilder();
		
		try {
			// Retrieve stored prompts from Redis
			if (conversationMemoryService != null) {
				java.util.List<String> storedPrompts = conversationMemoryService.getPrompts(userId);
				
				// Build context from previous prompts
				for (String prompt : storedPrompts) {
					if (prompt != null && !prompt.isEmpty()) {
						context.append("User: ").append(prompt).append("\n");
					}
				}
				
				if (!storedPrompts.isEmpty()) {
					logger.info("Retrieved {} previous prompts for user: {}", storedPrompts.size(), userId);
				}
			}
		} catch (Exception e) {
			// Fail gracefully - just use current message if Redis fails
			logger.error("Failed to retrieve conversation context for user: {}, using current message only", userId, e);
		}
		
		// Append current message
		context.append("User: ").append(currentMessage);
		
		return context.toString();
	}

}
