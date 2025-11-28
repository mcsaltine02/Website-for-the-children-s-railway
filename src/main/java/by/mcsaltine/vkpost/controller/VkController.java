package by.mcsaltine.vkpost.controller;

import com.fasterxml.jackson.databind.JsonNode;
import by.mcsaltine.vkpost.service.VkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VkController {

    private static final Logger logger = LoggerFactory.getLogger(VkController.class);

    private final VkService vkService;

    public VkController(VkService vkService) {
        this.vkService = vkService;
    }

    @GetMapping("/vk-proxy")
    public ResponseEntity<?> getVkPosts() {
        try {
            JsonNode response = vkService.fetchPosts();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error in /vk-proxy endpoint", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching VK posts: " + e.getMessage());
        }
    }

}