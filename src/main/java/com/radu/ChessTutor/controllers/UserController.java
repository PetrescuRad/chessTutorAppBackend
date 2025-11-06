package com.radu.ChessTutor.controllers;

import com.radu.ChessTutor.DTOs.AuthRequest;
import com.radu.ChessTutor.DTOs.AuthResponse;
import com.radu.ChessTutor.entities.User;
import com.radu.ChessTutor.exceptions.ApiException;
import com.radu.ChessTutor.services.JwtService;
import com.radu.ChessTutor.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin("http://localhost:5173/")
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;

    @Autowired
    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUser(@RequestBody AuthRequest request, @RequestParam String username) {
        System.out.println("SIGNUP HIT");
        AuthResponse authResponse = userService.createUser(request.getEmail(), request.getPassword(), username);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody AuthRequest request) {
        AuthResponse authResponse = userService.loginUser(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(authResponse);
    }
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@RequestHeader("Authorization") String authHeader) {
        System.out.println("PROFILE HIT");
        try {
            String email = getEmailFromHeader(authHeader);

            return ResponseEntity.ok(userService.getUserByEmail(email));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: " + e.getMessage());
        }
    }

    private String getEmailFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Missing or invalid token");
        }
        String token = authHeader.substring(7);
        String email = jwtService.extractEmail(token);

        if (!jwtService.isTokenValid(token, email)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }
        return email;
    }


    @Deprecated
    @GetMapping("/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username.toLowerCase()));
    }
    @Deprecated
    @PostMapping("/{username}")
    public ResponseEntity<String> fetchAndInsertUser(@PathVariable String username) {
        userService.fetchAndInsertUser(username);
        return ResponseEntity.ok("User : " + username + " inserted successfully!" );
    }
}
