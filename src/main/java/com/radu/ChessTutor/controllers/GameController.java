package com.radu.ChessTutor.controllers;

import com.radu.ChessTutor.entities.ChessGame;
import com.radu.ChessTutor.services.ChessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/games")
public class GameController {
    private final ChessService chessService;

    @Autowired
    public GameController(ChessService chessService) {
        this.chessService = chessService;
    }

    @PostMapping("/{username}/sync/all")
    public ResponseEntity<String> syncAllGames(@PathVariable String username) {
        chessService.fetchAndInsertAllGames(username);
        return ResponseEntity.ok("All games synced successfully for " + username);
    }

    @PostMapping("/{username}/sync")
    public ResponseEntity<String> syncGames(
            @PathVariable String username,
            @RequestParam int year,
            @RequestParam int month
    ) {
        chessService.fetchAndInsertGames(username, year, month);
        return ResponseEntity.ok(
                String.format("Synced games for %s - %d/%02d", username, year, month)
        );
    }

    @GetMapping("/{username}")
    public ResponseEntity<List<ChessGame>> getAllGamesByUser(@PathVariable String username) {
        return ResponseEntity.ok(chessService.getAllGamesByUser(username));
    }

    @GetMapping("/{username}/{gameID}")
    public ResponseEntity<ChessGame> getGameByUrl(
            @PathVariable String username,
            @PathVariable String gameID
    ) {
        String url = "https://www.chess.com/game/live/" + gameID;
        return ResponseEntity.ok(chessService.getGameByUrl(username, url));
    }

    @GetMapping("/{username}/filter")
    public ResponseEntity<List<ChessGame>> getGamesByDateRange(
            @PathVariable String username,
            @RequestParam(required = false) Integer startYear,
            @RequestParam(required = false) Integer endYear,
            @RequestParam(required = false) Integer startMonth,
            @RequestParam(required = false) Integer endMonth
    ) {
        // Implement filtering logic
        return ResponseEntity.ok(chessService.getGamesByDateRange(username, startYear, endYear, startMonth, endMonth));
    }
}
