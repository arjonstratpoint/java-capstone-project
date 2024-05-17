package com.example.arjon.model.response;

public record UserResponse(
        String username,
        String role,
        String token
) {
}
