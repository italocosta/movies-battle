package com.ada.moviesbattle.repository;

import com.ada.moviesbattle.model.entity.Game;
import com.ada.moviesbattle.model.enumarator.GameStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {

    Optional<Game> findByUserAndStatus(String user, GameStatus status);

    List<Game> findTop10ByStatusOrderByFinalScoreDesc(GameStatus status);
}
