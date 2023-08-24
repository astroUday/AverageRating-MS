package com.microservice.ratingservice.model;

import lombok.*;

import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
public class CustomServiceResponse<T> {
    private CompletableFuture<T> completableFuture;
    private Integer offset;

}
