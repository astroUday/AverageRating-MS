package com.microservice.ratingservice.controller;

import com.microservice.ratingservice.dto.MovieRatingsResponseDto;
import com.microservice.ratingservice.dto.RatingsRequestDto;
import com.microservice.ratingservice.service.AverageRatingService;
import com.microservice.ratingservice.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${server.servlet.context-path}"+"/movie/")
@RequiredArgsConstructor
public class MovieRatingsController {
    private final AverageRatingService ratingService;
    private final RatingService service;

    @GetMapping("{movieId}")
    public MovieRatingsResponseDto getRatingOfThisMovie(@PathVariable("movieId")String movieId){
        return ratingService.findFinalMovieRating(movieId);
    }
    @PostMapping("create")
    public ResponseEntity createMovie(@RequestParam String movieId){
        return (ratingService.addMovie(movieId))? ResponseEntity.status(HttpStatus.CREATED).body("Created"): ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please check input values correctly");
    }
    @PostMapping("test")
    public ResponseEntity testApi(@RequestBody RatingsRequestDto ratingsRequestDto) throws InterruptedException {
        return ResponseEntity.ok(service.checkIfValidRequest(ratingsRequestDto));
    }
}
