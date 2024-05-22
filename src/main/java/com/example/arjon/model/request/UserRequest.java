package com.example.arjon.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import static com.example.arjon.util.Constant.INVALID_PASSWORD_ERROR_MESSAGE;
import static com.example.arjon.util.Constant.PASSWORD_REGEX;

/**
 * A request object for user login and registration
 */
public record UserRequest(
        @NotBlank
        String username,
        @NotBlank
        @Pattern(regexp = PASSWORD_REGEX, message = INVALID_PASSWORD_ERROR_MESSAGE)
        String password
) {
}
