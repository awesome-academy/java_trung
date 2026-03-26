package com.example.foodsdrinks.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SocialLoginRequest(
        @NotBlank(message = "{validation.social.provider.required}") String provider,
        @NotBlank(message = "{validation.social.code.required}")    String code
) {}
