package com.microservice.ratingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRatingsResponseDto {
    private Long id;
    private String movieId;
    private double rating;
}
