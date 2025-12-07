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


    String accessToken = "EAAhSNCaW3poBQDJQ5INiW1YmPJKkBOZBHQPmftCqDab1MNVl0z7b0wgt0vdVenhLv0RdFMQPpf2elEPWzwMz04vR3yYwpXAToewFq1lcpsxBuGVzBZBlKCtG1wr9SswET9wwD8GDTyJGXsspfimlNcVkLul1j46STn2p5YP3yuwhb0ZCJNKVIhXMSZBhV9F7hwZDZD";
		
	
	public String sendMessage() {
		
		return apiUrl;
	}
	
	public String sendMessage(ClientDTO cleint, MessageDTO message) throws Exception {
		return sendMessage(cleint.getPhoneNumberId(), message);
	
	}



    public String sendMessage(String phoneNumberId, MessageDTO message) throws Exception {
		System.out.println("WhatsAppSendMessageService.sendMessage -------------------- [START]");
		
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
       
       // System.out.println(messagePayload.toString());
       System.out.println("WhatsAppSendMessageService.sendMessage -------------------- [END]");
		
        return response.getBody();
		
	
	}


}
