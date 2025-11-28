package by.mcsaltine.vkpost.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class VkService {

    private static final Logger logger = LoggerFactory.getLogger(VkService.class);

    @Value("${vk.access.token}")
    private String accessToken;

    private final String GROUP_ID = "2608975";
    private final String API_VERSION = "5.199";
    private final String VK_API_URL = "https://api.vk.com/method/wall.get";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public VkService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public JsonNode fetchPosts() throws Exception {
        String url = String.format("%s?owner_id=-%s&count=10&extended=1&access_token=%s&v=%s",
                VK_API_URL, GROUP_ID, accessToken, API_VERSION);

        try {
            logger.debug("Sending request to VK API: {}", url);
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            logger.debug("Received response: {}", response.getBody());

            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            if (jsonNode.has("error")) {
                JsonNode error = jsonNode.get("error");
                String errorMessage = "VK API Error " + error.get("error_code").asText() + ": " + error.get("error_msg").asText();
                logger.error(errorMessage);
                throw new Exception(errorMessage);
            }

            return jsonNode;
        } catch (Exception e) {
            logger.error("Failed to fetch posts from VK API", e);
            throw new Exception("Failed to fetch posts: " + e.getMessage(), e);
        }
    }


}