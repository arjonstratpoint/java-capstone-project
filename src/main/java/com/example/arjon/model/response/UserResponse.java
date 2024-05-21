package com.example.arjon.model.response;

/**
 * A response object for users login
 */
public record UserResponse(
        String username,
        String role,
        String token
) {
}
