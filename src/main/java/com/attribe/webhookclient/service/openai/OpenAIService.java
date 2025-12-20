package com.attribe.webhookclient.service.openai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.attribe.webhookclient.pojo.openai.OpenAIRequest;
import com.attribe.webhookclient.pojo.openai.OpenAIResponse;

/**
 * Service for integrating with OpenAI Responses API
 */
@Service
public class OpenAIService {
    private static final Logger logger = LoggerFactory.getLogger(OpenAIService.class);

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.url}")
    private String apiUrl;

    @Value("${openai.model:gpt-4.1-mini}")
    private String model;

    private final RestTemplate restTemplate;

    public OpenAIService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Send a message to OpenAI Responses API and get the response
     *
     * @param userMessage The user's message to send to OpenAI
     * @return The response message from OpenAI
     * @throws OpenAIException if API call fails or response is invalid
     */
    public String getResponse(String userMessage) {
        validateInputs(userMessage);
        validateApiKey();

        try {
            logger.info("Sending request to OpenAI API with model: {}", model);

            // Create the request object (without max_tokens as Responses API doesn't support it)
            OpenAIRequest request = new OpenAIRequest(model, userMessage);

            // Prepare headers
            HttpHeaders headers = prepareHeaders();

            // Create HTTP entity
            HttpEntity<OpenAIRequest> entity = new HttpEntity<>(request, headers);

            // Make API call
            OpenAIResponse response = restTemplate.postForObject(apiUrl, entity, OpenAIResponse.class);

            // Extract and return the response
            return extractResponseContent(response);

        } catch (RestClientException e) {
            String errorMsg = "Failed to call OpenAI API: " + e.getMessage();
            logger.error(errorMsg, e);
            throw new OpenAIException(errorMsg, e);
        } catch (OpenAIException e) {
            logger.error("OpenAI validation error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            String errorMsg = "Unexpected error while processing OpenAI response: " + e.getMessage();
            logger.error(errorMsg, e);
            throw new OpenAIException(errorMsg, e);
        }
    }

    /**
     * Validate that the input message is not null or empty
     *
     * @param userMessage The user message to validate
     * @throws OpenAIException if message is null or empty
     */
    private void validateInputs(String userMessage) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            throw new OpenAIException("User message cannot be null or empty");
        }
    }

    /**
     * Prepare HTTP headers for OpenAI API request
     *
     * @return HttpHeaders with authorization and content type
     */
    private HttpHeaders prepareHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);
        return headers;
    }

    /**
     * Extract the response content from OpenAI response object
     *
     * @param response The OpenAI response object
     * @return The extracted message content
     * @throws OpenAIException if response structure is invalid
     */
    private String extractResponseContent(OpenAIResponse response) {
        if (response == null) {
            throw new OpenAIException("OpenAI API returned null response");
        }

        if (response.getOutput() == null || response.getOutput().isEmpty()) {
            throw new OpenAIException("OpenAI API returned null or empty output");
        }

        // Output is a list of objects, get the first one
        Object outputObject = response.getOutput().get(0);
        if (outputObject == null) {
            throw new OpenAIException("OpenAI API returned null output object");
        }

        String content;
        if (outputObject instanceof String) {
            // Handle case where output is a string
            content = (String) outputObject;
        } else if (outputObject instanceof java.util.Map) {
            // Handle case where output is an object/map - try multiple field names
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> outputMap = (java.util.Map<String, Object>) outputObject;
            
            logger.debug("Output object keys: {}", outputMap.keySet());
            
            // Try different possible field names
            Object textObject = outputMap.get("text");
            if (textObject == null) {
                textObject = outputMap.get("message");
            }
            if (textObject == null) {
                textObject = outputMap.get("content");
            }
            if (textObject == null) {
                textObject = outputMap.get("response");
            }
            
            if (textObject == null) {
                logger.error("Output map structure: {}", outputMap);
                throw new OpenAIException("OpenAI API response object does not contain expected text field. Available fields: " + outputMap.keySet());
            }
            content = textObject.toString();
        } else {
            content = outputObject.toString();
        }

        if (content == null || content.trim().isEmpty()) {
            throw new OpenAIException("OpenAI API returned empty message content");
        }

        logger.info("Successfully received response from OpenAI API");
        return content.trim();
    }

    /**
     * Validate that API key is configured
     *
     * @throws OpenAIException if API key is not set
     */
    private void validateApiKey() {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new OpenAIException("OpenAI API key is not configured. Set the OPENAI_API_KEY environment variable.");
        }
    }
}
