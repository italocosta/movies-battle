package com.ada.moviesbattle.model.entity;

import com.ada.moviesbattle.model.enumarator.GameStatus;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String user;
    private LocalDateTime creationTime;
    private LocalDateTime lastUpdate;
    private GameStatus status;
    private Float finalScore;

    @OneToMany(mappedBy = "game")
    private Set<Quiz> quizzes;
}
