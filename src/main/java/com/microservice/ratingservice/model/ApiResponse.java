package com.microservice.ratingservice.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@RequiredArgsConstructor
public class ApiResponse {

    private HttpStatus httpStatus;
    private  Object data;
    private  String message;

}