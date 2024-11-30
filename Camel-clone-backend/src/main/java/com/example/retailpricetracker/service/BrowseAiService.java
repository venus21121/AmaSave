package com.example.retailpricetracker.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;

@Service

public class BrowseAiService {
    @Value("${browseai.api.key}")
    private String apiKey;

    @Value("${browseai.robot.id}")
    private String robotId;

    private final String baseUrl = "https://api.browse.ai/v2/robots";


    // Connect with BrowseAI to run scraping task
    @Async
    public void runRobot(Map<String, Object> inputParams) {
        try {
            String url = baseUrl + "/" + robotId + "/tasks";

            // Set the headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            headers.set("Content-Type", "application/json");

            // Set the body (input parameters for the robot)
            Map<String, Object> body = new HashMap<>();
            body.put("inputParameters", inputParams);

            // Create an HTTP entity
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            // Use RestTemplate to make the POST request
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

            System.out.println("Robot task triggered successfully.");
            System.out.println("Response Status Code: " + response.getStatusCode());
            System.out.println("Response Body: " + response.getBody());

        } catch (Exception e) {
            System.err.println("Error occurred while running the robot: " + e.getMessage());
        }
    }
}
