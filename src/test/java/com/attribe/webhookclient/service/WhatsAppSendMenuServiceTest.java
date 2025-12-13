package com.attribe.webhookclient.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.attribe.webhookclient.pojo.client.ClientDTO;
import com.attribe.webhookclient.pojo.client.MessageDTO;

@SpringBootTest
class WhatsAppSendMenuServiceTest {

	@Autowired
	private WhatsAppSendMenuService service;

	@MockBean
	private RestTemplate restTemplate;

	@MockBean
	private MessageTemplateService messageTemplateService;

	private ClientDTO testClient;
	private MessageDTO testMessage;

	@BeforeEach
	void setUp() {
		testClient = new ClientDTO();
		testClient.setPhoneNumberId("123456789");

		testMessage = new MessageDTO();
		testMessage.setTo("1234567890");
		testMessage.setType("interactive");
	}

	@Test
	void testSendMessageWithClientDTO_Success() throws Exception {
		// Arrange
		String expectedResponse = "{\"messages\":[{\"id\":\"wamid.123\"}]}";
		when(messageTemplateService.buildMenuMessagePayload(anyString())).thenReturn(java.util.Map.of());
		when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
				.thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

		// Act
		String result = service.sendMessage(testClient, testMessage);

		// Assert
		assertNotNull(result);
		assertEquals(expectedResponse, result);
		verify(restTemplate, times(1)).postForEntity(anyString(), any(), eq(String.class));
	}

	@Test
	void testSendMessageWithPhoneNumberId_Success() throws Exception {
		// Arrange
		String phoneNumberId = "123456789";
		String expectedResponse = "{\"messages\":[{\"id\":\"wamid.123\"}]}";
		when(messageTemplateService.buildMenuMessagePayload(anyString())).thenReturn(java.util.Map.of());
		when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
				.thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

		// Act
		String result = service.sendMessage(phoneNumberId, testMessage);

		// Assert
		assertNotNull(result);
		assertEquals(expectedResponse, result);
	}

	@Test
	void testSendMessage_ApiFailure() throws Exception {
		// Arrange
		String phoneNumberId = "123456789";
		when(messageTemplateService.buildMenuMessagePayload(anyString())).thenReturn(java.util.Map.of());
		when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
				.thenThrow(new RuntimeException("API Error"));

		// Act & Assert
		assertThrows(Exception.class, () -> service.sendMessage(phoneNumberId, testMessage));
	}

	@Test
	void testGetApiUrl() {
		// Act
		String url = service.sendMessage();

		// Assert
		assertNotNull(url);
		assertTrue(url.contains("graph.facebook.com"));
	}
}
