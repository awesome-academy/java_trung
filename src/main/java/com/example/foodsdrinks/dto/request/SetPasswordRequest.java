package com.example.foodsdrinks.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for setting or changing a password.
 *
 * <ul>
 *   <li>If the user is social-only (no existing password), {@code oldPassword} must be {@code null}.</li>
 *   <li>If the user already has a password, {@code oldPassword} is required.</li>
 * </ul>
 *
 * @param oldPassword current password – {@code null} for social-only accounts
 * @param newPassword the desired new password (6–100 chars)
 */
public record SetPasswordRequest(
        String oldPassword,
        @NotBlank(message = "validation.password.required")
        @Size(min = 6, max = 100, message = "validation.password.size")
        String newPassword
) {}
