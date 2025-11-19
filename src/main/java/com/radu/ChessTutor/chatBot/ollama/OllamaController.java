package com.radu.ChessTutor.chatBot.ollama;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ollama")
@CrossOrigin(origins = "http://localhost:5173")
public class OllamaController {

    private final OllamaService ollamaService;

    public OllamaController(OllamaService ollamaService) {
        this.ollamaService = ollamaService;
    }

    @PostMapping("/query")
    public String queryModel(@RequestBody String userPrompt) {
        return ollamaService.askModel(userPrompt);
    }
}
