package com.microservice.ratingservice.service.mvc;

import jakarta.servlet.http.HttpServletRequest;

public class RequestContextManager {

    private static final ThreadLocal<HttpServletRequest> requestContext = new ThreadLocal<>();

    public static void setRequestContext(HttpServletRequest request) {
        requestContext.set(request);
    }

    public static HttpServletRequest getRequestContext() {
        return requestContext.get();
    }

    public static void clearRequestContext() {
        requestContext.remove();
    }
}

