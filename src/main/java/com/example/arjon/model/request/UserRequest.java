package com.example.arjon.model.request;

import jakarta.validation.constraints.NotBlank;

/**
 * A request object for user login and registration
 */
public record UserRequest(
        @NotBlank
        String username,
        @NotBlank
        String password
) {
}
