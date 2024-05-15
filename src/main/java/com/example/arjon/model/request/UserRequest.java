package com.example.arjon.model.request;

import jakarta.validation.constraints.NotBlank;

public record UserRequest(
        @NotBlank
        String username,
        @NotBlank
        String password
) {
}
