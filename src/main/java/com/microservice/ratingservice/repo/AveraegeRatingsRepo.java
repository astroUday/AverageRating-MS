package com.microservice.ratingservice.repo;

import com.microservice.ratingservice.dto.MovieRatingsResponseDto;
import com.microservice.ratingservice.model.AverageRatings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AveraegeRatingsRepo extends JpaRepository<AverageRatings,Long> {
    @Query("SELECT NEW com.microservice.ratingservice.dto.MovieRatingsResponseDto(r.averageRating) FROM AverageRatings r WHERE r.movieId = :movieId")
    MovieRatingsResponseDto findByMovieId(String movieId);

    boolean existsByMovieId(String movieId);
}
