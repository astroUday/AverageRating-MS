package com.microservice.ratingservice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RatingsRequestDto {
    private Long id;
    @Min(value = 0, message = "Rating must be at least 0")
    @Max(value = 5, message = "Rating must be at most 5")
    private Double rating;
    @NotNull(message = "Bad request")
    private String movieId;
    @NotNull(message = "Bad request")
    private Integer userId;
}
