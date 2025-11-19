package com.radu.ChessTutor.chatBot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/chatbot")
@CrossOrigin(origins = "http://localhost:5173")
public class ChatBotController {
    private final ChatBotService chatBotService;

    @Autowired
    public ChatBotController(ChatBotService chatBotService) {
        this.chatBotService = chatBotService;
    }

    @PostMapping("/ask")
    public ResponseEntity<?> chat(@RequestBody ChatRequest req) {
        try {
            String reply = chatBotService.chatWithBot(req.getMessage(), req.getFen());
            return ResponseEntity.ok(reply);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to process chat request: " + e.getMessage());
        }
    }

    public static class ChatRequest {
        private String message;
        private String fen;

        public ChatRequest() {
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getFen() {
            return fen;
        }

        public void setFen(String fen) {
            this.fen = fen;
        }
    }
}
