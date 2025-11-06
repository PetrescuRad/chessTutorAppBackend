package com.radu.ChessTutor.mappers;

// UserMapper.java
import com.radu.ChessTutor.DTOs.UserDTO;
import com.radu.ChessTutor.entities.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserDTO dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setProfile(dto.getProfile());
        user.setUsername(dto.getUsername());
        user.setUrl(dto.getUrl());
        user.setJoined(dto.getJoined());
        user.setCountry(dto.getCountry());
        user.setLastOnline(dto.getLastOnline());
        user.setStatus(dto.getStatus());
        user.setLeague(dto.getLeague());
        user.setVerified(dto.isVerified());
        user.setStreamer(dto.isStreamer());
        return user;
    }

    public UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setProfile(user.getProfile());
        dto.setUsername(user.getUsername());
        dto.setUrl(user.getUrl());
        dto.setJoined(user.getJoined());
        dto.setCountry(user.getCountry());
        dto.setLastOnline(user.getLastOnline());
        dto.setStatus(user.getStatus());
        dto.setLeague(user.getLeague());
        dto.setVerified(user.isVerified());
        dto.setStreamer(user.isStreamer());
        return dto;
    }
}
