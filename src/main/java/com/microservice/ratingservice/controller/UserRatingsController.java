package com.microservice.ratingservice.controller;

import com.microservice.ratingservice.dto.UserRatingsResponseDto;
import com.microservice.ratingservice.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${server.servlet.context-path}"+"/user/")
@RequiredArgsConstructor
public class UserRatingsController {

    private final RatingService ratingService;
    @GetMapping("{userId}")
    public List<UserRatingsResponseDto> getAllRatingsOfUser(@PathVariable("userId") Integer userId){
        return ratingService.findAllRatingsOfUser(userId);
    }
}
