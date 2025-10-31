package com.radu.ChessTutor.DTOs;

import java.util.UUID;

public class PlayerDTO {
    private int rating;
    private String result;
    private String playerId; // corresponds to "@id" in JSON
    private String username;
    private UUID uuid;
}
