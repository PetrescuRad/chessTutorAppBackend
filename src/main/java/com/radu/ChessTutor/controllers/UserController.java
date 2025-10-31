package com.radu.ChessTutor.controllers;

import com.radu.ChessTutor.entities.User;
import com.radu.ChessTutor.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/{username}")
    public ResponseEntity<String> fetchAndInsertUser(@PathVariable String username) {
        userService.fetchAndInsertUser(username);
        return ResponseEntity.ok("User : " + username + " inserted successfully!" );
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username.toLowerCase()));
    }
}
