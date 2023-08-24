package com.microservice.ratingservice.repo;

import com.microservice.ratingservice.dto.MovieRatingsResponseDto;
import com.microservice.ratingservice.model.AverageRatings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AveraegeRatingsRepo extends JpaRepository<AverageRatings,Long> {
    @Query("SELECT NEW com.microservice.ratingservice.dto.MovieRatingsResponseDto(r.averageRating, :b) FROM AverageRatings r WHERE r.movieId = :movieId")
    MovieRatingsResponseDto findByMovieId(@Param("movieId") String movieId, @Param("b") boolean b);

    AverageRatings findByMovieId(String movieId);

    boolean existsByMovieId(String movieId);
}
