package com.microservice.ratingservice.service;

import com.microservice.ratingservice.dto.MovieRatingsResponseDto;
import com.microservice.ratingservice.model.AverageRatings;
import com.microservice.ratingservice.repo.AveraegeRatingsRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AverageRatingService {
    private final AveraegeRatingsRepo averageRatingsRepo;
    public MovieRatingsResponseDto findFinalMovieRating(String movieId){
        return averageRatingsRepo.findByMovieId(movieId);
    }

    public boolean addMovie(String movieId) {
        if(!averageRatingsRepo.existsByMovieId(movieId)){
            AverageRatings averageRatings=new AverageRatings();
            averageRatings.setMovieId(movieId);
            averageRatingsRepo.save(averageRatings);
            return true;
        }
        return false;
    }
}
