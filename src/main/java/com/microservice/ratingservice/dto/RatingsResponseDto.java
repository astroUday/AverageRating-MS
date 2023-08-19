package com.microservice.ratingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RatingsResponseDto {
    private Long id;
    private Integer userId;
    private String movieId;
    private double rating;
}
