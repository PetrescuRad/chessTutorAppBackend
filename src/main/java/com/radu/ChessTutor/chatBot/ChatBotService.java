package com.radu.ChessTutor.chatBot;

import com.radu.ChessTutor.chatBot.ollama.OllamaService;
import com.radu.ChessTutor.chatBot.stockfish.StockfishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
@Service
public class ChatBotService {

    private final StockfishService stockfishService;

    private final OllamaService ollamaService;

    @Autowired
    public ChatBotService(StockfishService stockfishService, OllamaService ollamaService) {
        this.stockfishService = stockfishService;
        this.ollamaService = ollamaService;
    }

    public String chatWithBot(String userMessage, String fen) throws IOException {
        String bestMove = String.valueOf(stockfishService.evaluatePosition(fen));

        System.out.println(bestMove);

        String prompt = String.format("""
            You are a helpful chess assistant.
            The user said: "%s"
            Current position (FEN): %s
            Stockfish recommends this as next move: %s.
            Explain this move in a friendly, educational way, only using the input from Stockfish. Do not analyse the position yourself.
        """, userMessage, fen, bestMove);

        return ollamaService.askModel(prompt);
    }
}
