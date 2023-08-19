package com.microservice.ratingservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(indexes = {
        @Index(name = "indexedColumns", columnList = "userId, movieId")
})
public class Ratings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Min(value = 0, message = "Rating must be at least 0")
    @Max(value = 5, message = "Rating must be at most 5")
    private double rating;
    @NotNull(message = "Movie cannot be null for this entity")
    private String movieId;
    @NotNull(message = "User cannot be null for this entity")
    private Integer userId;
}
