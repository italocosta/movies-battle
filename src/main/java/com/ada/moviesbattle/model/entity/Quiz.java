package com.ada.moviesbattle.model.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = true)
    private Integer answer;
    private Boolean correct;

    @ManyToMany(targetEntity = Movie.class)
    private List<Movie> movies;

    @ManyToOne(optional = false)
    private Game game;


}
