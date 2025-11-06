package com.radu.ChessTutor.controllers;

import com.radu.ChessTutor.entities.ChessGame;
import com.radu.ChessTutor.exceptions.ApiException;
import com.radu.ChessTutor.services.ChessService;
import com.radu.ChessTutor.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/games")
@CrossOrigin("http://localhost:5173/")
public class GameController {
    private final ChessService chessService;
    private final JwtService jwtService;

    @Autowired
    public GameController(ChessService chessService, JwtService jwtService) {
        this.chessService = chessService;
        this.jwtService = jwtService;
    }

    @PostMapping("/{username}/sync/all")
    public ResponseEntity<String> syncAllGames(@PathVariable String username, @RequestHeader("Authorization") String authHeader) {
        validateToken(authHeader);
        chessService.fetchAndInsertAllGames(username);
        return ResponseEntity.ok("All games synced successfully for " + username);
    }

    @PostMapping("/{username}/sync")
    public ResponseEntity<String> syncGames(
            @PathVariable String username,
            @RequestParam int year,
            @RequestParam int month,
            @RequestHeader("Authorization") String authHeader
    ) {
        chessService.fetchAndInsertGames(username, year, month);
        return ResponseEntity.ok(
                String.format("Synced games for %s - %d/%02d", username, year, month)
        );
    }

    @GetMapping("/{username}")
    public ResponseEntity<List<ChessGame>> getAllGamesByUser(@PathVariable String username , @RequestHeader("Authorization") String authHeader) {
        validateToken(authHeader);
        return ResponseEntity.ok(chessService.getAllGamesByUser(username));
    }

    @GetMapping("/{username}/{gameID}")
    public ResponseEntity<ChessGame> getGameByUrl(
            @PathVariable String username,
            @PathVariable String gameID,
            @RequestHeader("Authorization") String authHeader
    ) {
        validateToken(authHeader);
        String url = "https://www.chess.com/game/live/" + gameID;
        return ResponseEntity.ok(chessService.getGameByUrl(username, url));
    }

    @GetMapping("/{username}/filter")
    public ResponseEntity<List<ChessGame>> getGamesByDateRange(
            @PathVariable String username,
            @RequestParam(required = false) Integer startYear,
            @RequestParam(required = false) Integer endYear,
            @RequestParam(required = false) Integer startMonth,
            @RequestParam(required = false) Integer endMonth,
            @RequestHeader("Authorization") String authHeader
    ) {
        validateToken(authHeader);
        // Implement filtering logic
        return ResponseEntity.ok(chessService.getGamesByDateRange(username, startYear, endYear, startMonth, endMonth));
    }

    // Helper methods
    private void validateToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Missing or invalid token");
        }

        String token = authHeader.substring(7);
        if (!jwtService.isTokenValid(token, jwtService.extractEmail(token))) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }
    }
}
