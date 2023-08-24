package com.microservice.ratingservice.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.microservice.ratingservice.dto.RatingsRequestDto;
import com.microservice.ratingservice.dto.RatingsResponseDto;
import com.microservice.ratingservice.dto.UserRatingsResponseDto;
import com.microservice.ratingservice.model.Ratings;
import com.microservice.ratingservice.repo.AveraegeRatingsRepo;
import com.microservice.ratingservice.repo.RatingRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.microservice.ratingservice.config.WebclientConfig.webclient;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingRepo ratingRepo;
    private final AveraegeRatingsRepo averaegeRatingsRepo;
    private static final String X="This request cannot be processed due to bad input values";

    ObjectMapper objectMapper=new ObjectMapper();

    public List<UserRatingsResponseDto> findAllRatingsOfUser(Integer userId){
        return ratingRepo.findAllByUserId(userId);
    }

    public List<RatingsResponseDto> findThisRating(Integer userId, String movieId) {
        return ratingRepo.findByUserIdAndMovieId(userId,movieId);
    }

    public ResponseEntity<?> createOrUpdateRatingForThisMovie(RatingsRequestDto ratingsRequestDto) {
        if(averaegeRatingsRepo.existsByMovieId(ratingsRequestDto.getMovieId())) {

//checking if this is for creating a new rating
            if (ratingsRequestDto.getId() != null) {
                // if this really exists before in my data
                if (ratingRepo.existsById(ratingsRequestDto.getId())) {

                    return ResponseEntity.status(HttpStatus.CREATED).body(updateRatingForThisMovie(ratingsRequestDto)); // UPDATED
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(X);
                }
            } else {
                Ratings ratings = objectMapper.convertValue(ratingsRequestDto, Ratings.class);
                ratingRepo.save(ratings);
                return ResponseEntity.status(HttpStatus.CREATED).body(ratings); // CREATED
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(X); //return response entity
    }

    public Mono<Boolean> checkIfValidRequest(RatingsRequestDto ratingsRequestDto) {
        if (ratingsRequestDto.getMovieId()==null && ratingsRequestDto.getUserId()==null && ratingsRequestDto.getRating()==null){
            //return responseEntity
        }
         return fetchDataFromService("http://MOVIENEST/","api/get-film/"+ratingsRequestDto.getMovieId(),Boolean.class)
                .flatMap(res ->{
                    return Mono.just(res);
                }).onErrorResume(WebClientRequestException.class,error -> {
                    // or, throw exception
                     return Mono.just(false);
                }).onErrorResume(WebClientResponseException.class,error ->{
                     // or, throw exception
                     return Mono.just(false);
                });
    }

    private Ratings updateRatingForThisMovie(RatingsRequestDto ratingsRequestDto) {
        objectMapper.registerModule(new JavaTimeModule());

        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        Ratings ratings=objectMapper.convertValue(ratingsRequestDto, Ratings.class);

        ratingRepo.save(ratings);
        return ratings;
    }

    public static <T> Mono<T> fetchDataFromService(String baseUrl, String uri, Class<T> responseType) {
        return webclient()
                .baseUrl(baseUrl)
                .build()
                .get()
                .uri(uri)
                .retrieve()
                .bodyToMono(responseType);
    }
}
