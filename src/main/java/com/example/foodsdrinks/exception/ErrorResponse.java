package com.example.foodsdrinks.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private final int status;
    private final String error;
    private final String message;
    private final LocalDateTime timestamp;
    private final Map<String, String> fieldErrors;

    public static ErrorResponse of(ErrorCode code) {
        return ErrorResponse.builder()
                .status(code.getStatus().value())
                .error(code.getStatus().getReasonPhrase())
                .message(code.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ErrorResponse of(ErrorCode code, String customMessage) {
        return ErrorResponse.builder()
                .status(code.getStatus().value())
                .error(code.getStatus().getReasonPhrase())
                .message(customMessage)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ErrorResponse of(ErrorCode code, Map<String, String> fieldErrors) {
        return ErrorResponse.builder()
                .status(code.getStatus().value())
                .error(code.getStatus().getReasonPhrase())
                .message(code.getMessage())
                .timestamp(LocalDateTime.now())
                .fieldErrors(fieldErrors)
                .build();
    }
}
