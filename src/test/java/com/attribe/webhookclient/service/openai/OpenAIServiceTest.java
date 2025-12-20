package com.attribe.webhookclient.service.openai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.attribe.webhookclient.pojo.openai.OpenAIResponse;

@ExtendWith(MockitoExtension.class)
@DisplayName("OpenAIService Unit Tests")
class OpenAIServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private OpenAIService openAIService;

    @BeforeEach
    void setUp() {
        openAIService = new OpenAIService(restTemplate);
        
        // Set properties using reflection
        ReflectionTestUtils.setField(openAIService, "apiKey", "test-api-key");
        ReflectionTestUtils.setField(openAIService, "apiUrl", "https://api.openai.com/v1/responses");
        ReflectionTestUtils.setField(openAIService, "model", "gpt-4.1-mini");
    }

    @Test
    @DisplayName("Should return response content when API call is successful")
    void testGetResponseSuccess() {
        // Arrange
        String userMessage = "Hello, how are you?";
        String expectedResponse = "I'm doing well, thank you for asking!";
        
        OpenAIResponse mockResponse = createMockResponse(expectedResponse);
        
        when(restTemplate.postForObject(
                anyString(),
                any(HttpEntity.class),
                eq(OpenAIResponse.class)
        )).thenReturn(mockResponse);

        // Act
        String result = openAIService.getResponse(userMessage);

        // Assert
        assertEquals(expectedResponse, result);
        verify(restTemplate, times(1)).postForObject(anyString(), any(HttpEntity.class), eq(OpenAIResponse.class));
    }

    @Test
    @DisplayName("Should throw exception when user message is null")
    void testGetResponseWithNullMessage() {
        // Act & Assert
        OpenAIException exception = assertThrows(OpenAIException.class, () -> {
            openAIService.getResponse(null);
        });
        
        assertEquals("User message cannot be null or empty", exception.getMessage());
        verify(restTemplate, never()).postForObject(anyString(), any(HttpEntity.class), any());
    }

    @Test
    @DisplayName("Should throw exception when user message is empty")
    void testGetResponseWithEmptyMessage() {
        // Act & Assert
        OpenAIException exception = assertThrows(OpenAIException.class, () -> {
            openAIService.getResponse("   ");
        });
        
        assertEquals("User message cannot be null or empty", exception.getMessage());
        verify(restTemplate, never()).postForObject(anyString(), any(HttpEntity.class), any());
    }

    @Test
    @DisplayName("Should throw exception when API key is not configured")
    void testGetResponseWithoutApiKey() {
        // Arrange
        ReflectionTestUtils.setField(openAIService, "apiKey", null);
        
        // Act & Assert
        OpenAIException exception = assertThrows(OpenAIException.class, () -> {
            openAIService.getResponse("Hello");
        });
        
        assertTrue(exception.getMessage().contains("API key is not configured"));
        verify(restTemplate, never()).postForObject(anyString(), any(HttpEntity.class), any());
    }

    @Test
    @DisplayName("Should throw exception when API key is empty")
    void testGetResponseWithEmptyApiKey() {
        // Arrange
        ReflectionTestUtils.setField(openAIService, "apiKey", "   ");
        
        // Act & Assert
        OpenAIException exception = assertThrows(OpenAIException.class, () -> {
            openAIService.getResponse("Hello");
        });
        
        assertTrue(exception.getMessage().contains("API key is not configured"));
    }

    @Test
    @DisplayName("Should throw exception when RestTemplate throws RestClientException")
    void testGetResponseWithRestClientException() {
        // Arrange
        String userMessage = "Hello";
        when(restTemplate.postForObject(
                anyString(),
                any(HttpEntity.class),
                eq(OpenAIResponse.class)
        )).thenThrow(new RestClientException("Connection timeout"));

        // Act & Assert
        OpenAIException exception = assertThrows(OpenAIException.class, () -> {
            openAIService.getResponse(userMessage);
        });
        
        assertTrue(exception.getMessage().contains("Failed to call OpenAI API"));
    }

    @Test
    @DisplayName("Should throw exception when API returns null response")
    void testGetResponseWithNullResponse() {
        // Arrange
        String userMessage = "Hello";
        when(restTemplate.postForObject(
                anyString(),
                any(HttpEntity.class),
                eq(OpenAIResponse.class)
        )).thenReturn(null);

        // Act & Assert
        OpenAIException exception = assertThrows(OpenAIException.class, () -> {
            openAIService.getResponse(userMessage);
        });
        
        assertEquals("OpenAI API returned null response", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when API returns null output")
    void testGetResponseWithNullOutput() {
        // Arrange
        String userMessage = "Hello";
        OpenAIResponse mockResponse = new OpenAIResponse();
        mockResponse.setId("response-1");
        mockResponse.setOutput(null);
        
        when(restTemplate.postForObject(
                anyString(),
                any(HttpEntity.class),
                eq(OpenAIResponse.class)
        )).thenReturn(mockResponse);

        // Act & Assert
        OpenAIException exception = assertThrows(OpenAIException.class, () -> {
            openAIService.getResponse(userMessage);
        });
        
        assertEquals("OpenAI API returned null or empty output", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when API returns empty output array")
    void testGetResponseWithEmptyOutputArray() {
        // Arrange
        String userMessage = "Hello";
        OpenAIResponse mockResponse = new OpenAIResponse();
        mockResponse.setId("response-1");
        mockResponse.setOutput(java.util.Arrays.asList());
        
        when(restTemplate.postForObject(
                anyString(),
                any(HttpEntity.class),
                eq(OpenAIResponse.class)
        )).thenReturn(mockResponse);

        // Act & Assert
        OpenAIException exception = assertThrows(OpenAIException.class, () -> {
            openAIService.getResponse(userMessage);
        });
        
        assertEquals("OpenAI API returned null or empty output", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when API returns empty response text")
    void testGetResponseWithEmptyResponseText() {
        // Arrange
        String userMessage = "Hello";
        OpenAIResponse mockResponse = new OpenAIResponse();
        mockResponse.setId("response-1");
        java.util.Map<String, Object> emptyOutput = new java.util.HashMap<>();
        emptyOutput.put("text", "   ");
        mockResponse.setOutput(java.util.Arrays.asList(emptyOutput));
        
        when(restTemplate.postForObject(
                anyString(),
                any(HttpEntity.class),
                eq(OpenAIResponse.class)
        )).thenReturn(mockResponse);

        // Act & Assert
        OpenAIException exception = assertThrows(OpenAIException.class, () -> {
            openAIService.getResponse(userMessage);
        });
        
        assertEquals("OpenAI API returned empty message content", exception.getMessage());
    }

    @Test
    @DisplayName("Should trim whitespace from response content")
    void testGetResponseTrimsWhitespace() {
        // Arrange
        String userMessage = "Hello";
        String responseWithWhitespace = "  Response text with spaces  ";
        String expectedTrimmed = "Response text with spaces";
        
        OpenAIResponse mockResponse = new OpenAIResponse();
        mockResponse.setId("response-1");
        java.util.Map<String, Object> output = new java.util.HashMap<>();
        output.put("text", responseWithWhitespace);
        mockResponse.setOutput(java.util.Arrays.asList(output));
        
        when(restTemplate.postForObject(
                anyString(),
                any(HttpEntity.class),
                eq(OpenAIResponse.class)
        )).thenReturn(mockResponse);

        // Act
        String result = openAIService.getResponse(userMessage);

        // Assert
        assertEquals(expectedTrimmed, result);
    }

    @Test
    @DisplayName("Should handle multiline response content")
    void testGetResponseWithMultilineContent() {
        // Arrange
        String userMessage = "What is Java?";
        String multilineResponse = "Java is a programming language.\nIt is object-oriented.\nIt is widely used.";
        
        OpenAIResponse mockResponse = createMockResponse(multilineResponse);
        
        when(restTemplate.postForObject(
                anyString(),
                any(HttpEntity.class),
                eq(OpenAIResponse.class)
        )).thenReturn(mockResponse);

        // Act
        String result = openAIService.getResponse(userMessage);

        // Assert
        assertEquals(multilineResponse, result);
    }

    @Test
    @DisplayName("Should throw exception with cause information on unexpected error")
    void testGetResponseWithUnexpectedException() {
        // Arrange
        String userMessage = "Hello";
        RuntimeException cause = new RuntimeException("Unexpected error");
        
        when(restTemplate.postForObject(
                anyString(),
                any(HttpEntity.class),
                eq(OpenAIResponse.class)
        )).thenThrow(cause);

        // Act & Assert
        OpenAIException exception = assertThrows(OpenAIException.class, () -> {
            openAIService.getResponse(userMessage);
        });
        
        assertTrue(exception.getMessage().contains("Unexpected error while processing"));
        assertEquals(cause, exception.getCause());
    }

    // Helper method to create a mock OpenAI response
    private OpenAIResponse createMockResponse(String responseText) {
        OpenAIResponse response = new OpenAIResponse();
        response.setId("chatcmpl-123");
        response.setObject("text_completion");
        response.setCreated(System.currentTimeMillis() / 1000);
        response.setModel("gpt-4.1-mini");
        
        // Create output as a list of maps (objects with "text" field)
        java.util.Map<String, Object> outputObject = new java.util.HashMap<>();
        outputObject.put("text", responseText);
        response.setOutput(java.util.Arrays.asList(outputObject));
        
        return response;
    }
}
