package com.radu.ChessTutor.chatBot.stockfish;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/stockfish")
public class StockfishController {

    private final StockfishService stockfishService;

    @Autowired
    public StockfishController(StockfishService stockfishService) {
        this.stockfishService = stockfishService;
    }

    @GetMapping("/evaluate")
    public MoveEvaluation evaluate(@RequestParam String fen) throws IOException {
        return stockfishService.evaluatePosition(fen);
    }
}