package com.radu.ChessTutor.services;

import com.radu.ChessTutor.DTOs.AuthResponse;
import com.radu.ChessTutor.DTOs.UserDTO;
import com.radu.ChessTutor.entities.User;
import com.radu.ChessTutor.exceptions.ApiException;
import com.radu.ChessTutor.mappers.UserMapper;
import com.radu.ChessTutor.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class UserService {
    private final String USER_BASE_API = "https://api.chess.com/pub/player/";
    private final RestTemplate restTemplate;
    private final  UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    @Autowired
    public UserService(UserRepository userRepository,
                       RestTemplate restTemplate,
                       UserMapper userMapper,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }
    public void fetchAndInsertUser(String username) {
        try {
            ResponseEntity<UserDTO> response = restTemplate.exchange(USER_BASE_API + username, HttpMethod.GET, null, UserDTO.class);
            System.out.println(response);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Failed to fetch user from Chess.com API.");
            }
            User user = userMapper.toEntity(response.getBody());
            
            // insert into db
            userRepository.save(user);

        } catch (RestClientException e) {
            throw new ApiException(HttpStatus.BAD_GATEWAY, "Error communicating with Chess.com API: " + e.getMessage());
        } catch (DataAccessException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error: " + e.getMessage());
        }
    }

    public User getUserByUsername(String username) {
        try {
            Optional<User> user = userRepository.getUserByUsername(username);

            if (user.isPresent()) {
                return user.get();
            }
            throw new ApiException(HttpStatus.NOT_FOUND, "User : " + username + " not found.");
        } catch (DataAccessException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Database error:" + e.getMessage());
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error: " + e.getMessage());
        }
    }

    public AuthResponse createUser(String email, String password, String username) {
        // Check if user exists
        if (userRepository.existsById(email)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "User with that email already exists!");
        }

        try {
            ResponseEntity<UserDTO> response = restTemplate.exchange(USER_BASE_API + username, HttpMethod.GET, null, UserDTO.class);
            System.out.println(response.getBody().toString());
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Failed to fetch user from Chess.com API.");
            }
            // Complete user
            User user = userMapper.toEntity(response.getBody());
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));

            // insert into db
            userRepository.save(user);
            return new AuthResponse(jwtService.generateToken(email), user.getUsername());

        } catch (RestClientException e) {
            throw new ApiException(HttpStatus.BAD_GATEWAY, "Error communicating with Chess.com API: " + e.getMessage());
        } catch (DataAccessException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error: " + e.getMessage());
        }
    }

    public AuthResponse loginUser(String email, String password) {
        User user = userRepository.findById(email)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Invalid email or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid email or password");
        }

        return new AuthResponse(jwtService.generateToken(email), user.getUsername());
    }

    public UserDTO getUserByEmail(String email) {
        try {
            Optional<User> user = userRepository.getUserByEmail(email);
            if (user.isPresent()) {
                return userMapper.toDTO(user.get());
            }
            throw new ApiException(HttpStatus.NOT_FOUND, "User : " + email + " not found.");
        } catch (DataAccessException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Database error:" + e.getMessage());
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error: " + e.getMessage());
        }
    }
}
