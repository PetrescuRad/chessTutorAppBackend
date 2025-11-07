package com.radu.ChessTutor.repositories;

import com.radu.ChessTutor.entities.ChessGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChessRepository extends JpaRepository<ChessGame, String> {

    @Query("""
       SELECT g FROM ChessGame g 
       WHERE g.url = :url 
       AND (LOWER(g.white.username) = LOWER(:username) 
            OR LOWER(g.black.username) = LOWER(:username))
       """)
    Optional<ChessGame> findByUrlAndPlayerUsername(@Param("url") String url, @Param("username") String username);

}
