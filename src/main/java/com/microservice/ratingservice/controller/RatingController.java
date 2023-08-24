package com.microservice.ratingservice.controller;

import com.microservice.ratingservice.dto.RatingsRequestDto;
import com.microservice.ratingservice.dto.RatingsResponseDto;
import com.microservice.ratingservice.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${server.servlet.context-path}"+"/")
@RequiredArgsConstructor
public class RatingController {
    private final RatingService ratingService;

    @GetMapping("{userId}/{movieId}")
    public List<RatingsResponseDto> getThisRating(@PathVariable("userId") Integer userId, @PathVariable("movieId") String movieId){
        return ratingService.findThisRating(userId,movieId);
    }
    @PostMapping
    public ResponseEntity rateAndGetAvgRatingForThisMovie(@RequestBody RatingsRequestDto ratingsRequestDto){
        return ratingService.createOrUpdateRatingForThisMovie(ratingsRequestDto);
    }
}
