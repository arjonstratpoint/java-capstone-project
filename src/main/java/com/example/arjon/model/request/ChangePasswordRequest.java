package com.example.arjon.model.request;

import jakarta.validation.constraints.NotBlank;

/**
 * A request object for changing password
 */
public record ChangePasswordRequest(
        @NotBlank
        String currentPassword,
        @NotBlank
        String newPassword
) {
}
