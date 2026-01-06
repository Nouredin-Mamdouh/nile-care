package com.nilecare.controller;

import com.nilecare.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class ChatController {

    @Autowired
    private GeminiService geminiService;

    // Render the HTML page
    @GetMapping("/chat")
    public String chatPage() {
        return "student/chatbot";
    }

    // API called by JavaScript
    @PostMapping("/api/chat/send")
    @ResponseBody
    public ResponseEntity<?> sendMessage(@RequestBody Map<String, String> payload) {
        String userMessage = payload.get("message");
        
        // Call Google AI
        String aiResponse = geminiService.getAiResponse(userMessage);

        return ResponseEntity.ok(Map.of(
            "response", aiResponse
        ));
    }
}