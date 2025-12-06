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


    String accessToken = "EAAhSNCaW3poBQM8Knp2AUuSduSj0QjwQ74NcD5UCZBs3CZB1UhDbsQDg9xnotorY7iqzYsCT38X7PinatB3CxZCGPC0FZAG7gyGu0vC89ruPvvhbRHJgEmTGTIwoOeEM1spgwgrZAxTUQrTqus4N5VpY5367TIIkgw2EDZCYGa4uRRnCgEvx25IswHbKvJILRL5YKY3KVWAZAG6ZBGUcItQTyZAU1iBdCZBncJ1F44KYNbhK3fPmhyBOm3G8M7QhVCSm7rcVgDn8f7QPPBxFmpbujZC5wZDZD";
		
	
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
