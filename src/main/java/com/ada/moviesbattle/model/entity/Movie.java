package com.ada.moviesbattle.model.entity;

import com.ada.moviesbattle.model.api.OMDbMovie;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded  = true)
@Entity
public class Movie {

    @EqualsAndHashCode.Include
    @Id
    private String id;
    private String title;
    private double rating;
    private int votesAmount;

    public static Movie of(OMDbMovie omDbMovie) {
        Movie movie = new Movie();
        movie.setId(omDbMovie.getImdbId());
        movie.setTitle(omDbMovie.getTitle());
        movie.setRating(Double.parseDouble(omDbMovie.getImdbRating()));
        movie.setVotesAmount(Integer.parseInt(omDbMovie.getImdbVotes().replace(",","")));
        return movie;
    }
}
