package com.nilecare.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    //@Value("${google.gemini.api.key}")
    private String apiKey = "AIzaSyD8ZKa-cJuxB1jXQdq6AoUYvUEgPDXoHW4";

    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";

    public String getAiResponse(String userMessage) {
        try {
            // --- DEBUG PRINT START ---
            System.out.println("========================================");
            System.out.println("DEBUG: The code is running!");
            System.out.println("DEBUG: API Key is: " + apiKey);
            System.out.println("DEBUG: Target URL is: " + API_URL + apiKey);
            System.out.println("========================================");
            // --- DEBUG PRINT END ---

            // 1. Check if key is loaded
            if (apiKey == null || apiKey.isEmpty()) {
                return "Error: API Key is missing";
            }

            RestTemplate restTemplate = new RestTemplate();
            String finalUrl = API_URL + apiKey;
            
            // ... rest of your code ...

            // 2. Construct the Request Body matching Gemini's requirements
            Map<String, Object> part = new HashMap<>();
            part.put("text", "You are a mental health assistant named NileCare. Be empathetic, short, and helpful. User says: " + userMessage);

            Map<String, Object> content = new HashMap<>();
            content.put("parts", List.of(part));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", List.of(content));

            // 3. Set Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // 4. Make the Call
            ResponseEntity<Map> response = restTemplate.postForEntity(finalUrl, entity, Map.class);

            // 5. Parse the response
            return extractTextFromResponse(response.getBody());

        } catch (Exception e) {
            e.printStackTrace();
            return "I am having trouble connecting to my brain right now. (Error: " + e.getMessage() + ")";
        }
    }

    @SuppressWarnings("unchecked")
    private String extractTextFromResponse(Map responseBody) {
        try {
            if (responseBody == null) return "No response from AI.";

            // Navigate the JSON: candidates -> [0] -> content -> parts -> [0] -> text
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseBody.get("candidates");
            if (candidates == null || candidates.isEmpty()) {
                return "I'm sorry, I don't know what to say to that.";
            }

            Map<String, Object> firstCandidate = candidates.get(0);
            Map<String, Object> content = (Map<String, Object>) firstCandidate.get("content");
            if (content == null) return "I'm sorry, I don't know what to say to that.";

            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            if (parts == null || parts.isEmpty()) {
                return "I'm sorry, I don't know what to say to that.";
            }

            return (String) parts.get(0).get("text");

        } catch (Exception e) {
            return "Sorry, I couldn't understand that.";
        }
    }
}