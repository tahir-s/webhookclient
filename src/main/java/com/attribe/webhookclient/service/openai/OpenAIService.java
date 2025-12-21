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
            
            logger.info("Raw response received: {}", response);
            logger.info("Response output type: {}", response.getOutput().getClass().getName());
            if (!response.getOutput().isEmpty()) {
                logger.info("First output item type: {}", response.getOutput().get(0).getClass().getName());
                logger.info("First output item value: {}", response.getOutput().get(0));
            }

            // Extract and return the response
            String result = extractResponseContent(response);
            logger.info("Final extracted response: {}", result);
            return result;

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

        String content = null;
        
        logger.debug("Processing output object of type: {}", outputObject.getClass().getSimpleName());
        
        if (outputObject instanceof String) {
            // Handle case where output is a string
            content = (String) outputObject;
            logger.debug("Output is String: {}", content);
        } else if (outputObject instanceof java.util.Map) {
            // Handle case where output is an object/map
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> outputMap = (java.util.Map<String, Object>) outputObject;
            
            logger.debug("Output is Map with keys: {}", outputMap.keySet());
            
            // Try different possible field names - 'text' should be the main field for output_text type
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
                logger.error("Could not find text field in output map: {}", outputMap);
                throw new OpenAIException("OpenAI API response object does not contain expected text field. Available fields: " + outputMap.keySet());
            }
            
            logger.debug("Extracted text object type: {}, value: {}", textObject.getClass().getSimpleName(), textObject);
            
            // Ensure we get the string value, not the object representation
            if (textObject instanceof String) {
                content = (String) textObject;
            } else {
                content = textObject.toString();
            }
        } else {
            logger.warn("Output is unexpected type: {}", outputObject.getClass().getName());
            content = outputObject.toString();
        }

        // Safety check: if we somehow got the full object representation, extract just the text
        if (content != null && content.startsWith("[{")) {
            logger.warn("Content appears to be object representation, extracting text field: {}", content);
            // Try to extract text from pattern like [{...text=Hello...}]
            int textStart = content.indexOf("text=");
            if (textStart != -1) {
                int textEnd = content.indexOf('}', textStart);
                if (textEnd != -1) {
                    content = content.substring(textStart + 5, textEnd).trim();
                    logger.info("Extracted text from object representation: {}", content);
                }
            }
        }

        if (content == null || content.trim().isEmpty()) {
            throw new OpenAIException("OpenAI API returned empty message content");
        }

        logger.info("Successfully extracted response content: {}", content);
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
