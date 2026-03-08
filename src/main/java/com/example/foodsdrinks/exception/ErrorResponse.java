package com.example.foodsdrinks.exception;

import com.example.foodsdrinks.config.MessageHelper;
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

    public static ErrorResponse of(ErrorCode code, MessageHelper messageHelper) {
        return ErrorResponse.builder()
                .status(code.getStatus().value())
                .error(code.getStatus().getReasonPhrase())
                .message(messageHelper.get(code.getMessageKey()))
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ErrorResponse of(ErrorCode code, MessageHelper messageHelper, Object... args) {
        return ErrorResponse.builder()
                .status(code.getStatus().value())
                .error(code.getStatus().getReasonPhrase())
                .message(messageHelper.get(code.getMessageKey(), args))
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ErrorResponse of(ErrorCode code, Map<String, String> fieldErrors) {
        return ErrorResponse.builder()
                .status(code.getStatus().value())
                .error(code.getStatus().getReasonPhrase())
                .timestamp(LocalDateTime.now())
                .fieldErrors(fieldErrors)
                .build();
    }
}
