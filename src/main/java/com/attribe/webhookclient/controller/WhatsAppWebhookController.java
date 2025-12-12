package com.attribe.webhookclient.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.attribe.webhookclient.pojo.whatsapp.Change;
import com.attribe.webhookclient.pojo.whatsapp.Entry;
import com.attribe.webhookclient.pojo.whatsapp.Message;
import com.attribe.webhookclient.pojo.whatsapp.Value;
import com.attribe.webhookclient.pojo.whatsapp.WebhookPayload;
import com.attribe.webhookclient.service.WhatsAppSendMessageService;
import com.attribe.webhookclient.service.handle.ClientHandle;
import com.attribe.webhookclient.service.handle.ClientHandlerFactory;

@RestController
@RequestMapping("/webhook")
public class WhatsAppWebhookController {
    
    @org.springframework.beans.factory.annotation.Value("${whatsapp.token}")
    private String whatsappToken;
    
    @Autowired
    private ClientHandlerFactory factory;

    @Autowired
    private WhatsAppSendMessageService sendMessageService;
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
        System.out.println("Received Webhook Event:");
        System.out.println(payload);
        return ResponseEntity.ok().build(); // Respond with 200 OK
    }
    
    @PostMapping()
    public ResponseEntity<Void> receiveWebhook(@RequestBody WebhookPayload payload) {
    	
    	// Validate payload
    	if (payload == null || payload.getObject() == null || payload.getEntry() == null) {
    		return ResponseEntity.badRequest().build();
    	}
    	
    	System.out.println("Received Webhook Event:");
    	System.out.println(payload.toString());
        if ("whatsapp_business_account".equals(payload.getObject())) {
            for (Entry entry : payload.getEntry()) {
                for (Change change : entry.getChanges()) {
                    Value value = change.getValue();
                    if (value.getMessages() != null) {
                        for (Message message : value.getMessages()) {
                            String from = message.getFrom();
                            String text = message.getText() != null ? message.getText().getBody() : null;
                            System.out.println("Received message from " + from + ": " + text);

                            /**
                             * Mark messae a read ---------------------------------
                             */
                            try {
                                sendMessageService.markMessageRead(value.getMetadata().getPhone_number_id(), message.getId());
                                
                            } catch (Exception e) {
                            }

                            /**
                             * Manange in bond message to send response
                             */
                            try {
                                
                                ClientHandle handle = factory.getHandler("OfspHandler");
                                if(handle!=null){
                                    handle.handleInbondMessage(value.getMetadata(), message);
                                }
                                
                            } catch (Exception e) {
                                System.err.println(e.getMessage());
                            }



                        }
                    }
                }
            }
        }
        return ResponseEntity.ok().build();
    }
}

