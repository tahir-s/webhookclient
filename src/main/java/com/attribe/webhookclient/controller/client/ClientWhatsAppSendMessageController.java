package com.attribe.webhookclient.controller.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.attribe.webhookclient.pojo.client.ClientDTO;
import com.attribe.webhookclient.pojo.client.MessageDTO;
import com.attribe.webhookclient.service.WhatsAppSendMessageService;
import com.attribe.webhookclient.service.client.ClientSerivces;

import jakarta.validation.constraints.Size;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/clienthook/v1.0")
@Validated
public class ClientWhatsAppSendMessageController {
	
	@Autowired
	private WhatsAppSendMessageService messageService;
	
	@Autowired
	private ClientSerivces clientService;	
	
	//TODO make this API secure by implementing oath 2.0 
	@PostMapping("/{clientId}/message")
	public ResponseEntity<String>  sendMessage(
			@PathVariable @Size(min = 3, max = 100, message = "client ID must be between 3 and 100 characters") String clientId
			, @RequestBody MessageDTO message ) {
		String response="ok";
		
		ClientDTO client = null;
		

		try {
			client = clientService.getClientByClientId(clientId);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Client ID not found or deavtivted, please contact with Admin. Detials:" + e.getMessage());
		}
		
		/**
		 * Sending message 
		 */
		try {
			response = messageService.sendMessage(client, message);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unable to send message, please contact with Admin: Detials:" + e.getMessage());
		}

		return ResponseEntity.ok(response);
	}
	
	@GetMapping()
	public String get() {
		return "testing";
	}

	@GetMapping("/{clientId}/message")
	public ResponseEntity<String>  getMethodName(
			@PathVariable @Size(min = 3, max = 100, message = "client ID must be between 3 and 100 characters") String clientId) {
		String response="ok";
		
		ClientDTO client = null;
		
		try {
			 client = clientService.getClientByClientId(clientId);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Client ID not found or deavtivted, please contact with Admin");
		}
		
		/**
		 * Sending message 
		 */
		try {
			MessageDTO messageDto= new MessageDTO();
			messageDto.setTo("923007085369");
			messageDto.setBody("This message is coming form Attribe ClinetHook via an API");
			
			response = messageService.sendMessage(client, messageDto);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unable to send message, please contact with Admin");
		}
		 
		return ResponseEntity.ok(response);
	}	
}
