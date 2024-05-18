package com.example.arjon.model.request;

import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordValidateRequest(
        @NotBlank
        String code,
        @NotBlank
        String password
) {
}
