package com.example.arjon.model.request;

import jakarta.validation.constraints.NotBlank;

/**
 * A request object for validating forgot password request
 */
public record ForgotPasswordValidateRequest(
        @NotBlank
        String code,
        @NotBlank
        String password
) {
}
