package com.example.arjon.model.request;

import jakarta.validation.constraints.NotBlank;

public record UserRequest(
        @NotBlank
        String userName,
        @NotBlank
        String password,
        @NotBlank
        String email
) {
}
