package com.microservice.ratingservice.filters;

import com.microservice.ratingservice.model.ApiResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.microservice.ratingservice.config.ObjectMapperConfig.objectMapper;
import static com.microservice.ratingservice.config.WebclientConfig.webclient;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

public class CustomAuthorizationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(request.getServletPath().equals("/api/user/login")||request.getServletPath().equals("/api/user") || request.getServletPath().contains("actuator")
        || request.getServletPath().contains("/")) {
            filterChain.doFilter(request, response);
            return;
        }
        String authorization = request.getHeader("Authorization");
        if(authorization==null||authorization.length()<8){
            Map<String,String> error=new HashMap<>();
            error.put("error_message","Please provide valid token");
            response.setContentType(APPLICATION_JSON_VALUE);
             objectMapper().writeValue(response.getOutputStream(),error);
            return;
        }
        ApiResponse apiResponse = webclient().build().get().uri("http://user-service/api/user/token")
                .header("Authorization",authorization)
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .block();


        if(apiResponse.getHttpStatus()!= HttpStatus.valueOf(200)){
            Map<String,String> error=new HashMap<>();
            error.put("error_message","Please provide valid token");
            response.setContentType(APPLICATION_JSON_VALUE);
            objectMapper().writeValue(response.getOutputStream(),error);
            return;
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(apiResponse.getData(),  null, authorities);

        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        filterChain.doFilter(request,response);
    }
}
