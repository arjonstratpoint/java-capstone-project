package com.example.arjon.model.request;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
        @NotBlank
        String currentPassword,
        @NotBlank
        String newPassword
) {
}
