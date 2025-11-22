package com.attribe.webhookclient.controller;

import java.util.Map;

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

@RestController
@RequestMapping("/webhook")
public class WhatsAppWebhookController {
    private final String VERIFY_TOKEN = "EAAUnvBJjQBsBP5u5ZB74QG8zoouX7ZBaAAwaZC9UNhFuIZA4Q2qIdehddQVWguHhZAyqPLxJP9y6VTnrWnkccrt6BcBdfgkKZBgTQK7x6cPdsM5nEraWh7btrVNsyu9XgBFNT7HwUvoistOkFoCinzYe17jFxmdq8SsZCZBUwGA8DMtmn1mqLU0hh6MuqxZBT1FUMfJ2LmZBy4FKannw31gpNfhqFU6515Cfc5of1NTMxIW2k5"; // your custom token
    
    // STEP 1: Verification Endpoint
    @GetMapping()
    public ResponseEntity<String> verifyWebhook(
            @RequestParam(name = "hub.mode", required = false) String mode,
            @RequestParam(name = "hub.challenge", required = false) String challenge,
            @RequestParam(name = "hub.verify_token", required = false) String token) {

        if ("subscribe".equals(mode) && VERIFY_TOKEN.equals(token)) {
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
    
    @PostMapping
    public ResponseEntity<Void> receiveWebhook(@RequestBody WebhookPayload payload) {
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
                        }
                    }
                }
            }
        }
        return ResponseEntity.ok().build();
    }
}

