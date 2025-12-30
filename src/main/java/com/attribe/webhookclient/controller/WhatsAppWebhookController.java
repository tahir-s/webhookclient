package com.attribe.webhookclient.controller;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.attribe.webhookclient.entity.SystemActivityLog;
import com.attribe.webhookclient.pojo.whatsapp.Change;
import com.attribe.webhookclient.pojo.whatsapp.Entry;
import com.attribe.webhookclient.pojo.whatsapp.Message;
import com.attribe.webhookclient.pojo.whatsapp.Value;
import com.attribe.webhookclient.pojo.whatsapp.WebhookPayload;
import com.attribe.webhookclient.service.SystemActivityLogService;
import com.attribe.webhookclient.service.WhatsAppSendMessageService;
import com.attribe.webhookclient.service.handle.ClientHandle;
import com.attribe.webhookclient.service.handle.ClientHandlerFactory;

@RestController
@RequestMapping("/webhook")
public class WhatsAppWebhookController {
    
    private static final Logger logger = LoggerFactory.getLogger(WhatsAppWebhookController.class);
    
    @org.springframework.beans.factory.annotation.Value("${whatsapp.token}")
    private String whatsappToken;
    
    @Autowired
    private ClientHandlerFactory factory;

    @Autowired
    private WhatsAppSendMessageService sendMessageService;
    
    @Autowired
    private SystemActivityLogService systemActivityLogService;
    // STEP 1: Verification Endpoint
    @GetMapping()
    public ResponseEntity<String> verifyWebhook(
            @RequestParam(name = "hub.mode", required = false) String mode,
            @RequestParam(name = "hub.challenge", required = false) String challenge,
            @RequestParam(name = "hub.verify_token", required = false) String token) {

        if ("subscribe".equals(mode) && whatsappToken.equals(token)) {
            return ResponseEntity.ok(challenge); // Meta expects this response
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Verification failed");
        }
    }

    // STEP 2: Receive Messages
    @PostMapping("/old")
    public ResponseEntity<Void> receiveWebhook(@RequestBody Map<String, Object> payload) {

        logger.info("Received Webhook Event: {}", payload);
        return ResponseEntity.ok().build(); // Respond with 200 OK
    }
    
    @PostMapping()
    public ResponseEntity<Void> receiveWebhook(@RequestBody WebhookPayload payload) {
    	
    	// Validate payload
    	if (payload == null || payload.getObject() == null || payload.getEntry() == null) {
    		return ResponseEntity.badRequest().build();
    	}
    	
    	logger.info("Received Webhook Event: {}", payload.toString());
        if ("whatsapp_business_account".equals(payload.getObject())) {
            for (Entry entry : payload.getEntry()) {
                for (Change change : entry.getChanges()) {
                    Value value = change.getValue();
                    
                    // [SMART BUG FIX #1] Check if messages list exists AND is not empty
                    if (value.getMessages() == null || value.getMessages().isEmpty()) {
                        logger.debug("Skipping webhook: No messages found or empty messages list");
                        return ResponseEntity.ok().build();
                    }
                    
                    // [SMART BUG FIX #2] Validate metadata exists
                    if (value.getMetadata() == null) {
                        logger.warn("Skipping webhook: Metadata is null");
                        return ResponseEntity.ok().build();
                    }
                    
                    for (Message message : value.getMessages()) {
                        // [SMART BUG FIX #3] Filter delivery/read receipts: they have no contacts
                        if (isNonConversationalReceipt(message, value)) {
                            logger.debug("Skipping non-conversational message (delivery/read receipt)");
                            return ResponseEntity.ok().build();
                        }
                        
                        String from = message.getFrom();
                        String text = message.getText() != null ? message.getText().getBody() : null;
                        logger.info("Received message from {}: {}", from, text);

                        /**
                         * Mark message as read ---------------------------------
                         */
                        try {
                            sendMessageService.markMessageRead(value.getMetadata().getPhone_number_id(), message.getId());

                            // Save payload to DB in SystemActivityLog table
                            saveActivityLog(message, value);
                            
                        } catch (Exception e) {
                            logger.error("Error marking message as read or saving activity log: {}", e.getMessage(), e);
                        }

                        /**
                         * Manage inbound message to send response
                         */
                        try {
                            String phoneNumberId = value.getMetadata().getPhone_number_id();
                            ClientHandle handle = factory.getHandler(phoneNumberId);
                            if(handle!=null){
                                handle.handleInbondMessage(value.getMetadata(), message);
                            }
                            else{
                                logger.error("No handler found for phone number ID: {}", phoneNumberId);
                            }
                            
                        } catch (Exception e) {
                            logger.error("Error handling inbound message: {}", e.getMessage(), e);
                        }
                    }
                }
            }
        }
        return ResponseEntity.ok().build();
    }
    
    /**
     * Helper method to save activity log from incoming webhook message
     * 
     * @param message the incoming WhatsApp message
     * @param value the webhook value containing metadata
     */
    private void saveActivityLog(Message message, Value value) {
        try {
            SystemActivityLog activityLog = new SystemActivityLog();
            
            // Message details
            activityLog.setMessageId(message.getId());
            activityLog.setMessageFrom(message.getFrom());
            activityLog.setMessageTimestamp(message.getTimestamp());
            activityLog.setMessageType(message.getType());
            if (message.getText() != null) {
                activityLog.setMessageText(message.getText().getBody());
            }
            
            // Interactive details (if present)
            if (message.getInteractive() != null) {
                activityLog.setInteractiveType(message.getInteractive().getType());
                if (message.getInteractive().getButton_reply() != null) {
                    activityLog.setInteractiveButtonId(message.getInteractive().getButton_reply().getId());
                    activityLog.setInteractiveButtonTitle(message.getInteractive().getButton_reply().getTitle());
                }
            }
            
            // Contact details (if present)
            if (value.getContacts() != null && !value.getContacts().isEmpty()) {
                activityLog.setContactWaId(value.getContacts().get(0).getWa_id());
                activityLog.setContactProfileName(value.getContacts().get(0).getProfile().getName());
            }
            
            // Context details (if present)
            if (message.getContext() != null) {
                activityLog.setContextFrom(message.getContext().getFrom());
                activityLog.setContextId(message.getContext().getId());
            }
            
            // Metadata details
            if (value.getMetadata() != null) {
                activityLog.setMetadataDisplayPhonNumber(value.getMetadata().getDisplay_phone_number());
                activityLog.setMetadataPhoneNumberId(value.getMetadata().getPhone_number_id());
            }

            activityLog.setCreatedAt(new Date(System.currentTimeMillis()));
            activityLog.setCreatedBy("System");
            
            // Save to database
            systemActivityLogService.saveActivityLog(activityLog);
            
        } catch (Exception e) {
            logger.error("Error saving activity log: {}", e.getMessage(), e);
        }
    }
    
    /**
     * [SMART BUG FIX] Intelligently detects non-conversational messages (delivery/read receipts).
     * 
     * Detection Logic:
     * - Delivery/Read receipts have NO contacts in the webhook
     * - Real user messages ALWAYS have contacts
     * - If message has no text AND no interactive content AND no contacts = Receipt
     * 
     * @param message the WhatsApp message (Message object)
     * @param value the webhook value with contact info (Value object)
     * @return true if message is a non-conversational receipt
     */
    private boolean isNonConversationalReceipt(Object message, Object value) {
        try {
            // Safely cast and check using reflection to avoid classloader issues
            Message msg = (Message) message;
            Value val = (Value) value;
            
            // Check if message has actual conversational content
            boolean hasTextContent = msg.getText() != null && msg.getText().getBody() != null 
                    && !msg.getText().getBody().trim().isEmpty();
            
            boolean hasInteractiveContent = msg.getInteractive() != null 
                    && msg.getInteractive().getButton_reply() != null;
            
            // Check if contacts are present (indicates real message, not receipt)
            boolean hasContacts = val.getContacts() != null && !val.getContacts().isEmpty();
            
            // If no text, no interactive, and no contacts = it's a delivery/read receipt
            if (!hasTextContent && !hasInteractiveContent && !hasContacts) {
                logger.debug("Detected receipt: No text, no interactive content, no contacts");
                return true;
            }
            
            // If it has either text or interactive content AND has contacts = real message
            if ((hasTextContent || hasInteractiveContent) && hasContacts) {
                return false;
            }
            
            // Edge case: has content but no contacts (unusual but possible)
            if ((hasTextContent || hasInteractiveContent) && !hasContacts) {
                logger.warn("Unusual message: Has content but no contacts - processing anyway");
                return false;
            }
            
            // Default: treat as receipt
            return true;
        } catch (ClassCastException e) {
            logger.warn("Failed to process message detection: {}", e.getMessage());
            return true; // Fail safe - treat as receipt
        }
    }
}

