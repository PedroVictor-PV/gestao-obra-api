package com.example.gestaoobraapi.exception;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class FilterExceptionHandler {
       private final ObjectMapper objectMapper;

    public void handle(HttpServletResponse response, HttpServletRequest request,
                       int status, String error, String message) throws IOException {
        StandardError standardError = new StandardError(
                LocalDateTime.now(),
                status,
                error,
                message,
                request.getRequestURI()
        );
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(standardError));
    }
}

