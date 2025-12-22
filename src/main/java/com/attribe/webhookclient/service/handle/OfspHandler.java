package com.attribe.webhookclient.service.handle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.attribe.webhookclient.pojo.client.MessageDTO;
import com.attribe.webhookclient.pojo.whatsapp.Interactive;
import com.attribe.webhookclient.pojo.whatsapp.Message;
import com.attribe.webhookclient.pojo.whatsapp.Metadata;
import com.attribe.webhookclient.service.Constant;
import com.attribe.webhookclient.service.ConversationMemoryService;
import com.attribe.webhookclient.service.WhatsAppSendMenuService;
import com.attribe.webhookclient.service.WhatsAppSendMessageService;
import com.attribe.webhookclient.service.openai.OpenAIException;
import com.attribe.webhookclient.service.openai.OpenAIService;

@Component("763421936848515")
public class OfspHandler implements  ClientHandle{
    private static final Logger logger = LoggerFactory.getLogger(OfspHandler.class);


    @Value("${whatsapp.menu.button.chat-agent}")
    private String buttonChatAgent;
    
	@Autowired
	private WhatsAppSendMessageService messageService;

    @Autowired
    private WhatsAppSendMenuService menuService;

    @Autowired
    private OpenAIService openAIService;
    
    @Autowired(required = false)
    private ConversationMemoryService conversationMemoryService;


    @Override
    public void handleInbondMessage(Metadata metadata, Message message) {


       
		try {
            String commandRecived ="";
            String type =  message.getType()+"";
            String button_id = "";
            
            //Text message handlling
            if(Constant.MessageType.test.equals(type)){
                commandRecived =  message.getText() != null ? message.getText().getBody() : "";
                commandRecived = commandRecived.toLowerCase();
            
                //Quit from agent chat
                if(commandRecived.equalsIgnoreCase("q")){
                    Constant.UserChatChache.cache.remove(message.getFrom());
                }

                //Keep chating with agent
                String isChantExsit = Constant.UserChatChache.cache.get(message.getFrom());
                if(isChantExsit!=null && isChantExsit.equals("chat_agent")){
                    commandRecived = "chat_agent";
                }
                

             
            }

            //intractive message handlling
            else if(Constant.MessageType.interactive.equals(type)){
                commandRecived= getButtonId(metadata, message);

                if("chat_agent".equals(commandRecived)){

                   // Constant.UserChatChache.cache.put(message.getFrom(), "chat_agent");
                   Constant.UserChatChache.cache.put(message.getFrom(), "chat_agent");
                    commandRecived = "chat_agent";
                }
                else{
                    Constant.UserChatChache.cache.remove(message.getFrom());

                }
            }

           System.out.println("Command Recived:---------- "+ commandRecived);

            switch (commandRecived.toLowerCase()) {
            case "lst_sponcered_childs": // List Sponcered Child
                sendListSponceredChildMessage(metadata, message);
                break;
            case "lst_last_donations": // List Donation
                sendListDonationessage(metadata, message);
                break;
            case "lst_contact_detais": // Get Latest News
                sendListLatestNewsMessage(metadata, message);
                break;
            case "chat_agent": // Chat Agent
                // Store the user prompt in Redis for conversation memory
                String userPrompt = message.getText() != null ? message.getText().getBody() : "";
                if (conversationMemoryService != null && !userPrompt.isEmpty()) {
                    conversationMemoryService.storePrompt(message.getFrom(), userPrompt);
                    logger.info("Stored user prompt in conversation memory for: {}", message.getFrom());
                }
                sendChatAgentMessage(metadata, message);
                break;
            case "m": // Send Menu
                sendMenuMessage(metadata, message);
                break;
            default:
                System.out.println("Unknown type!");
                sendMenuMessage(metadata, message);
        }

           
		} catch (Exception e) {
			System.out.println(e.getMessage());
        }

    }



    private void sendListSponceredChildMessage(Metadata metadata, Message message) {

		try {

			MessageDTO messageDto= new MessageDTO();
			messageDto.setTo(message.getFrom());
			
			messageDto.setBody("Api will be call to get the Sponcered Childs");
			
	
           
			messageService.sendMessage(metadata.getPhone_number_id(), messageDto);
		} catch (Exception e) {
			System.out.println(e.getMessage());
        }

    }


     private void sendListDonationessage(Metadata metadata, Message message) {

		try {

			MessageDTO messageDto= new MessageDTO();
			messageDto.setTo(message.getFrom());
			
			messageDto.setBody("API will be call to get the List Donation. Mujtabab & TUba");
			
	
           
			messageService.sendMessage(metadata.getPhone_number_id(), messageDto);
		} catch (Exception e) {
			System.out.println(e.getMessage());
        }

    }




    private void sendListLatestNewsMessage(Metadata metadata, Message message) {

		try {

			MessageDTO messageDto= new MessageDTO();
			messageDto.setTo(message.getFrom());
			
			messageDto.setBody("API will be call to get the List Latest News Message");
			
	
           
			messageService.sendMessage(metadata.getPhone_number_id(), messageDto);
		} catch (Exception e) {
			System.out.println(e.getMessage());
        }

    }


    private void sendChatAgentMessage(Metadata metadata, Message message) {

		try {
			MessageDTO messageDto = new MessageDTO();
			messageDto.setTo(message.getFrom());
			
			// Get user message from text
			String userMessage = message.getText() != null ? message.getText().getBody() : "Hello";
			logger.info("Processing chat agent request from: {} with message: {}", message.getFrom(), userMessage);
			
			// Build conversation context from Redis
			String conversationContext = buildConversationContext(message.getFrom(), userMessage);
			
			try {
				// Get response from OpenAI API with conversation context
				String aiResponse = openAIService.getResponse(conversationContext);
				messageDto.setBody(aiResponse);
				logger.info("Successfully received response from OpenAI API");
			} catch (OpenAIException e) {
				logger.error("OpenAI API error: {}", e.getMessage(), e);
				messageDto.setBody("I apologize, I'm temporarily unavailable. Please try again later.");
			}
			
			messageService.sendMessage(metadata.getPhone_number_id(), messageDto);
		} catch (Exception e) {
			logger.error("Error in sendChatAgentMessage: {}", e.getMessage(), e);
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


    private void sendMenuMessage(Metadata metadata, Message message) {

		try {

			MessageDTO messageDto= new MessageDTO();
			messageDto.setTo(message.getFrom());
			
			messageDto.setBody("");
			
	
           
			menuService.sendMessage(metadata.getPhone_number_id(), messageDto);
		} catch (Exception e) {
			System.out.println(e.getMessage());
        }

    }



    private String getButtonId(Metadata metadata, Message message) {
         
        String  button_id = "m"; // be default show menu

		try {

			MessageDTO messageDto= new MessageDTO();
			messageDto.setTo(message.getFrom());
            
			
             Interactive interactive = message.getInteractive();
                if(interactive!=null){
                   button_id =  interactive.getButton_reply().getId();

                }
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
        }

        return button_id;

    }

    

}
