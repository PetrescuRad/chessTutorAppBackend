package com.radu.ChessTutor.DTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.radu.ChessTutor.entities.ChessGame;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChessApiResponse {
    private List<ChessGame> games;

    public ChessApiResponse() {
    }

    public List<ChessGame> getGames() {
        return games;
    }

    public void setGames(List<ChessGame> games) {
        this.games = games;
    }
}
