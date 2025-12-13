package com.attribe.webhookclient.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

	private static final Logger logger = LoggerFactory.getLogger(WhatsAppSendMenuService.class);

	@Value("${whatsapp.api.url}")


    private String apiUrl;

	@Value("${whatsapp.token}")
	private String accessToken;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private MessageTemplateService messageTemplateService;

	/**
	 * Sends a menu message to a WhatsApp client
	 * @param client The client DTO containing phone number information
	 * @param message The message DTO
	 * @return API response body
	 * @throws Exception If message sending fails
	 */
	public String sendMessage(ClientDTO client, MessageDTO message) throws Exception {
		logger.info("Sending menu message to client: {}", client.getPhoneNumberId());
		return sendMessage(client.getPhoneNumberId(), message);
	}

	/**
	 * Sends a menu message to a specific phone number ID
	 * @param phoneNumberId WhatsApp Business Account Phone Number ID
	 * @param message The message DTO
	 * @return API response body
	 * @throws Exception If message sending fails
	 */
	public String sendMessage(String phoneNumberId, MessageDTO message) throws Exception {
		try {
			logger.debug("Building menu message payload for phone number ID: {}", phoneNumberId);

			String url = String.format("%s/%s/messages", apiUrl, phoneNumberId);
			logger.debug("WhatsApp API URL: {}", url);

			// Build HTTP headers
			HttpHeaders headers = new HttpHeaders();
			headers.setBearerAuth(accessToken);
			headers.setContentType(MediaType.APPLICATION_JSON);

			// Build message payload using MessageTemplateService
			Map<String, Object> messagePayload = messageTemplateService.buildMenuMessagePayload(message.getTo());

			// Create HTTP request entity
			HttpEntity<Map<String, Object>> request = new HttpEntity<>(messagePayload, headers);

			// Send request to WhatsApp API
			logger.info("Sending menu message to WhatsApp API");
			ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

			logger.info("Menu message sent successfully. Status: {}", response.getStatusCode());
			return response.getBody();

		} catch (Exception e) {
			logger.error("Failed to send menu message to phone number ID: {}", phoneNumberId, e);
			throw e;
		}
	}

	/**
	 * Returns the API URL (for testing purposes)
	 * @return The configured API URL
	 */
	public String sendMessage() {
		return apiUrl;
	}
}
