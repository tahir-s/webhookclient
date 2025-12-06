package com.attribe.webhookclient.service;

import java.util.HashMap;
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
public class WhatsAppSendMessageService {
	
	@Value("${whatsapp.api.url}")
    private String apiUrl;


    String accessToken = "EAAhSNCaW3poBQKBfDFWZAo2HkEZCkldxwA6Orun8IS7PU8aSlsUXKqxaYEziTFdT5xgsZBRNHFZBmsaSxjatDBfOiP6mIGVBFu17CEK2rBZCDSjbFRHR3IjyaE0pRDmY5Fm3n1Tk1BbSMfOQvQLWUnDsosJuayjZCkVQzG3bDdbGXySTiUbtBQR5XngYBmOvVoOu7biJiwNtR82hhYpPOF4jOVm0BwWgjhrgLmmP7ryvJ5ZCn6Gq9XLgJAaR2CEono8ZCZCYumxzouZCXiUCElCeNAXAZDZD";
		
	
	public String sendMessage() {
		
		return apiUrl;
	}
	
	public String sendMessage(ClientDTO cleint, MessageDTO message) throws Exception {
		return sendMessage(cleint.getPhoneNumberId(), message);
	
	}



    public String sendMessage(String phoneNumberId, MessageDTO message) throws Exception {
		
		
		RestTemplate restTemplate = new RestTemplate();
		String url = String.format("%s/%s/messages", apiUrl, phoneNumberId);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        
        Map<String, Object> messagePayload = new HashMap<>();
        messagePayload.put("messaging_product", "whatsapp");
        messagePayload.put("to", message.getTo());
        messagePayload.put("type", "text");
        
        
        Map<String, String> text = new HashMap<>();
        text.put("body", message.getBody());
        messagePayload.put("text", text);
        
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(messagePayload, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        return response.getBody();
		
	
	}


}
