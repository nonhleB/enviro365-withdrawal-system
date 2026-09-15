package com.enviro.assessment.junior.nonhle.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Consistent shape for every error response the API returns, instead of
 * Spring's default generic error body. Makes the frontend's error handling
 * simple - it always knows where to find the message.
 */
@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
