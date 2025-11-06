package com.radu.ChessTutor.repositories;

import com.radu.ChessTutor.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> getUserByUsername(String username);

    Optional<User> getUserByEmail(String email);
}
