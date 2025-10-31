package com.radu.ChessTutor.services;

import com.radu.ChessTutor.DTOs.UserDTO;
import com.radu.ChessTutor.entities.User;
import com.radu.ChessTutor.exceptions.ApiException;
import com.radu.ChessTutor.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class UserService {
    private final String USER_BASE_API = "https://api.chess.com/pub/player/";
    private RestTemplate restTemplate;
    private final  UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
    }

    public void fetchAndInsertUser(String username) {
        try {
            ResponseEntity<User> response = restTemplate.exchange(USER_BASE_API + username, HttpMethod.GET, null, User.class);
            System.out.println(response);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Failed to fetch user from Chess.com API.");
            }
            User user = response.getBody();
            
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
}
