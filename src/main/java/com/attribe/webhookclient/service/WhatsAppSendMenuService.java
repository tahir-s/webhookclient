package com.attribe.webhookclient.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.attribe.webhookclient.pojo.client.ClientDTO;
import com.attribe.webhookclient.pojo.client.MessageDTO;

@Service
public class WhatsAppSendMenuService {
	
	@Value("${whatsapp.api.url}")
    private String apiUrl;


    String accessToken = "EAAhSNCaW3poBQDJQ5INiW1YmPJKkBOZBHQPmftCqDab1MNVl0z7b0wgt0vdVenhLv0RdFMQPpf2elEPWzwMz04vR3yYwpXAToewFq1lcpsxBuGVzBZBlKCtG1wr9SswET9wwD8GDTyJGXsspfimlNcVkLul1j46STn2p5YP3yuwhb0ZCJNKVIhXMSZBhV9F7hwZDZD";
		
	
	public String sendMessage() {
		
		return apiUrl;
	}
	
	public String sendMessage(ClientDTO cleint, MessageDTO message) throws Exception {
		return sendMessage(cleint.getPhoneNumberId(), message);
	
	}



    public String sendMessage(String phoneNumberId, MessageDTO message) throws Exception {
		
		
		RestTemplate restTemplate = new RestTemplate();
		String url = String.format("%s/%s/messages", apiUrl, phoneNumberId);

        // ---- HTTP Request ----
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        // ---- Payload of Response ----
        Map<String, Object> messagePayload = new HashMap<>();
        messagePayload.put("messaging_product", "whatsapp");
        messagePayload.put("to", message.getTo());
        messagePayload.put("type", "interactive"); // interactive / text 


        // ---- Building WhatsApp Interactive Button Message ----
        Map<String, Object> interactive = new HashMap<>();
        interactive.put("type", "button");

        //TODO --- HEADING of mwnu: Will make it dynamic in futre for DB/YML 
        Map<String, Object> body = new HashMap<>();
        body.put("text", "Welcome! Please choose an option:");

        //TODO --- BUTTONS of menu:  Will make it dynamic in futre for DB/YML 
        List<Map<String, Object>> buttons = new ArrayList<>();
        buttons.add(createButton("menu_1", "📦 Track Order"));
        buttons.add(createButton("menu_2", "🛍️ Shop Products"));
        buttons.add(createButton("menu_3", "📞 Contact Support"));

        Map<String, Object> action = new HashMap<>();
        action.put("buttons", buttons);
        interactive.put("body", body);

        //TODO --- FOOTER of menu:  Will make it dynamic in futre for DB/YML 
        Map<String, Object> footer = new HashMap<>();
        footer.put("text", "Powered by WhatsApp Attribe.AI");

         
        // ---- Assembing message---
        interactive.put("body", body);
        interactive.put("footer", footer);
        interactive.put("action", action);
        messagePayload.put("interactive", interactive);

        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(messagePayload, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        return response.getBody();
		
	
	}


    /**
     * This method us user for creating interactive menu.
     */
    private Map<String, Object> createButton(String id, String title) {
        Map<String, Object> reply = new HashMap<>();
        reply.put("id", id);
        reply.put("title", title);

        Map<String, Object> button = new HashMap<>();
        button.put("type", "reply");
        button.put("reply", reply);

        return button;

}

}
