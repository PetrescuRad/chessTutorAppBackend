package com.radu.ChessTutor.services;

import com.radu.ChessTutor.DTOs.ChessApiResponse;
import com.radu.ChessTutor.entities.ChessGame;
import com.radu.ChessTutor.exceptions.ApiException;
import com.radu.ChessTutor.repositories.ChessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.*;
import java.util.List;
import java.util.Map;

@Service
public class ChessService {
    private final String CHESS_API_BASE = "https://api.chess.com/pub/player/";
    private final RestTemplate restTemplate;
    private final ChessRepository chessRepository;

    @Autowired
    public ChessService(RestTemplate restTemplate, ChessRepository chessRepository) {
        this.restTemplate = restTemplate;
        this.chessRepository = chessRepository;
    }

    public void fetchAndInsertGames(String username, int year, int month) {
        String url = String.format("%s%s/games/%d/%02d", CHESS_API_BASE, username, year, month);

        try {
            var response = restTemplate.exchange(url, HttpMethod.GET, null, ChessApiResponse.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Failed to fetch games from Chess.com API.");
            }

            List<ChessGame> games = response.getBody().getGames();
            if (games == null || games.isEmpty()) {
                throw new ApiException(HttpStatus.NOT_FOUND, "No games found for this month.");
            }

            chessRepository.saveAll(games);

        } catch (RestClientException e) {
            throw new ApiException(HttpStatus.BAD_GATEWAY, "Error communicating with Chess.com API: " + e.getMessage());
        } catch (DataAccessException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error: " + e.getMessage());
        }
    }

    public void fetchAndInsertAllGames(String username) {
        String archivesUrl = String.format("%s%s/games/archives", CHESS_API_BASE, username);

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(archivesUrl, Map.class);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Could not fetch game archives for " + username);
            }

            List<String> archives = (List<String>) response.getBody().get("archives");
            if (archives == null || archives.isEmpty()) {
                throw new ApiException(HttpStatus.NOT_FOUND, "No archives found for user: " + username);
            }

            for (String archiveUrl : archives) {
                String[] parts = archiveUrl.split("/");
                int year = Integer.parseInt(parts[parts.length - 2]);
                int month = Integer.parseInt(parts[parts.length - 1]);
                fetchAndInsertGames(username, year, month);
            }

        } catch (RestClientException e) {
            throw new ApiException(HttpStatus.BAD_GATEWAY, "Error fetching archives from Chess.com API: " + e.getMessage());
        } catch (DataAccessException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Database error while saving games: " + e.getMessage());
        }
    }

    public List<ChessGame> getAllGamesByUser(String username) {
        try {
            List<ChessGame> games = chessRepository.findAll();
            if (games.isEmpty()) {
                throw new ApiException(HttpStatus.NOT_FOUND,"No games found for user: " + username);
            }
            return games;
        } catch (DataAccessException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Database error" + e.getMessage());
        }
    }

    public ChessGame getGameByUrl(String username, String gameUrl) {
        try {
            return chessRepository
                    .findByUrlAndPlayerUsername(gameUrl, username)
                    .orElseThrow(() -> new ApiException(
                            HttpStatus.NOT_FOUND,
                            "Game " + gameUrl + " not found for user: " + username
                    ));
        } catch (DataAccessException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
        }
    }

    public List<ChessGame> getGamesByDateRange(String username, Integer startYear, Integer endYear, Integer startMonth, Integer endMonth) {
        try {
            List<ChessGame> games = getAllGamesByUser(username);

            // If no filters provided, just return all
            if (startYear == null && endYear == null) {
                return games;
            }

            // Determine defaults from earliest/latest game if null
            long minEndTime = games.stream().mapToLong(ChessGame::getEndTime).min().orElseThrow();
            long maxEndTime = games.stream().mapToLong(ChessGame::getEndTime).max().orElseThrow();

            LocalDateTime minDate = Instant.ofEpochSecond(minEndTime).atZone(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime maxDate = Instant.ofEpochSecond(maxEndTime).atZone(ZoneId.systemDefault()).toLocalDateTime();


            // Defaults if partial input provided
            int fromYear = (startYear != null) ? startYear : minDate.getYear();
            int toYear   = (endYear   != null) ? endYear   : maxDate.getYear();
            int fromMonth = (startMonth != null) ? startMonth : 1;
            int toMonth = (endMonth != null) ? endMonth : 12;

            // Define lower and upper bounds
            LocalDateTime startDate = LocalDate.of(fromYear, fromMonth, 1).atStartOfDay();
            LocalDateTime endDate = LocalDate.of(
                    toYear,
                    toMonth,
                    YearMonth.of(toYear, toMonth).lengthOfMonth()
            ).atTime(23, 59, 59);

            List<ChessGame> filtered = games.stream()
                    .filter(game -> {
                        LocalDateTime endTime = Instant.ofEpochSecond(game.getEndTime())
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime();
                        return !endTime.isBefore(startDate) && !endTime.isAfter(endDate);
                    })
                    .toList();

            if (filtered.isEmpty()) {
                throw new ApiException(HttpStatus.NOT_FOUND,
                        String.format("No games found for %s between %s and %s", username, startDate, endDate));
            }

            return filtered;

        } catch (DataAccessException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
        }
    }
}
