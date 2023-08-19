package com.microservice.ratingservice.repo;

import com.microservice.ratingservice.dto.RatingsResponseDto;
import com.microservice.ratingservice.dto.UserRatingsResponseDto;
import com.microservice.ratingservice.model.Ratings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RatingRepo extends JpaRepository<Ratings,Long> {
    @Query("SELECT NEW com.microservice.ratingservice.dto.UserRatingsResponseDto(r.id, r.movieId, r.rating) FROM Ratings r WHERE r.userId = :userId")
    List<UserRatingsResponseDto> findAllByUserId(Integer userId);

    @Query("SELECT NEW com.microservice.ratingservice.dto.RatingsResponseDto(r.id, r.userId, r.movieId, r.rating) FROM Ratings r WHERE r.userId = :userId AND r.movieId= :movieId")
    List<RatingsResponseDto> findByUserIdAndMovieId(Integer userId, String movieId);
}
